package me.qeklydev.scoreboard.manager;

import java.util.List;

import me.qeklydev.scoreboard.cache.CachedScoreboardModel;
import me.qeklydev.scoreboard.config.Configuration;
import me.qeklydev.scoreboard.config.ConfigurationProvider;
import me.qeklydev.scoreboard.event.ScoreboardCloseEvent;
import me.qeklydev.scoreboard.event.ScoreboardCreateEvent;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.CustomExecutorThreadModel;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import me.qeklydev.scoreboard.utils.ComponentUtils;
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
 * and creation or deletion (no toggle) of active scoreboards.
 *
 * @since 0.0.1
 */
public final class ScoreboardManager {
  private final ComponentLogger logger;
  private final ScoreboardModelRepository repository;
  private final ConfigurationProvider<Configuration> configProvider;
  private ScoreboardLibrary scoreboardLibrary;
  private List<CustomExecutorThreadModel> customExecutorModels;
  
  public ScoreboardManager(final @NotNull ComponentLogger logger, final @NotNull ScoreboardModelRepository repository,
                           final @NotNull ConfigurationProvider<@NotNull Configuration> configProvider) {
    this.repository = repository;
    this.logger = logger;
    this.configProvider = configProvider;
  }

  /**
   * Boots up the scoreboard main component.
   *
   * @param plugin a {@link JavaPlugin} instance.
   * @since 0.0.1
   */
  public boolean load(final @NotNull JavaPlugin plugin) {
    try {
      this.scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
      this.logger.info("Loaded scoreboard library correctly.");
      return true;
    } catch (final NoPacketAdapterAvailableException exception) {
      this.logger.error("No packet adapter was founded for the scoreboard library", exception);
      return false;
    }
  }

  /**
   * Uses the given executor thread model list to provide it
   * to the custom executor model list.
   * 
   * @param providedExecutorModels the {@link CustomExecutorThreadModel} list.
   * @since 0.0.1
   */
  public void scheduleWithProvidedExecutors(final @NotNull List<@NotNull CustomExecutorThreadModel> providedExecutorModels) {
    /*
     * Throw an exception if the executor-model list
     * is already initialized.
     */
    if (this.customExecutorModels != null) {
      throw new IllegalStateException("Custom Executor Thread Model list already have a provider.");
    }
    this.customExecutorModels = providedExecutorModels;
    for (final var executorThreadModel : this.customExecutorModels) {
      /*
       * If unexpected, due to some reason a provided
       * executor model is running, skip the iteration.
       */
      if (executorThreadModel.running()) {
        continue;
      }
      /*
       * If the period-rate for the executor is not defined
       * yet, skip it.
       */
      if (executorThreadModel.periodRate() == 0) {
        continue;
      }
      executorThreadModel.schedule();
    }
  }

  /**
   * Executes the shutdown process for the scoreboard
   * handling components.
   *
   * @since 0.0.1
   */
  public void shutdown() {
    if (this.scoreboardLibrary == null) {
      this.logger.info("Scoreboard components already off, skipping shutting down for them.");
      return;
    }
    this.logger.info("Shutting down scoreboard components.");
    /*
     * Checks if there are executor thread models
     * available, and if is, perform a shutting down
     * on them.
     */
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
      /*
       * If executor has no-longer run before,
       * we skip it.
       */
      if (!executorThreadModel.running()) {
        continue;
      }
      final var executorShutdownResult = executorThreadModel.shutdown();
      if (executorShutdownResult.error()) {
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
    if (scoreboardModel == null) {
      return;
    }
    final var scoreboardCreateEvent = new ScoreboardCreateEvent(player, scoreboardModel);
    Bukkit.getPluginManager().callEvent(scoreboardCreateEvent);
    if (scoreboardCreateEvent.isCancelled()) {
      return;
    }
    final var scoreboardModelSidebar = scoreboardModel.sidebar();
    // Assign the player for this sidebar object.
    scoreboardModelSidebar.addPlayer(player);
    // Store player ID and sidebar object into the scoreboard repository.
    this.repository.register(player.getUniqueId().toString(), scoreboardModelSidebar);
  }

//  private @Nullable CachedScoreboardModel createNeededModelForPlayer(final @NotNull Player player) {
//    final var config = this.configProvider.get();
//    final var playerUid = player.getUniqueId().toString();
//    final var newSidebarObject = this.scoreboardLibrary.createSidebar();
//    return switch (config.scoreboardMode) {
//      case "WORLD" -> {
//        for (final var worldSection : config.scoreboardForWorlds) {
//          final var worldName = worldSection.targetedWorld;
//          /*
//           * Checks if the world specified exists and is loaded
//           * on the server, or if the player world name is equals
//           * than the currently iterated.
//           */
//          if ((Bukkit.getWorld(worldName) == null) || !player.getWorld().getName().equals(worldName)) {
//            continue;
//          }
//          yield new CachedScoreboardModel(
//              playerUid,
//              newSidebarObject,
//              ScoreboardToggleStateType.VISIBLE,
//              ComponentUtils.ofMany(worldSection.titleContent),
//              ComponentUtils.ofMany(worldSection.content));
//        }
//      }
//      case "SINGLE" -> new CachedScoreboardModel(
//          playerUid,
//          newSidebarObject,
//          ScoreboardToggleStateType.VISIBLE,
//          ComponentUtils.ofMany(config.titleContent),
//          ComponentUtils.ofMany(config.content));
//      default -> null;
//    };
//  }

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
    /*
     * Check if the player have a scoreboard assigned
     * before deletion.
     */
    if (scoreboardModel == null) {
      return false;
    }
    /*
     * Fires the scoreboard close event and return
     * the final boolean state for this operation
     * since repository execution.
     */
    final var scoreboardCloseEvent = new ScoreboardCloseEvent(player, scoreboardModel);
    Bukkit.getPluginManager().callEvent(scoreboardCloseEvent);
    return this.repository.unregister(scoreboardModel);
  }
}
