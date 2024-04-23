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
package me.qeklydev.scoreboard.thread.impl;

import java.util.List;
import java.util.concurrent.Executors;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.CustomExecutorThreadModel;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class TitleUpdaterThreadModelImpl extends CustomExecutorThreadModel {
  private List<Component> content;
  private byte index;

  public TitleUpdaterThreadModelImpl(final @NotNull ScoreboardModelRepository scoreboardRepository,
                                     final @NotNull List<@NotNull Component> content) {
    super(
        Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "ScoreboardTitleUpdaterExecutor")),
        scoreboardRepository);
    this.content = content;
  }

  /**
   * Sets a new list of components for this executor-model.
   *
   * @param newContent the new components list.
   * @since 0.0.1
   */
  public void content(final @NotNull List<@NotNull Component> newContent) {
    this.content = newContent;
  }

  /**
   * Returns the list of components for this executor-model.
   *
   * @return The list of {@link Component} for this {@link CustomExecutorThreadModel}.
   * @since 0.0.1
   */
  public @NotNull List<@NotNull Component> content() {
    return this.content;
  }

  @Override
  public void run() {
    /*
     * If current index value is equals than the list size,
     * reset the value to zero.
     */
    if (++this.index == this.content.size()) {
      this.index = 0;
    }
    for (final var scoreboardModel : super.scoreboardRepository.scoreboards()) {
      /*
       * If visibility status for the scoreboard is 'CLOSED',
       * skip this iteration.
       */
      if (scoreboardModel.toggleState() == ScoreboardToggleStateType.CLOSED) {
        continue;
      }
      /*
       * Establish the current content list index as the current animation
       * line for the title.
       */
      scoreboardModel.updateTitle(this.content.get(this.index));
    }
  }
}
