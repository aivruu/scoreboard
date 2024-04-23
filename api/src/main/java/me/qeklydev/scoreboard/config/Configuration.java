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
package me.qeklydev.scoreboard.config;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class Configuration implements ConfigurationInterface {
  @Comment("""
      The format used for the prefix.
      This prefix is used on most plugin messages,
      can be showed using the '<prefix>' tag.""")
  public String prefix = "<green><hover:show_text:'<green>Modrinth: https://modrinth.com/plugin/scoreboard<br><gradient:gray:white>GitHub: https://github.com/aivruu/scoreboard'>Scoreboard</hover> <gray>>";

  @Comment("""
      Use an animated-title for the scoreboard of every
      player on the server?""")
  public boolean useScoreboardAnimatedTitle = true;

  @Comment("""
      Requires the use-scoreboard-animated-title option enabled.

      The update-rate value for the scoreboard title
      content update.""")
  public byte scoreboardTitleUpdateRate = 10;

  @Comment("""
      The update-rate value for the scoreboard frame (lines)
      content update.""")
  public byte scoreboardFrameUpdateRate = 20;

  @Comment("""
      The scoreboard type that will be used.
      There are only 2 available modes.

      - SINGLE (DEFAULT) -> A same single scoreboard for all players.
      - WORLD -> There's a different scoreboard for each configured world.""")
  public String scoreboardMode = "SINGLE";

  @Comment("""
        This option is global, will be applied for
        any scoreboard mode.

        The content that will be showed as title if the
        'animated-title' option is enabled, otherwise will
        take only the first element to show.""")
  public List<String> titleContent = List.of(
      "<gradient:aqua:green>Scoreboard | Modrinth: .com/plugin/scoreboard",
      "<gradient:green:aqua>Scoreboard");

  @Comment("""
        THIS IS FOR 'SINGLE' SCOREBOARD MODE.

        This content will be used for the scoreboard lines.""")
  public List<String> content = List.of(
      "",
      "");

  @Comment("""
      Configure the worlds that you want to have an scoreboard
      pre-defined.""")
  public ScoreboardSection[] scoreboardForWorlds = { new ScoreboardSection() };

  @ConfigSerializable
  public static class ScoreboardSection {
    @Comment("""
        The name of the world that will use the scoreboard,
        also, actuate like an identifier for this section.""")
    public String targetedWorld = "world";

    @Comment("This content will be used for the scoreboard lines.")
    public List<String> content = List.of(
        "",
        "");
  }
}
