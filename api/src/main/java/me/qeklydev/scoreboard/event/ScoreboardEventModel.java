package me.qeklydev.scoreboard.event;

import me.qeklydev.scoreboard.cache.CachedScoreboardModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * The base model for every event implementation.
 *
 * @since 0.0.1
 */
public abstract class ScoreboardEventModel extends Event {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player;
  private final CachedScoreboardModel scoreboardModel;

  public ScoreboardEventModel(final @NotNull Player player, final @NotNull CachedScoreboardModel scoreboardModel) {
    this.player = player;
    this.scoreboardModel = scoreboardModel;
  }

  /**
   * Returns the player involved in this event.
   *
   * @return The involved {@link Player}.
   * @since 0.0.1
   */
  public @NotNull Player player() {
    return this.player;
  }

  /**
   * Returns the scoreboard model involved in this
   * event.
   *
   * @return The involved {@link CachedScoreboardModel}.
   * @since 0.0.1
   */
  public @NotNull CachedScoreboardModel scoreboardModel() {
    return this.scoreboardModel;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static @NotNull HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
