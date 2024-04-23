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
package me.qeklydev.scoreboard;

import me.qeklydev.scoreboard.manager.ScoreboardManager;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import org.jetbrains.annotations.NotNull;

/**
 * This interface is used as model for main plugin class
 * implementation and provider for API instances.
 *
 * @since 0.0.1
 */
public interface ApiModel {
  /**
   * Returns an instance of the scoreboard model repository.
   * If reference is null, will throw an {@link IllegalStateException}.
   *
   * @return The {@link ScoreboardModelRepository} reference.
   * @since 0.0.1
   */
  @NotNull ScoreboardModelRepository scoreboardRepository();

  /**
   * Returns an instance of the scoreboard manager.
   * If reference is null, will throw an {@link IllegalStateException}.
   *
   * @return The {@link ScoreboardManager} reference.
   * @since 0.0.1
   */
  @NotNull ScoreboardManager scoreboardManager();
}
