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

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class ScoreboardLoader implements PluginLoader {
  @Override
  public void classloader(final @NotNull PluginClasspathBuilder pluginClasspathBuilder) {
    final var libraryResolver = new MavenLibraryResolver();
    // Declare repositories for the declared
    // dependencies search.
    libraryResolver.addRepository(new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build());
    libraryResolver.addRepository(new RemoteRepository.Builder("triumphteam", "default", "https://repo.triumphteam.dev/snapshots/").build());
    // Declare required dependencies with artifacts
    // and coords.
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("dev.triumphteam:triumph-cmd-bukkit:2.0.0-SNAPSHOT"), null));
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("org.spongepowered:configurate-hocon:4.1.2"), null));
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("net.megavex:scoreboard-library-api:2.1.6"), null));
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("net.megavex:scoreboard-library-implementation:2.1.6"), null));
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("net.megavex:scoreboard-library-modern:2.1.6"), null));

    pluginClasspathBuilder.addLibrary(libraryResolver);
  }
}
