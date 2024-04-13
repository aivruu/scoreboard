package me.qeklydev.scoreboard.thread;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.result.ExecutorThreadShutdownResult;
import me.qeklydev.scoreboard.type.ScoreboardToggleStateType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles the thread executor for the scoreboards
 * updating process.
 *
 * @since 0.0.1
 */
public final class ScoreboardUpdatingExecutorThread implements Runnable {
  private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(r ->
      new Thread(r, "ScoreboardUpdatingExecutor"));
  private final ScoreboardModelRepository scoreboardRepository;
  private List<String> content;
  private byte index;
  private boolean running;

  public ScoreboardUpdatingExecutorThread(final @NotNull ScoreboardModelRepository scoreboardRepository,
                                          final @NotNull List<@NotNull String> content) {
    this.scoreboardRepository = scoreboardRepository;
    this.content = content;
  }

  /**
   * Schedules the executor to execute the logic every
   * 'x' (given value) amount time.
   *
   * @param periodRate the update rate for the scoreboard.
   * @since 0.0.1
   */
  public void schedule(final byte periodRate) {
    EXECUTOR.scheduleAtFixedRate(this, 0, periodRate, TimeUnit.SECONDS);
    this.running = true;
  }

  /**
   * Marks this thread as running or not.
   *
   * @param markAsRunning whether is running or not.
   * @since 0.0.1
   */
  public void running(final boolean markAsRunning) {
    this.running = markAsRunning;
  }

  /**
   * Returns whether this thread is running.
   *
   * @return Whether this thread is running.
   * @since 0.0.1
   */
  public boolean running() {
    return this.running;
  }

  /**
   * Sets a new content for the list lines for
   * the scoreboards.
   *
   * @param newContent a new string list.
   * @since 0.0.1
   */
  public void content(final @NotNull List<@NotNull String> newContent) {
    this.content = newContent;
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

  /**
   * Shutdowns this executor thread and provide a final result
   * depending on operation status.
   * 
   * @return The {@link ExecutorThreadShutdownResult} with several
   *     status.
   * <p></p>
   *     - {@link ExecutorThreadShutdownResult#withError()} if an
   *     exception was triggered during shutdown.<p></p>
   *     - {@link ExecutorThreadShutdownResult#withShutdownWithTermination()}
   *     if shutdown was before timeout have elapsed.<p></p>
   *     - {@link ExecutorThreadShutdownResult#withShutdownImmediate()} if
   *     shutdown was later timeout have elapsed.<p></p>
   *     - {@link ExecutorThreadShutdownResult#withAlreadyShutdown()} if
   *     executor was already shutdown early.
   * @since 0.0.1
   */
  public @NotNull ExecutorThreadShutdownResult shutdown() {
    if (!this.running) {
      return ExecutorThreadShutdownResult.withAlreadyShutdown();
    }
    this.running = false;
    try {
      EXECUTOR.shutdown();
      final var terminatedLaterTimeoutElapsed = !EXECUTOR.awaitTermination(5, TimeUnit.SECONDS);
      if (terminatedLaterTimeoutElapsed) {
        EXECUTOR.shutdownNow();
        return ExecutorThreadShutdownResult.withShutdownImmediate();
      }
      return ExecutorThreadShutdownResult.withShutdownWithTermination();
    } catch (final InterruptedException exception) {
      exception.printStackTrace();
      return ExecutorThreadShutdownResult.withError();
    }
  }
}
