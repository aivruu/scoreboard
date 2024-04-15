package me.qeklydev.scoreboard.cache;

import java.util.List;
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
 * @param sidebar the {@link Sidebar} model that represents
 *                the player scoreboard.
 * @param toggleState the current {@link ScoreboardToggleStateType}
 *                    for this scoreboard.
 * @param definedTitleForThisScoreboard the pre-defined title that will
 *                                      use this scoreboard.
 * @since 0.0.1
 */
public record CachedScoreboardModel(@NotNull String id, @NotNull Sidebar sidebar, @NotNull ScoreboardToggleStateType toggleState,
                                    @NotNull List<@NotNull Component> definedTitleForThisScoreboard,
                                    @NotNull List<@NotNull Component> definedContentForThisScoreboard) {
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
    if (this.sidebar.closed()) {
      return false;
    }
    this.sidebar.removePlayer(this.ofUid());
    this.sidebar.close();
    return true;
  }

  /**
   * Modifies the current scoreboard title by the new
   * given title.
   *
   * @param newTitle the title component to set.
   * @return The boolean state for this operation,
   *     {@code true} if title was changed or reseted.
   *     Otherwise {@code false} if toggle-state is {@link ScoreboardToggleStateType#CLOSED}.
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
    if (newTitle.equals(Component.empty())) {
      this.sidebar.title(this.definedTitleForThisScoreboard.get(0));
      return true;
    }
    this.sidebar.title(newTitle);
    return true;
  }

  /**
   * Toggles the visibility of the scoreboard for
   * the current player.
   *
   * @return The boolean state for this operation,
   *     {@code false} if the scoreboard was closed
   *     for visibility. Otherwise {@code true}.
   * @since 0.0.1
   */
  public boolean toggleVisibility() {
    final var player = this.ofUid();
    if (this.toggleState == ScoreboardToggleStateType.CLOSED) {
      this.sidebar.addPlayer(player);
      return true;
    }
    this.sidebar.removePlayer(player);
    return false;
  }
}
