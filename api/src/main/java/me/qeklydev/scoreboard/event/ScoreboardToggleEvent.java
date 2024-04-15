package me.qeklydev.scoreboard.event;

import me.qeklydev.scoreboard.cache.CachedScoreboardModel;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when the player change the current
 * {@link ScoreboardToggleStateType} for the scoreboard.
 *
 * @since 0.0.1
 */
public final class ScoreboardToggleEvent extends ScoreboardEventModel implements Cancellable {
  private ScoreboardToggleStateType oldScoreboardToggleState;
  private boolean cancelled;

  public ScoreboardToggleEvent(final @NotNull Player player, final @NotNull CachedScoreboardModel scoreboardModel,
                               final @NotNull ScoreboardToggleStateType oldScoreboardToggleState) {
    super(player, scoreboardModel);
    this.oldScoreboardToggleState = oldScoreboardToggleState;
  }

  /**
   * Returns the scoreboard toggle state type early before
   * state change, .e.g CLOSED (this is returned) -> VISIBLE.
   *
   * @return The previous {@link ScoreboardToggleStateType} for
   *     the scoreboard.
   * @since 0.0.1
   */
  public @NotNull ScoreboardToggleStateType oldToggleState() {
    return this.oldScoreboardToggleState;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(final boolean cancel) {
    this.cancelled = cancel;
  }
}
