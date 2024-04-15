package me.qeklydev.scoreboard;

import java.util.List;
import me.qeklydev.scoreboard.config.Configuration;
import me.qeklydev.scoreboard.config.ConfigurationProvider;
import me.qeklydev.scoreboard.manager.ScoreboardManager;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.impl.ScoreboardUpdaterThreadModelImpl;
import me.qeklydev.scoreboard.thread.impl.TitleUpdaterThreadModelImpl;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardPlugin extends JavaPlugin implements ApiModel {
  private ComponentLogger logger;
  private ConfigurationProvider<Configuration> configProvider;
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
    if (this.configProvider == null) {
      throw new IllegalStateException("Configuration have not been loaded correctly.");
    }
    final var config = this.configProvider.get();
    this.scoreboardRepository = new ScoreboardModelRepository();
    this.scoreboardManager = new ScoreboardManager(this.logger, this.scoreboardRepository);
    this.scoreboardUpdaterThreadModel = new ScoreboardUpdaterThreadModelImpl(this.scoreboardRepository);
    this.titleUpdaterThreadModel = new TitleUpdaterThreadModelImpl(this.scoreboardRepository);
  }

  @Override
  public void onEnable() {
    /*
     * If configuration have not been loaded correctly
     * during on-load process, skip on-enable method execution.
     */
    if (this.configProvider == null) {
      return;
    }
    ApiProvider.load(this);
    /*
     * If scoreboard-manager has suffered some error
     * during loading, stop method execution.
     */
    if (!this.scoreboardManager.load(this)) {
      this.logger.error("Something went wrong during scoreboard-manager start-up process.");
      return;
    }
    final var config = this.configProvider.get();
    /*
     * Define update-rate values for the scoreboard content
     * and title updater executors.
     */
    this.scoreboardUpdaterThreadModel.periodRate(config.scoreboardFrameUpdateRate);
    this.titleUpdaterThreadModel.periodRate(config.scoreboardTitleUpdateRate);
    this.scoreboardManager.scheduleWithProvidedExecutors(List.of(
        this.scoreboardUpdaterThreadModel, this.titleUpdaterThreadModel));
  }

  @Override
  public void onDisable() {
    ApiProvider.unload();
    if (this.scoreboardManager != null) {
      this.scoreboardManager.shutdown();
    }
  }
}
