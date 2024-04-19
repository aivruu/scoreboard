package me.qeklydev.scoreboard.listener;

import me.qeklydev.scoreboard.manager.ScoreboardManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardListener implements Listener {
  private final ScoreboardManager scoreboardManager;
  private final ComponentLogger logger;

  public ScoreboardListener(final @NotNull ScoreboardManager scoreboardManager, final @NotNull ComponentLogger logger) {
    this.scoreboardManager = scoreboardManager;
    this.logger = logger;
  }

  @EventHandler
  void onJoin(final @NotNull PlayerJoinEvent event) {
    this.scoreboardManager.create(event.getPlayer());
  }

  @EventHandler
  void onQuit(final @NotNull PlayerQuitEvent event) {
    final var player = event.getPlayer();
    final var couldBeClosed = this.scoreboardManager.delete(player);
    /*
     * If scoreboard could not be deleted correctly due
     * to any reason, notify this as a warn log.
     */
    if (!couldBeClosed) {
      this.logger.warn("The scoreboard for player '{}' could not be deleted, maybe already was deleted before.", player.getName());
    }
  }
}
