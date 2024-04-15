package me.qeklydev.scoreboard.thread.impl;

import java.util.concurrent.Executors;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.CustomExecutorThreadModel;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import org.jetbrains.annotations.NotNull;

public final class TitleUpdaterThreadModelImpl extends CustomExecutorThreadModel {
  private byte index;

  public TitleUpdaterThreadModelImpl(final @NotNull ScoreboardModelRepository scoreboardRepository) {
    super(
        Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "ScoreboardTitleUpdaterExecutor")),
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
      final var scoreboardTitle = scoreboardModel.definedTitleForThisScoreboard();
      scoreboardModel.sidebar().line(this.index, scoreboardTitle.get(this.index));
    }
  }
}
