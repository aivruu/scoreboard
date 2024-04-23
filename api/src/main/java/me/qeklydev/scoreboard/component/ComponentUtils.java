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
package me.qeklydev.scoreboard.component;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public final class ComponentUtils {
  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  private ComponentUtils() {
    throw new UnsupportedOperationException("This class is for utility and cannot be instantiated.");
  }

  public static @NotNull Component ofSingle(final @NotNull String text) {
    return MINI_MESSAGE.deserialize(text);
  }

  public static @NotNull Component ofSingleWith(final @NotNull String text, final TagResolver @NotNull... resolvers) {
    return MINI_MESSAGE.deserialize(text, resolvers);
  }

  public static @NotNull List<@NotNull Component> ofMany(final @NotNull List<@NotNull String> text) {
    final var componentsList = new ArrayList<Component>(text.size());
    for (final var iterated : text) {
      componentsList.add(MINI_MESSAGE.deserialize(iterated));
    }
    return componentsList;
  }
}
