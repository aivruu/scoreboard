package me.qeklydev.scoreboard.thread.impl;

import java.util.concurrent.Executors;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.CustomExecutorThreadModel;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles the updating content process for
 * the active scoreboards
 *
 * @since 0.0.1
 */
public final class ScoreboardUpdaterThreadModelImpl extends CustomExecutorThreadModel {
  private byte index;

  public ScoreboardUpdaterThreadModelImpl(final @NotNull ScoreboardModelRepository scoreboardRepository) {
    super(
        Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "ScoreboardUpdaterExecutor")),
        scoreboardRepository);
  }

  @Override
  public void run() {
    /*
     * If current index value is equals than the list size,
     * reset the value to zero.
     */
    if (++this.index == this.content.size()) {
      this.index = 0;
    }
    for (final var scoreboardModel : this.scoreboardRepository.scoreboards()) {
      /*
       * If visibility status for the scoreboard is 'CLOSED',
       * skip this iteration.
       */
      if (scoreboardModel.toggleState() == ScoreboardToggleStateType.CLOSED) {
        continue;
      }
      scoreboardModel.sidebar().line(0, this.content.get(this.index));
    }
  }
}
