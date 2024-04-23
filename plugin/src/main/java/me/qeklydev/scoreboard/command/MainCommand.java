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
import dev.triumphteam.cmd.core.annotation.SubCommand;
import me.qeklydev.scoreboard.ScoreboardPlugin;
import me.qeklydev.scoreboard.component.ComponentUtils;
import me.qeklydev.scoreboard.config.Configuration;
import me.qeklydev.scoreboard.config.ConfigurationProvider;
import me.qeklydev.scoreboard.config.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class MainCommand extends BaseCommand {
  private final ScoreboardPlugin plugin;
  private final ConfigurationProvider<Configuration> configProvider;
  private final ConfigurationProvider<Messages> messagesProvider;

  public MainCommand(final @NotNull ScoreboardPlugin plugin, final @NotNull ConfigurationProvider<@NotNull Configuration> configProvider,
                     final @NotNull ConfigurationProvider<@NotNull Messages> messagesProvider) {
    super("proboard");
    this.plugin = plugin;
    this.configProvider = configProvider;
    this.messagesProvider = messagesProvider;
  }

  @Default
  public void mainExecutor(final @NotNull CommandSender sender) {
    sender.sendMessage(ComponentUtils.ofSingle("<gradient:blue:gray>Thanks for using the plugin! Type /proboard help for more information."));
  }

  @SubCommand("help")
  @Requirement("help-perm")
  public void helpExecutor(final @NotNull CommandSender sender) {
    ComponentUtils.ofMany(this.messagesProvider.get().help).forEach(sender::sendMessage);
  }

  @SubCommand("reload")
  @Requirement("reload-perm")
  public void reloadExecutor(final @NotNull CommandSender sender) {
    final var messages = this.messagesProvider.get();
    final var prefixResolver = Placeholder.parsed("prefix", this.configProvider.get().prefix);
    final var reloadStatus = this.configProvider.reload()
        .thenCombineAsync(this.messagesProvider.reload(), (c, m) -> c && m)
        .join();
    /*
     * Check if the reload operation state for configuration
     * models is false.
     */
    if (!reloadStatus) {
      sender.sendMessage(ComponentUtils.ofSingleWith(messages.reloadConfigFailed, prefixResolver));
      return;
    }
    final var scoreboardComponentsReloadState = this.plugin.reload();
    /*
     * Check if the scoreboard-components reload status code
     * returned is 'false' (indicates an error).
     */
    if (!scoreboardComponentsReloadState) {
      sender.sendMessage(ComponentUtils.ofSingleWith(messages.reloadComponentsFailed, prefixResolver));
      return;
    }
    sender.sendMessage(ComponentUtils.ofSingleWith(messages.reloadSuccess, prefixResolver));
  }
}
