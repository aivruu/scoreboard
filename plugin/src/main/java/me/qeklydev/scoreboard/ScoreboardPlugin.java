package me.qeklydev.scoreboard;

import java.util.List;
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
    /*
     * Check if configurations was loaded correctly before
     * continue with loading process.
     */
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
    /*
     * If configurations have not been loaded correctly
     * during on-load process, skip on-enable method execution.
     */
    if (this.configProvider == null || this.messagesProvider == null) {
      return;
    }
    ApiProvider.load(this);
    /*
     * If scoreboard-manager has suffered some error
     * during loading, stop method execution.
     */
    if (!this.scoreboardManager.load(this)) {
      return;
    }
    super.getServer().getPluginManager().registerEvents(new ScoreboardListener(this.scoreboardManager, this.logger), this);
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
