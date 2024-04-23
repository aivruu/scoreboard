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
package me.qeklydev.scoreboard.cache;

import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This record class represents a cached model for the
 * scoreboard of each connected player.
 *
 * @param player the player for this scoreboard.
 * @param internal the {@link Sidebar} model that represents
 *                 the scoreboard, and is used for their handling.
 * @param toggleState the current {@link ScoreboardToggleStateType}
 *                    for this scoreboard.
 * @since 0.0.1
 */
public record CachedScoreboardModel(@NotNull Player player, @NotNull Sidebar internal, @NotNull ScoreboardToggleStateType toggleState) {
  /**
   * Removes this scoreboard for the player with
   * this assigned model.
   *
   * @return A boolean state for this operation, {@code true}
   *     if the scoreboard was removed. Otherwise {@code false}
   *     if was already removed early.
   * @since 0.0.1
   */
  public boolean remove() {
    /*
     * Check if the scoreboard already was closed
     * for the player.
     */
    if (this.internal.closed()) {
      return false;
    }
    this.internal.removePlayer(this.player);
    this.internal.close();
    return true;
  }

  /**
   * Modifies the current scoreboard title by the new
   * given title.
   *
   * @param newTitle the title component to set.
   * @return The boolean state for this operation,
   *     {@code true} if title was changed. Otherwise {@code false}
   *     if toggle-state is {@link ScoreboardToggleStateType#CLOSED}.
   * @since 0.0.1
   */
  public boolean changeTitle(final @NotNull Component newTitle) {
    /*
     * We don't want to update the title for this scoreboard
     * if is closed for visibility.
     */
    if (this.toggleState == ScoreboardToggleStateType.CLOSED) {
      return false;
    }
    this.internal.title(newTitle);
    return true;
  }

  /**
   * Updates the content for the specified line with the
   * given component.
   *
   * @param index the line number.
   * @param lineComponent the component for that line.
   * @since 0.0.1
   */
  public void updateLine(final int index, final @NotNull Component lineComponent) {
    this.internal.line(index, lineComponent);
  }

  /**
   * Updates the current title for the scoreboard by the
   * given component.
   * Don't confuse with {@link CachedScoreboardModel#changeTitle(Component)},
   * {@code updateTitle()} method is used during title-animation.
   *
   * @param titleComponent the new title component.
   * @since 0.0.1
   */
  public void updateTitle(final @NotNull Component titleComponent) {
    this.internal.title(titleComponent);
  }

  /**
   * Toggles the visibility of the scoreboard for
   * the current player.
   *
   * @return The new {@link ScoreboardToggleStateType}
   *     for this operation.
   * @since 0.0.1
   */
  public @NotNull ScoreboardToggleStateType toggleVisibility() {
    /*
     * If the previous toggle-state for the scoreboard
     * was 'CLOSED', then we show again the scoreboard
     * to the player and return the toggle-state as 'VISIBLE'.
     */
    if (this.toggleState == ScoreboardToggleStateType.CLOSED) {
      this.internal.addPlayer(this.player);
      return ScoreboardToggleStateType.VISIBLE;
    }
    this.internal.removePlayer(this.player);
    return ScoreboardToggleStateType.CLOSED;
  }
}
