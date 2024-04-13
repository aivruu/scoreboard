package me.qeklydev.scoreboard;

import me.qeklydev.scoreboard.config.Configuration;
import me.qeklydev.scoreboard.config.ConfigurationProvider;
import me.qeklydev.scoreboard.manager.ScoreboardManager;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.ScoreboardUpdatingExecutorThread;
import me.qeklydev.scoreboard.utils.ComponentUtils;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardPlugin extends JavaPlugin implements ApiModel {
  private ComponentLogger logger;
  private ConfigurationProvider<Configuration> configProvider;
  private ScoreboardModelRepository scoreboardRepository;
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
    this.logger = super.getComponentLogger();
    this.scoreboardRepository = new ScoreboardModelRepository();
    this.scoreboardManager = new ScoreboardManager(
        this.logger,
        this.scoreboardRepository,
        new ScoreboardUpdatingExecutorThread(this.scoreboardRepository, ));
  }

  @Override
  public void onEnable() {
    ApiProvider.load(this);
    this.scoreboardManager.load(this);
  }

  @Override
  public void onDisable() {
    ApiProvider.unload();
    if (this.scoreboardManager != null) {
      this.scoreboardManager.shutdown();
    }
  }
}
