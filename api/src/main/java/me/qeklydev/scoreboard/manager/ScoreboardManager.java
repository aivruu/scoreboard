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
package me.qeklydev.scoreboard.manager;

import java.util.List;
import me.qeklydev.scoreboard.cache.CachedScoreboardModel;
import me.qeklydev.scoreboard.component.ComponentUtils;
import me.qeklydev.scoreboard.config.Configuration;
import me.qeklydev.scoreboard.config.ConfigurationProvider;
import me.qeklydev.scoreboard.event.ScoreboardCloseEvent;
import me.qeklydev.scoreboard.event.ScoreboardCreateEvent;
import me.qeklydev.scoreboard.event.ScoreboardToggleEvent;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.CustomExecutorThreadModel;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used for handle start-up and shutdown
 * of the scoreboard components, handling of currently
 * running {@link CustomExecutorThreadModel} implementations,
 * and creation, toggling or deletion of active scoreboards.
 *
 * @since 0.0.1
 */
public final class ScoreboardManager {
  public static final byte FIRST_POSSIBLE_TOGGLE_RESULT = 0;
  public static final byte TOGGLE_ENABLE_RESULT = 1;
  public static final byte TOGGLE_DISABLE_RESULT = 2;
  private final ComponentLogger logger;
  private final ScoreboardModelRepository repository;
  private final ConfigurationProvider<Configuration> configProvider;
  private ScoreboardLibrary scoreboardLibrary;
  @SuppressWarnings("unused")
  private List<CustomExecutorThreadModel> customExecutorModels;

  public ScoreboardManager(final @NotNull ComponentLogger logger, final @NotNull ScoreboardModelRepository repository,
                           final @NotNull ConfigurationProvider<@NotNull Configuration> configProvider) {
    this.repository = repository;
    this.logger = logger;
    this.configProvider = configProvider;
  }

  /**
   * Boots up the scoreboard library component and perform
   * defined scoreboard-mode check for validation.
   *
   * @param plugin a {@link JavaPlugin} instance.
   * @return The boolean state for this operation, {@code true}
   *     if library component was loaded correctly, and
   *     scoreboard-mode validation was correct. Otherwise {@code false}.
   * @since 0.0.1
   */
  public boolean load(final @NotNull JavaPlugin plugin) {
    try {
      this.scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
      this.logger.info("Loaded scoreboard library correctly.");
    } catch (final NoPacketAdapterAvailableException exception) {
      this.logger.error("No packet adapter was founded for the scoreboard library", exception);
      return false;
    }
    this.logger.info("Checking for valid scoreboard-mode.");
    final var scoreboardMode = this.configProvider.get().scoreboardMode;
    return switch (scoreboardMode) {
      // Valid scoreboard modes ->
      case "SINGLE", "WORLD" -> {
        this.logger.info("Detected '{}' scoreboard mode as valid.", scoreboardMode);
        yield true;
      }
      // Any different scoreboard mode.
      default -> {
        this.logger.error("-> '{}' is not valid as a scoreboard-mode in the configuration.", scoreboardMode);
        yield false;
      }
    };
  }

  /**
   * Uses the given executor thread model list to provide it
   * to the custom executor model list.
   *
   * @param providedExecutorModels the {@link CustomExecutorThreadModel} list.
   * @since 0.0.1
   */
  public void scheduleWithProvidedExecutors(final @NotNull List<@NotNull CustomExecutorThreadModel> providedExecutorModels) {
    for (final var executorThreadModel : providedExecutorModels) {
      // If unexpected, due to some reason a provided
      // executor model is running, skip the iteration.
      if (executorThreadModel.running()) {
        continue;
      }
      // If the period-rate for the executor is not defined
      // yet, define a default value -> 20.
      if (executorThreadModel.periodRate() <= 0) {
        executorThreadModel.periodRate(20);
      }
      executorThreadModel.schedule();
    }
  }

  /**
   * Adds the given executor model to the custom executors list
   * and schedule it to start it.
   *
   * @param providedExecutorModel the {@link CustomExecutorThreadModel}.
   * @since 0.0.1
   */
  public void scheduleWithProvidedExecutor(final @NotNull CustomExecutorThreadModel providedExecutorModel) {
    this.customExecutorModels.add(providedExecutorModel);
    // If the given executor is already running, we do nothing.
    if (providedExecutorModel.running()) {
      return;
    }
    // If unexpected, due to some reason a provided
    // executor model is running, skip the iteration.
    if (providedExecutorModel.running()) {
      return;
    }
    // If the period-rate for the executor is not defined
    // yet, define a default value -> 20.
    if (providedExecutorModel.periodRate() <= 0) {
      providedExecutorModel.periodRate(20);
    }
    providedExecutorModel.schedule();
  }

  /**
   * Executes the shutdown process for the scoreboard
   * handling components.
   *
   * @since 0.0.1
   */
  public void shutdown() {
    // If the scoreboard-library used for internal scoreboards handling
    // through the server, we log to the console that is already shutdown.
    if (this.scoreboardLibrary == null) {
      this.logger.info("Scoreboard library already off, skipping shutting down for it.");
    }
    this.logger.info("Shutting down scoreboard components.");
    // Checks if there are executor thread models
    // available, and if is, perform a shutting down
    // on them.
    if (!this.customExecutorModels.isEmpty()) {
      this.shutdownExecutorModels();
    }
    this.repository.clear();
    this.scoreboardLibrary.close();
  }

  /**
   * Executes the shutdown logic and handles the provided
   * shutdown result for the running executor thread models.
   *
   * @since 0.0.1
   */
  private void shutdownExecutorModels() {
    for (final var executorThreadModel : this.customExecutorModels) {
      // If executor has no-longer run before,
      // we skip it.
      if (!executorThreadModel.running()) {
        continue;
      }
      final var executorShutdownResult = executorThreadModel.shutdown();
      // Notify if the executor shutdown has failed.
      if (executorShutdownResult.failed()) {
        this.logger.warn("Incorrectly shutdown on current CustomExecutorThreadModel.");
      }
      this.logger.info("Shutdown result for 'ScoreboardUpdatingExecutor' thread is: {}", executorShutdownResult);
    }
  }

  /**
   * Creates a new scoreboard for the given player.
   *
   * @param player the targeted player.
   * @since 0.0.1
   */
  public void create(final @NotNull Player player) {
    final var scoreboardModel = this.createNeededModelForPlayer(player);
    // Checks if the provided scoreboard model is nullable,
    // or not.
    if (scoreboardModel == null) {
      return;
    }
    final var scoreboardCreateEvent = new ScoreboardCreateEvent(player, scoreboardModel);
    Bukkit.getPluginManager().callEvent(scoreboardCreateEvent);
    // Avoid totally scoreboard creation for the player
    // if the event was cancelled.
    if (scoreboardCreateEvent.isCancelled()) {
      return;
    }
    final var config = this.configProvider.get();
    final var scoreboardModelSidebar = scoreboardModel.internal();
    scoreboardModelSidebar.addPlayer(player);
    // In case that animated-title isn't enabled, we need to define manually
    // the title for the scoreboard, so we use the first element of the animated-title
    // content list as static-title for the scoreboard.
    if (config.useScoreboardAnimatedTitle) {
      scoreboardModelSidebar.title(ComponentUtils.ofSingle(config.titleContent.get(0)));
    }
    this.repository.register(player, scoreboardModelSidebar);
  }

  /**
   * Creates a new cached scoreboard model based on the
   * information provided by the configuration.
   *
   * @param player the player for this scoreboard.
   * @return The {@link CachedScoreboardModel} or {@code null}
   *     if mode or world specified isn't valid.
   * @since 0.0.1
   */
  private @Nullable CachedScoreboardModel createNeededModelForPlayer(final @NotNull Player player) {
    final var config = this.configProvider.get();
    final var newSidebarObject = this.scoreboardLibrary.createSidebar();
    // Provides the new cached scoreboard model based on
    // the case ended, or null if the mode isn't valid.
    return switch (config.scoreboardMode) {
      case "WORLD" -> {
        CachedScoreboardModel scoreboardModel = null;
        for (final var section : config.scoreboardForWorlds) {
          final var worldName = section.targetedWorld;
          // Checks if the world specified exists and is loaded
          // on the server, or if the player world name is equals
          // than the currently iterated.
          if ((Bukkit.getWorld(worldName) == null) || !player.getWorld().getName().equals(worldName)) {
            continue;
          }
          scoreboardModel = new CachedScoreboardModel(
              player, newSidebarObject,
              ScoreboardToggleStateType.VISIBLE);
          break;
        }
        yield scoreboardModel;
      }
      case "SINGLE" -> new CachedScoreboardModel(
          player, newSidebarObject,
          ScoreboardToggleStateType.VISIBLE);
      default -> null;
    };
  }

  /**
   * Toggles the state of the scoreboard for the specified
   * player.
   *
   * @param player the specified player.
   * @return The status code for this operation. {@code 0} if
   *     the scoreboard-model is not available or event is cancelled,
   *     {@code 1} if new toggle-status is 'CLOSED', otherwise {@code 2}.
   * @since 0.0.1
   * @see ScoreboardManager#FIRST_POSSIBLE_TOGGLE_RESULT
   * @see ScoreboardManager#TOGGLE_ENABLE_RESULT
   * @see ScoreboardManager#TOGGLE_DISABLE_RESULT
   */
  public byte toggle(final @NotNull Player player) {
    final var playerId = player.getUniqueId().toString();
    final var scoreboardModel = this.repository.findOrNull(playerId);
    // Check if the player have a scoreboard assigned
    // before toggle-state change.
    if (scoreboardModel == null) {
      return FIRST_POSSIBLE_TOGGLE_RESULT;
    }
    // Fires the scoreboard toggle event and return
    // the final status code for this operation.
    final var scoreboardToggleEvent = new ScoreboardToggleEvent(player, scoreboardModel, scoreboardModel.toggleState());
    Bukkit.getPluginManager().callEvent(scoreboardToggleEvent);
    // In case that another plugin/s has cancelled this event due
    // to any reason, we will return the [FIRST_POSSIBLE_TOGGLE_RESULT]
    // status that aims to a byte data type with value -> 0.
    if (scoreboardToggleEvent.isCancelled()) {
      return FIRST_POSSIBLE_TOGGLE_RESULT;
    }
    // Toggle visibility and state for the scoreboard,
    // and use the provided toggle-state for update model
    // ín repository cache, and provide a status code depending
    // on new toggle-state type.
    final var newToggleStateProvided = scoreboardModel.toggleVisibility();
    this.repository.update(player, newToggleStateProvided);
    // If the new toggle-state defined is the [CLOSED] type, we return
    // the [TOGGLE_DISABLE_RESULT] status with byte value of -> 2. Other-wise
    // we return the [TOGGLE_ENABLE_RESULT] status that aims to a byte data
    // value with value -> 1.
    return (newToggleStateProvided == ScoreboardToggleStateType.CLOSED)
        ? TOGGLE_DISABLE_RESULT : TOGGLE_ENABLE_RESULT;
  }

  /**
   * Deletes the scoreboard assigned for this player.
   *
   * @param player the targeted player.
   * @return A boolean state for this operation, {@code true}
   *     if the scoreboard was removed. Otherwise {@code false}.
   * @see ScoreboardModelRepository#unregister(CachedScoreboardModel)
   * @since 0.0.1
   */
  public boolean delete(final @NotNull Player player) {
    final var scoreboardModel = this.repository.findOrNull(player.getUniqueId().toString());
    // Check if the player have a scoreboard assigned
    // before deletion.
    if (scoreboardModel == null) {
      return false;
    }
    // Fires the scoreboard close event during scoreboard deletion
    // for interactions with another plugins.
    final var scoreboardCloseEvent = new ScoreboardCloseEvent(player, scoreboardModel);
    Bukkit.getPluginManager().callEvent(scoreboardCloseEvent);
    // We will return the boolean state depending on execution result
    // for the unregister(...) method from the scoreboards-repository.
    return this.repository.unregister(scoreboardModel);
  }
}
