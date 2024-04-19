package me.qeklydev.scoreboard.cache;

import java.util.UUID;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This record class represents a cached model for the
 * scoreboard of each connected player.
 * 
 * @param id the player uuid as a string.
 * @param internal the {@link Sidebar} model that represents
 *                 the scoreboard, and is used for their handling.
 * @param toggleState the current {@link ScoreboardToggleStateType}
 *                    for this scoreboard.
 * @since 0.0.1
 */
public record CachedScoreboardModel(@NotNull String id, @NotNull Sidebar internal, @NotNull ScoreboardToggleStateType toggleState) {
  /**
   * Returns the {@link Player} based on the uuid
   * for this scoreboard model. Will throw an {@link IllegalArgumentException}
   * if the player is not connected to server.
   * 
   * @return The {@link Player}.
   * @since 0.0.1
   */
  public @NotNull Player ofUid() {
    final var player = Bukkit.getPlayer(UUID.fromString(this.id));
    /*
     * Checks if the specified player for the UUID
     * is connected.
     */
    if (player == null) {
      throw new IllegalArgumentException("The player with uid '%s' was not founded.".formatted(this.id));
    }
    return player;
  }

  /**
   * Removes this scoreboard for the player with
   * this assigned model.
   *
   * @return A boolean state for this operation, {@code true}
   *     if the scoreboard was removed. Otherwise {@code false}
   *     if was already removed early.
   * @see CachedScoreboardModel#ofUid()
   * @since 0.0.1
   */
  public boolean remove() {
    /*
     * Check if the scoreboard already was closed
     * for the player.
     */
    if (this.internal.closed()) {
      return false;
    }
    this.internal.removePlayer(this.ofUid());
    this.internal.close();
    return true;
  }

  /**
   * Modifies the current scoreboard title by the new
   * given title.
   *
   * @param newTitle the title component to set.
   * @return The boolean state for this operation,
   *     {@code true} if title was changed. Otherwise {@code false}
   *     if toggle-state is {@link ScoreboardToggleStateType#CLOSED}.
   * @since 0.0.1
   */
  public boolean changeTitle(final @NotNull Component newTitle) {
    /*
     * We don't want to update the title for this scoreboard
     * if is closed for visibility.
     */
    if (this.toggleState == ScoreboardToggleStateType.CLOSED) {
      return false;
    }
    this.internal.title(newTitle);
    return true;
  }

  /**
   * Updates the content for the specified line with the
   * given component.
   *
   * @param index the line number.
   * @param lineComponent the component for that line.
   * @since 0.0.1
   */
  public void updateLine(final int index, final @NotNull Component lineComponent) {
    this.internal.line(index, lineComponent);
  }

  /**
   * Updates the current title for the scoreboard by the
   * given component.
   * Don't confuse with {@link CachedScoreboardModel#changeTitle(Component)},
   * {@code updateTitle()} method is used during title-animation.
   *
   * @param titleComponent the new title component.
   * @since 0.0.1
   */
  public void updateTitle(final @NotNull Component titleComponent) {
    this.internal.title(titleComponent);
  }

  /**
   * Toggles the visibility of the scoreboard for
   * the current player.
   *
   * @return The new {@link ScoreboardToggleStateType}
   *     for this operation.
   * @since 0.0.1
   */
  public @NotNull ScoreboardToggleStateType toggleVisibility() {
    final var player = this.ofUid();
    /*
     * If the previous toggle-state for the scoreboard
     * was 'CLOSED', then we show again the scoreboard
     * to the player.
     */
    if (this.toggleState == ScoreboardToggleStateType.CLOSED) {
      this.internal.addPlayer(player);
      return ScoreboardToggleStateType.VISIBLE;
    }
    this.internal.removePlayer(player);
    return ScoreboardToggleStateType.CLOSED;
  }
}
