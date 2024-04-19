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
    /*
     * Declare repositories for the declared
     * dependencies search.
     */
    libraryResolver.addRepository(new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven1/").build());
    libraryResolver.addRepository(new RemoteRepository.Builder("triumphteam", "default", "https://repo.triumphteam.dev/snapshots/").build());
    /*
     * Declare required dependencies with artifacts
     * and coords.
     */
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("dev.triumphteam:triumph-cmd-bukkit:2.0.0-SNAPSHOT"), null));
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("org.spongepowered:configurate-hocon:4.1.2"), null));
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("net.megavex:scoreboard-library-api:2.1.6"), null));
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("net.megavex:scoreboard-library-implementation:2.1.6"), null));
    libraryResolver.addDependency(new Dependency(new DefaultArtifact("net.megavex:scoreboard-library-modern:2.1.6"), null));

    pluginClasspathBuilder.addLibrary(libraryResolver);
  }
}
