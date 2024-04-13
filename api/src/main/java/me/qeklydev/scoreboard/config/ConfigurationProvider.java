package me.qeklydev.scoreboard.config;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

/**
 * This record is used to proportionate a handling
 * about the configuration models.
 *
 * @param config the configuration model with an
 *               {@link AtomicReference}.
 * @param loader the loader for this configuration.
 * @param configModelClazz the model class.
 * @param <C> a serializable class model for the
 *            configuration.
 * @since 0.0.1
 */
public record ConfigurationProvider<C>(@NotNull AtomicReference<@NotNull C> config, @NotNull HoconConfigurationLoader loader,
                                       @NotNull Class<@NotNull C> configModelClazz) {
  /**
   * Reloads the configuration and sets the new
   * config model to the atomic reference for
   * this provider.
   *
   * @return A boolean state for this operation, {@code true}
   *     if configuration was reloaded. Otherwise {@code false}.
   * @since 0.0.1
   */
  public @NotNull CompletableFuture<@NotNull Boolean> reload() {
    return CompletableFuture.supplyAsync(() -> {
      try {
        final var node = this.loader.load();
        final var reloadedConfig = node.get(this.configModelClazz);
        node.set(this.configModelClazz, reloadedConfig);
        this.loader.save(node);
        this.config.set(reloadedConfig);
        return true;
      } catch (final ConfigurateException exception) {
        exception.printStackTrace();
        return false;
      }
    });
  }

  /**
   * Returns the configuration model reference for
   * this provider.
   *
   * @return The configuration model reference.
   * @since 0.0.1
   */
  public @NotNull C get() {
    return this.config.get();
  }

  /**
   * Creates a new configuration provider using the provided
   * configuration information.
   *
   * @param directory the destination for the configuration file.
   * @param fileName the name of the file.
   * @param clazz the model for the configuration.
   * @param <C> a serializable configuration model.
   * @return The {@link ConfigurationProvider} or {@code null} if
   *     something went wrong during creation/loading.
   * @since 0.0.1
   */
  public static <C extends ConfigurationInterface> @Nullable ConfigurationProvider<C> of(final @NotNull Path directory,
                                                                                         final @NotNull String fileName,
                                                                                         final @NotNull Class<@NotNull C> clazz) {
    final var loader = HoconConfigurationLoader.builder()
        .emitJsonCompatible(true)
        .prettyPrinting(true)
        .defaultOptions(opts -> opts
            .header("""
                Scoreboard | Create awesome and customizable scoreboards with many options!
                - A Paper plugin to create customizable scoreboards on several modes.
                - This plugin use MiniMessage format for the messages.""")
            .shouldCopyDefaults(true))
        .path(directory.resolve(fileName + ".conf"))
        .build();
    try {
      final var node = loader.load();
      final var config = node.get(clazz);
      node.set(clazz, config);
      loader.save(node);
      assert config != null;
      return new ConfigurationProvider<>(new AtomicReference<>(config), loader, clazz);
    } catch (final ConfigurateException exception) {
      exception.printStackTrace();
      return null;
    }
  }
}
