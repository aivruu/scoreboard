package me.qeklydev.scoreboard.thread.impl;

import java.util.List;
import java.util.concurrent.Executors;
import me.qeklydev.scoreboard.config.Configuration;
import me.qeklydev.scoreboard.config.ConfigurationProvider;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.CustomExecutorThreadModel;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import me.qeklydev.scoreboard.component.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles the updating content process for
 * the active scoreboards
 *
 * @since 0.0.1
 */
public final class ScoreboardUpdaterThreadModelImpl extends CustomExecutorThreadModel {
  private final ConfigurationProvider<Configuration> configProvider;
  private final ComponentLogger logger;

  public ScoreboardUpdaterThreadModelImpl(final @NotNull ScoreboardModelRepository scoreboardRepository,
                                          final @NotNull ConfigurationProvider<@NotNull Configuration> configProvider,
                                          final @NotNull ComponentLogger logger) {
    super(
        Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "ScoreboardUpdaterExecutor")),
        scoreboardRepository);
    this.configProvider = configProvider;
    this.logger = logger;
  }

  @Override
  public void run() {
    final var config = this.configProvider.get();
    switch (config.scoreboardMode) {
      case "SINGLE" -> this.processOnSingleMode(config);
      case "WORLD" -> this.processOnWorldMode(config);
      default -> {
        /*
         * If is the case that the specified scoreboard-mode isn't
         * valid, the scoreboard-updater-thread will be shutdown.
         */
        this.logger.error("Unknown detected mode for the scoreboard, shutting down 'ScoreboardUpdaterExecutor' thread.");
        final var shutdownResult = super.shutdown();
        if (shutdownResult.failed()) {
          this.logger.warn("Incorrectly shutdown on current CustomExecutorThreadModel.");
        }
        this.logger.info("Shutdown result for 'ScoreboardUpdatingExecutor' thread is: {}", shutdownResult);
      }
    }
  }

  /**
   * Process the scoreboards based on the 'SINGLE' mode.
   *
   * @param config the configuration model.
   * @since 0.0.1
   */
  private void processOnSingleMode(final @NotNull Configuration config) {
    final var componentsLines = ComponentUtils.ofMany(config.content);
    for (final var scoreboardModel : super.scoreboardRepository.scoreboards()) {
      /*
       * If visibility status for the scoreboard is 'CLOSED',
       * skip this iteration.
       */
      if (scoreboardModel.toggleState() == ScoreboardToggleStateType.CLOSED) {
        continue;
      }
      var lineIndex = 0;
      for (final var line : componentsLines) {
        /*
         * Establish the current content list index as the current line for
         * the scoreboard content.
         */
        scoreboardModel.updateLine(lineIndex, line);
        lineIndex++;
      }
    }
  }

  /**
   * Process the scoreboards based on the 'WORLD' mode
   * defined in the configuration.
   *
   * @param config the configuration model.
   * @since 0.0.1
   */
  private void processOnWorldMode(final @NotNull Configuration config) {
    for (final var scoreboardModel : super.scoreboardRepository.scoreboards()) {
      List<Component> content = null;
      for (final var section : config.scoreboardForWorlds) {
        final var worldName = section.targetedWorld;
        /*
         * Checks if the world specified exists and is loaded
         * on the server, or if the player world name is equals
         * than the currently iterated.
         */
        if ((Bukkit.getWorld(worldName) == null) || !scoreboardModel.ofUid()
            .getWorld()
            .getName()
            .equals(worldName)) {
          continue;
        }
        content = ComponentUtils.ofMany(section.content);
        break;
      }
      /*
       * If content for the scoreboard was not provided due
       * to any reason, process the next iteration.
       */
      if (content == null) {
        continue;
      }
      /*
       * If visibility status for the scoreboard is 'CLOSED',
       * skip this iteration.
       */
      if (scoreboardModel.toggleState() == ScoreboardToggleStateType.CLOSED) {
        continue;
      }
      var lineIndex = 0;
      for (final var line : content) {
        /*
         * Establish the current content list index as the current line for
         * the scoreboard content.
         */
        scoreboardModel.updateLine(lineIndex, line);
        lineIndex++;
      }
    }
  }
}
