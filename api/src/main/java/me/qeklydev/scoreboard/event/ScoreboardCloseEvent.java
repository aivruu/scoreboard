package me.qeklydev.scoreboard.event;

import me.qeklydev.scoreboard.cache.CachedScoreboardModel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a scoreboard is deleted for
 * the current player, fired during {@link org.bukkit.event.player.PlayerQuitEvent}.
 *
 * @since 0.0.1
 */
public final class ScoreboardCloseEvent extends ScoreboardEventModel {
  public ScoreboardCloseEvent(final @NotNull Player player, final @NotNull CachedScoreboardModel scoreboardModel) {
    super(player, scoreboardModel);
  }
}
