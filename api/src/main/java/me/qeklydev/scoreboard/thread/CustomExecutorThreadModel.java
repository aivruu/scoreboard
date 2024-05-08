/*
 * This file is part of scoreboard - https://github.com/aivruu/scoreboard
 * Copyright (C) 2020-2024 aivruu (https://github.com/aivruu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
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
    // We return the status since the withAlreadyShutdown(...) method
    // invocation in case that this executor already was shot as shutdown
    // before.
    if (!this.running) {
      return ExecutorThreadShutdownResult.withAlreadyShutdown();
    }
    // We mark this executor as no running anymore, and we start the shutdown
    // process for the executor.
    this.running = false;
    try {
      this.executorService.shutdown();
      final var terminatedAfterTimeoutElapsed = !this.executorService.awaitTermination(5, TimeUnit.SECONDS);
      // Checks if the executor has terminated after the timeout specified (5 seconds).
      if (terminatedAfterTimeoutElapsed) {
        this.executorService.shutdownNow();
        return ExecutorThreadShutdownResult.withShutdownImmediate();
      }
      // Executor has ended correctly before time-out have elapsed.
      return ExecutorThreadShutdownResult.withShutdownWithTermination();
    } catch (final InterruptedException exception) {
      exception.printStackTrace();
      return ExecutorThreadShutdownResult.withError();
    }
  }
}
