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

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public final class Messages implements ConfigurationInterface {
  public String permission = "<prefix> <red>You don't have permission for this.";

  public List<String> help = List.of(
      "",
      "",
      "",
      "");

  public String reloadConfigFailed = "<prefix> <red>Failed to reload the configuration models, check them by some search of syntax error.";

  public String reloadComponentsFailed = """
      <prefix> <red>Failed to reload the updating-components for the scoreboard.
       Some causes could be a negative or a zero value for update-rates, or
       defined scoreboard-mode is not valid. <gradient:green:yellow>-> Threads: ScoreboardUpdaterExecutor & ScoreboardTitleUpdaterExecutor""";

  public String reloadSuccess = "<prefix> <green>The configuration and scoreboard-components have been reloaded!";

  public String scoreboardEnabled = "<prefix> <green>Scoreboard has been enabled!";

  public String scoreboardDisabled = "<prefix> <red>Scoreboard has been disabled!";
}
