package me.qeklydev.scoreboard.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class Messages implements ConfigurationInterface {
  public String permission = "<prefix> <red>You don't have permission for this.";
}
