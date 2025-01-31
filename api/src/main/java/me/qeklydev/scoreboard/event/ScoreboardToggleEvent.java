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
  private final ScoreboardToggleStateType oldScoreboardToggleState;
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
