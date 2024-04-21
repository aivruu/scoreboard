package me.qeklydev.scoreboard.thread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import me.qeklydev.scoreboard.repository.ScoreboardModelRepository;
import me.qeklydev.scoreboard.thread.result.ExecutorThreadShutdownResult;
import org.jetbrains.annotations.NotNull;

/**
 * Thread Handling Class Model for implementations
 * that make periodically modifications to the scoreboards
 * content.
 *
 * @since 0.0.1
 */
public abstract class CustomExecutorThreadModel implements Runnable {
  protected final ScoreboardModelRepository scoreboardRepository;
  private final ScheduledExecutorService executorService;
  private byte periodRate;
  private boolean running;

  public CustomExecutorThreadModel(final @NotNull ScheduledExecutorService executorService,
                                   final @NotNull ScoreboardModelRepository scoreboardRepository) {
    this.executorService = executorService;
    this.scoreboardRepository = scoreboardRepository;
  }

  /**
   * Schedules the executor to execute the logic every
   * 'x' amount time, this time is set during constructor.
   *
   * @since 0.0.1
   */
  public void schedule() {
    this.executorService.scheduleAtFixedRate(this, 0, this.periodRate, TimeUnit.SECONDS);
    this.running = true;
  }

  /**
   * Returns the current period-rate for this
   * executor model.
   *
   * @return The period-rate for this {@link CustomExecutorThreadModel}.
   * @since 0.0.1
   */
  public byte periodRate() {
    return this.periodRate;
  }

  /**
   * Sets a new period-rate for this executor
   * model.
   *
   * @param newPeriodRate the new period-rate.
   * @since 0.0.1
   */
  public void periodRate(final int newPeriodRate) {
    this.periodRate = (byte) newPeriodRate;
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
    /*
     * This executor was already given as shutdown
     * before.
     */
    if (!this.running) {
      return ExecutorThreadShutdownResult.withAlreadyShutdown();
    }
    /*
     * Mark as no-longer running, and proceed with the
     * normal or immediate shutdown.
     */
    this.running = false;
    try {
      this.executorService.shutdown();
      final var terminatedLaterTimeoutElapsed = !this.executorService.awaitTermination(5, TimeUnit.SECONDS);
      if (terminatedLaterTimeoutElapsed) {
        this.executorService.shutdownNow();
        return ExecutorThreadShutdownResult.withShutdownImmediate();
      }
      return ExecutorThreadShutdownResult.withShutdownWithTermination();
    } catch (final InterruptedException exception) {
      exception.printStackTrace();
      return ExecutorThreadShutdownResult.withError();
    }
  }
}
