package me.qeklydev.scoreboard.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.qeklydev.scoreboard.cache.CachedScoreboardModel;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;
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
   * Removes the scoreboard model for this player based
   * on the uuid provided.
   *
   * @param player the player.
   * @return The boolean state for this operation, {@code true}
   *     if the scoreboard model exists, and sidebar was removed
   *     correctly. Otherwise {@code false}.
   * @see CachedScoreboardModel#remove()
   * @since 0.0.1
   */
  public boolean unregister(final @NotNull Player player) {
    final var scoreboardModel = this.scoreboards.remove(player.getUniqueId().toString());
    /*
     * Check if this player have a scoreboard
     * assigned, and then remove it.
     */
    return (scoreboardModel != null) && scoreboardModel.remove();
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
