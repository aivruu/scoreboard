package me.qeklydev.scoreboard.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.qeklydev.scoreboard.cache.CachedScoreboardModel;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to have a handling about the
 * current cached scoreboard models that are used.
 *
 * @since 0.0.1
 */
public final class ScoreboardModelRepository {
  private final Map<String, CachedScoreboardModel> scoreboards;

  public ScoreboardModelRepository() {
    this.scoreboards = new HashMap<>();
  }

  /**
   * Tries to return the scoreboard model based on the
   * player uuid provided.
   *
   * @param id the player id.
   * @return The {@link CachedScoreboardModel}, or {@code null} if
   *     model isn't in cache.
   * @since 0.0.1
   */
  public @Nullable CachedScoreboardModel findOrNull(final @NotNull String id) {
    return this.scoreboards.get(id);
  }

  /**
   * Returns the collection with the scoreboard models for
   * each active scoreboard.
   *
   * @return The {@link Collection} of {@link CachedScoreboardModel}.
   * @since 0.0.1
   */
  public @NotNull Collection<@NotNull CachedScoreboardModel> scoreboards() {
    return this.scoreboards.values();
  }

  /**
   * Stores the player id and their Sidebar controller provided,
   * and store it in the repository cache.
   *
   * @param id the player id.
   * @param sidebar the {@link CachedScoreboardModel} controller for this
   *                scoreboard.
   * @since 0.0.1
   */
  public void register(final @NotNull String id, final @NotNull Sidebar sidebar) {
    final var scoreboardModel = new CachedScoreboardModel(id, sidebar, ScoreboardToggleStateType.VISIBLE);
    this.scoreboards.put(id, scoreboardModel);
  }

  /**
   * Updates the current toggle-state for the cached scoreboard model
   * based on the provided identifier, only if model is present in cache.
   *
   * @param id the player id.
   * @param newToggleState the new {@link ScoreboardToggleStateType} for
   *                       update the old one.
   * @since 0.0.1
   */
  public void update(final @NotNull String id, final @NotNull ScoreboardToggleStateType newToggleState) {
    this.scoreboards.computeIfPresent(id, (uid, scoreboardModel) ->
        new CachedScoreboardModel(id, scoreboardModel.internal(), newToggleState));
  }

  /**
   * Removes the scoreboard model for this player based
   * on the uuid provided.
   *
   * @param scoreboardModel the {@link CachedScoreboardModel}
   *                        to delete.
   * @return The boolean state for this operation, {@code true}
   *     if the scoreboard model exists, and sidebar was removed
   *     correctly. Otherwise {@code false}.
   * @see CachedScoreboardModel#remove()
   * @since 0.0.1
   */
  public boolean unregister(final @NotNull CachedScoreboardModel scoreboardModel) {
    /*
     * We skip the non-null check for this model
     * due that already was effected before.
     */
    this.scoreboards.remove(scoreboardModel.id());
    /*
     * Check if this player have a scoreboard
     * assigned, and then remove it.
     */
    return scoreboardModel.remove();
  }

  /**
   * Removes all models from the repository cache.
   *
   * @since 0.0.1
   */
  public void clear() {
    this.scoreboards.clear();
  }
}
