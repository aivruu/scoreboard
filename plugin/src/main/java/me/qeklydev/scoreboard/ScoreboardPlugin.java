/*
 * This file is part of scoreboard - https://github.com/aivruu/scoreboard
 * Copyright (C) 2020-2024 aivruu (https://github.com/aivruu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.qeklydev.scoreboard;

import me.qeklydev.scoreboard.config.Configuration;
import me.qeklydev.scoreboard.config.ConfigurationProvider;
import me.qeklydev.scoreboard.config.Messages;
import me.qeklydev.scoreboard.listener.ScoreboardListener;
import me.qeklydev.scoreboard.manager.ScoreboardManager;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.impl.ScoreboardUpdaterThreadModelImpl;
import me.qeklydev.scoreboard.thread.impl.TitleUpdaterThreadModelImpl;
import me.qeklydev.scoreboard.component.ComponentUtils;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardPlugin extends JavaPlugin implements ApiModel {
  public static final String VERSION = "{version}"; // Replaced with blossom during project compilation.
  private ComponentLogger logger;
  private ConfigurationProvider<Configuration> configProvider;
  private ConfigurationProvider<Messages> messagesProvider;
  private ScoreboardModelRepository scoreboardRepository;
  private ScoreboardUpdaterThreadModelImpl scoreboardUpdaterThreadModel;
  private TitleUpdaterThreadModelImpl titleUpdaterThreadModel;
  private ScoreboardManager scoreboardManager;

  @Override
  public @NotNull ScoreboardModelRepository scoreboardRepository() {
    if (this.scoreboardRepository == null) {
      throw new IllegalStateException("The scoreboard model repository reference is not initialized.");
    }
    return this.scoreboardRepository;
  }

  @Override
  public @NotNull ScoreboardManager scoreboardManager() {
    if (this.scoreboardManager == null) {
      throw new IllegalStateException("The scoreboard manager reference is not initialized.");
    }
    return this.scoreboardManager;
  }

  @Override
  public void onLoad() {
    final var directory = super.getDataFolder().toPath();
    this.logger = super.getComponentLogger();
    this.configProvider = ConfigurationProvider.of(directory, "config", Configuration.class);
    this.messagesProvider = ConfigurationProvider.of(directory, "messages", Messages.class);
    // Check if configurations was loaded correctly before
    // continue with loading process.
    if (this.configProvider == null || this.messagesProvider == null) {
      this.logger.error("Configurations have not been loaded correctly, check them for any syntax error.");
    }
    this.scoreboardRepository = new ScoreboardModelRepository();
    this.scoreboardManager = new ScoreboardManager(this.logger, this.scoreboardRepository, this.configProvider);
    this.scoreboardUpdaterThreadModel = new ScoreboardUpdaterThreadModelImpl(
        this.scoreboardRepository, this.configProvider, this.logger);
    this.titleUpdaterThreadModel = new TitleUpdaterThreadModelImpl(
        this.scoreboardRepository, ComponentUtils.ofMany(this.configProvider.get().titleContent));
  }

  @Override
  public void onEnable() {
    // If configurations have not been loaded correctly
    // during on-load process, skip on-enable method execution.
    if (this.configProvider == null || this.messagesProvider == null) {
      return;
    }
    ApiProvider.load(this);
    // If the scoreboard-manager could not be loaded correctly,
    // we stop all loading process for the entire plugin.
    if (!this.scoreboardManager.load(this)) {
      return;
    }
    super.getServer().getPluginManager().registerEvents(new ScoreboardListener(this.scoreboardManager, this.logger), this);
    final var config = this.configProvider.get();
    // Define update-rate values for the scoreboard content and title
    // updater executors.
    this.scoreboardUpdaterThreadModel.periodRate(config.scoreboardFrameUpdateRate);
    this.scoreboardManager.scheduleWithProvidedExecutor(this.scoreboardUpdaterThreadModel);
    // If animated-title option is enabled, set period-rate for the
    // title-updater and start it.
    if (config.useScoreboardAnimatedTitle) {
      this.titleUpdaterThreadModel.periodRate(config.scoreboardTitleUpdateRate);
      this.scoreboardManager.scheduleWithProvidedExecutor(this.titleUpdaterThreadModel);
    }
  }

  /**
   * Uses the reloaded configuration model to update the
   * period-rate for executor thread models, and title-animation
   * content.
   *
   * @return The boolean state for this operation, {@code true} if
   *     the executors period-rate and title-animation content were updated.
   *     Otherwise {@code false}.
   * @since 0.0.1
   */
  public boolean reload() {
    final var config = this.configProvider.get();
    // Check if new provided values for update-rate for
    // scoreboard lines, and title-animation is zero, or
    // negative.
    if (config.scoreboardTitleUpdateRate <= 0 || config.scoreboardFrameUpdateRate <= 0) {
      return false;
    }
    // Check the scoreboard-mode, for cases where the case
    // are 'SINGLE' OR 'WORLD', we update the update-rate
    // values for the threads and, update the title content.
    return switch (config.scoreboardMode) {
      case "SINGLE", "WORLD" -> {
        this.scoreboardUpdaterThreadModel.periodRate(config.scoreboardFrameUpdateRate);
        this.titleUpdaterThreadModel.periodRate(config.scoreboardTitleUpdateRate);
        this.titleUpdaterThreadModel.content(ComponentUtils.ofMany(config.titleContent));
        yield true;
      }
      // Scoreboard-mode defined isn't valid.
      default -> false;
    };
  }

  @Override
  public void onDisable() {
    ApiProvider.unload();
    if (this.scoreboardManager != null) {
      this.scoreboardManager.shutdown();
    }
  }
}
