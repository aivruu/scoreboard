package me.qeklydev.scoreboard.config;

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
}
