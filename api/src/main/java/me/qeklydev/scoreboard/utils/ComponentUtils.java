package me.qeklydev.scoreboard.utils;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public final class ComponentUtils {
  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  private ComponentUtils() {
    throw new UnsupportedOperationException("This class is for utility and cannot be instantiated.");
  }

  public static @NotNull List<@NotNull Component> ofMany(final @NotNull List<@NotNull String> text) {
    final var componentsList = new ArrayList<Component>(text.size());
    for (final var iterated : text) {
      componentsList.add(MINI_MESSAGE.deserialize(iterated));
    }
    return componentsList;
  }
}
