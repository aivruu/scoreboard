package me.qeklydev.scoreboard;

import org.jetbrains.annotations.NotNull;

/**
 * This class provides rapid-access static methods
 * for handle and get API instances.
 *
 * @since 0.0.1
 */
public final class ApiProvider {
  private static ApiModel apiModelReference;

  private ApiProvider() {
    throw new UnsupportedOperationException("This class is for API instances handling and cannot be instantiated.");
  }

  /**
   * Returns an API instance, if API reference has not
   * been loaded yet, will throw an {@link IllegalStateException}.
   *
   * @return The {@link ApiModel}.
   * @since 0.0.1
   */
  public static @NotNull ApiModel get() {
    if (apiModelReference == null) {
      throw new IllegalStateException("The API instance is not loaded yet.");
    }
    return apiModelReference;
  }

  /**
   * Assigns the provided API reference to the current instance.
   * If it is already assigned, will throw an {@link IllegalStateException}.
   *
   * @param providedReference the {@link ApiModel} reference to assign.
   * @since 0.0.1
   */
  public static void load(final @NotNull ApiModel providedReference) {
    if (ApiProvider.apiModelReference != null) {
      throw new IllegalStateException("The API instance is already initialized.");
    }
    ApiProvider.apiModelReference = providedReference;
  }

  /**
   * Removes the assigned reference for the API instance.
   *
   * @since 0.0.1
   */
  public static void unload() {
    if (apiModelReference != null) {
      apiModelReference = null;
    }
  }
}
