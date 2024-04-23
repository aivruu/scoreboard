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
package me.qeklydev.scoreboard.listener;

import me.qeklydev.scoreboard.manager.ScoreboardManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardListener implements Listener {
  private final ScoreboardManager scoreboardManager;
  private final ComponentLogger logger;

  public ScoreboardListener(final @NotNull ScoreboardManager scoreboardManager, final @NotNull ComponentLogger logger) {
    this.scoreboardManager = scoreboardManager;
    this.logger = logger;
  }

  @EventHandler
  void onJoin(final @NotNull PlayerJoinEvent event) {
    this.scoreboardManager.create(event.getPlayer());
  }

  @EventHandler
  void onQuit(final @NotNull PlayerQuitEvent event) {
    final var player = event.getPlayer();
    final var couldBeClosed = this.scoreboardManager.delete(player);
    /*
     * If scoreboard could not be deleted correctly due
     * to any reason, notify this as a warn log.
     */
    if (!couldBeClosed) {
      this.logger.warn("The scoreboard for player '{}' could not be deleted, maybe already was deleted before.", player.getName());
    }
  }
}
