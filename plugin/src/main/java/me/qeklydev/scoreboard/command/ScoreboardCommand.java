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
package me.qeklydev.scoreboard.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Requirement;
import dev.triumphteam.cmd.core.annotation.Requirements;
import java.util.List;
import me.qeklydev.scoreboard.component.ComponentUtils;
import me.qeklydev.scoreboard.config.Configuration;
import me.qeklydev.scoreboard.config.ConfigurationProvider;
import me.qeklydev.scoreboard.config.Messages;
import me.qeklydev.scoreboard.manager.ScoreboardManager;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardCommand extends BaseCommand {
  private final ScoreboardManager scoreboardManager;
  private final ConfigurationProvider<Configuration> configProvider;
  private final ConfigurationProvider<Messages> messagesProvider;

  public ScoreboardCommand(final @NotNull ScoreboardManager scoreboardManager, final @NotNull ConfigurationProvider<@NotNull Configuration> configProvider,
                     final @NotNull ConfigurationProvider<@NotNull Messages> messagesProvider) {
    super("scoreboard", List.of("sb"));
    this.scoreboardManager = scoreboardManager;
    this.configProvider = configProvider;
    this.messagesProvider = messagesProvider;
  }

  @Default
  @Requirements({
      @Requirement("scoreboard-use-perm"),
      @Requirement("player")
  })
  public void mainExecutor(final @NotNull Player player) {
    final var messages = this.messagesProvider.get();
    final var prefixResolver = Placeholder.parsed("prefix", this.configProvider.get().prefix);
    final var scoreboardToggleStatus = this.scoreboardManager.toggle(player);
    /* Send a different message to player depending on case. */
    switch (scoreboardToggleStatus) {
      case ScoreboardManager.FIRST_POSSIBLE_TOGGLE_RESULT -> {} /* Do nothing */
      case ScoreboardManager.TOGGLE_ENABLE_RESULT -> player.sendMessage(ComponentUtils.ofSingleWith(
          messages.scoreboardEnabled, prefixResolver));
      case ScoreboardManager.TOGGLE_DISABLE_RESULT -> player.sendMessage(ComponentUtils.ofSingleWith(
          messages.scoreboardDisabled, prefixResolver));
    }
  }
}
