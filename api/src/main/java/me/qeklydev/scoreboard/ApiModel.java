package me.qeklydev.scoreboard;

import me.qeklydev.scoreboard.manager.ScoreboardManager;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import org.jetbrains.annotations.NotNull;

/**
 * This interface is used as model for main plugin class
 * implementation and provider for API instances.
 *
 * @since 0.0.1
 */
public interface ApiModel {
  /**
   * Returns an instance of the scoreboard model repository.
   * If reference is null, will throw an {@link IllegalStateException}.
   *
   * @return The {@link ScoreboardModelRepository} reference.
   * @since 0.0.1
   */
  @NotNull ScoreboardModelRepository scoreboardRepository();

  /**
   * Returns an instance of the scoreboard manager.
   * If reference is null, will throw an {@link IllegalStateException}.
   *
   * @return The {@link ScoreboardManager} reference.
   * @since 0.0.1
   */
  @NotNull ScoreboardManager scoreboardManager();
}
