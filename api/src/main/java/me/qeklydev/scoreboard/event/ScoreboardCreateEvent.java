package me.qeklydev.scoreboard.event;

import me.qeklydev.scoreboard.cache.CachedScoreboardModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a scoreboard is created
 * by first time, fired during {@link org.bukkit.event.player.PlayerJoinEvent}.
 *
 * @since 0.0.1
 */
public final class ScoreboardCreateEvent extends ScoreboardEventModel implements Cancellable {
  private boolean cancelled;

  public ScoreboardCreateEvent(final @NotNull Player player, final @NotNull CachedScoreboardModel scoreboardModel) {
    super(player, scoreboardModel);
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
