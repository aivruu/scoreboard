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
   * @param player the player.
   * @param sidebar the {@link CachedScoreboardModel} controller for this
   *                scoreboard.
   * @since 0.0.1
   */
  public void register(final @NotNull Player player, final @NotNull Sidebar sidebar) {
    final var scoreboardModel = new CachedScoreboardModel(player, sidebar, ScoreboardToggleStateType.VISIBLE);
    this.scoreboards.put(player.getUniqueId().toString(), scoreboardModel);
  }

  /**
   * Updates the current toggle-state for the cached scoreboard model
   * based on the provided identifier, only if model is present in cache.
   *
   * @param player the player.
   * @param newToggleState the new {@link ScoreboardToggleStateType} for
   *                       update the old one.
   * @since 0.0.1
   */
  public void update(final @NotNull Player player, final @NotNull ScoreboardToggleStateType newToggleState) {
    final var playerId = player.getUniqueId().toString();
    this.scoreboards.computeIfPresent(playerId, (id, scoreboardModel) ->
        new CachedScoreboardModel(player, scoreboardModel.internal(), newToggleState));
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
    final var playerId = scoreboardModel.player().getUniqueId().toString();
    /*
     * We skip the non-null check for this model
     * due that already was effected before.
     */
    this.scoreboards.remove(playerId);
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
