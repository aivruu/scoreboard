package me.qeklydev.scoreboard.manager;

import me.qeklydev.scoreboard.config.Configuration;
import me.qeklydev.scoreboard.config.ConfigurationProvider;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.ScoreboardUpdatingExecutorThread;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used for handle start-up and shutdown
 * of the scoreboard components, and provide functions
 * for creation and deletion of scoreboards.
 *
 * @since 0.0.1
 */
public final class ScoreboardManager {
  private final ComponentLogger logger;
  private final ScoreboardModelRepository repository;
  private final ScoreboardUpdatingExecutorThread updatingExecutorThread;
  private final ConfigurationProvider<Configuration> configProvider;
  private ScoreboardLibrary scoreboardLibrary;

  public ScoreboardManager(final @NotNull ComponentLogger logger, final @NotNull ScoreboardModelRepository repository,
                           final @NotNull ScoreboardUpdatingExecutorThread updatingExecutorThread,
                           final @NotNull ConfigurationProvider<@NotNull Configuration> configProvider) {
    this.repository = repository;
    this.logger = logger;
    this.updatingExecutorThread = updatingExecutorThread;
    this.configProvider = configProvider;
  }

  /**
   * Boots up the scoreboard main component.
   *
   * @param plugin a {@link JavaPlugin} instance.
   * @since 0.0.1
   */
  public void load(final @NotNull JavaPlugin plugin) {
    try {
      this.scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
      this.logger.info("Loaded scoreboard library correctly.");
    } catch (final NoPacketAdapterAvailableException exception) {
      this.logger.error("No packet adapter was founded for the scoreboard library", exception);
      return;
    }
    this.updatingExecutorThread.schedule(this.configProvider.get().scoreboardFrameUpdateRate);
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
    final var executorShutdownResult = this.updatingExecutorThread.shutdown();
    if (executorShutdownResult.error()) {
      this.logger.error("Incorrectly shutdown on 'ScoreboardUpdatingExecutor' thread");
    }
    this.logger.info("Shutdown result for 'ScoreboardUpdatingExecutor' thread is: {}", executorShutdownResult);
    this.repository.clear();
    this.scoreboardLibrary.close();
  }

  /**
   * Creates a new scoreboard for the given player.
   *
   * @param player the targeted player.
   * @since 0.0.1
   */
  public void create(final @NotNull Player player) {
    final var sidebar = this.scoreboardLibrary.createSidebar();
    // Assign this sidebar to the given player.
    sidebar.addPlayer(player);
    // Store player ID and sidebar object into the scoreboard repository.
    this.repository.register(player.getUniqueId().toString(), sidebar);
  }

  /**
   * Deletes the scoreboard assigned for this player.
   *
   * @param player the targeted player.
   * @return A boolean state for this operation, {@code true}
   *     if the scoreboard was removed. Otherwise {@code false}.
   * @see ScoreboardModelRepository#unregister(Player)
   * @since 0.0.1
   */
  public boolean delete(final @NotNull Player player) {
    return this.repository.unregister(player);
  }
}
