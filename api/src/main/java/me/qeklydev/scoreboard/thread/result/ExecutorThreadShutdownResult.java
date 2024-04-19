package me.qeklydev.scoreboard.thread.result;

import org.jetbrains.annotations.NotNull;

/**
 * This class is used to proportionate multiple status codes
 * based on the operations result.
 *
 * @param status the status code.
 * @since 0.0.1
 */
public record ExecutorThreadShutdownResult(byte status) {
  /**
   * The executor was already shutdown early.
   *
   * @since 0.0.1
   */
  public static final byte ALREADY_SHUTDOWN = 0;
  /**
   * The executor was shutdown with an await-termination
   * for pending non-processed tasks.
   *
   * @since 0.0.1
   */
  public static final byte SHUTDOWN_WITH_TERMINATION = 1;
  /**
   * The executor was shutdown immediate without
   *
   * @since 0.0.1
   */
  public static final byte SHUTDOWN_IMMEDIATE = 2;
  /**
   * Something went wrong during the shutdown process.
   *
   * @since 0.0.1
   */
  public static final byte ERROR_STATUS = 3;

  /**
   * Creates a new executor thread shutdown result with the
   * status 'ALREADY_SHUTDOWN' type.
   *
   * @return The {@link ExecutorThreadShutdownResult} with
   *     the {@link ExecutorThreadShutdownResult#ALREADY_SHUTDOWN}
   *     status.
   * @since 0.0.1
   */
  public static @NotNull ExecutorThreadShutdownResult withAlreadyShutdown() {
    return new ExecutorThreadShutdownResult(ALREADY_SHUTDOWN);
  }

  /**
   * Creates a new executor thread shutdown result with the
   * status 'SHUTDOWN_WITH_TERMINATION' type.
   *
   * @return The {@link ExecutorThreadShutdownResult} with
   *     the {@link ExecutorThreadShutdownResult#SHUTDOWN_WITH_TERMINATION}
   *     status.
   * @since 0.0.1
   */
  public static @NotNull ExecutorThreadShutdownResult withShutdownWithTermination() {
    return new ExecutorThreadShutdownResult(SHUTDOWN_WITH_TERMINATION);
  }

  /**
   * Creates a new executor thread shutdown result with the
   * status 'SHUTDOWN_IMMEDIATE' type.
   *
   * @return The {@link ExecutorThreadShutdownResult} with
   *     the {@link ExecutorThreadShutdownResult#SHUTDOWN_IMMEDIATE}
   *     status.
   * @since 0.0.1
   */
  public static @NotNull ExecutorThreadShutdownResult withShutdownImmediate() {
    return new ExecutorThreadShutdownResult(SHUTDOWN_IMMEDIATE);
  }

  /**
   * Creates a new executor thread shutdown result with the
   * status 'ERROR_STATUS' type.
   *
   * @return The {@link ExecutorThreadShutdownResult} with
   *     the {@link ExecutorThreadShutdownResult#ERROR_STATUS}.
   * @since 0.0.1
   */
  public static @NotNull ExecutorThreadShutdownResult withError() {
    return new ExecutorThreadShutdownResult(ERROR_STATUS);
  }

  /**
   * Returns whether the status type is 'ALREADY_SHUTDOWN'.
   *
   * @return Whether the status type is
   *     {@link ExecutorThreadShutdownResult#ALREADY_SHUTDOWN}.
   * @since 0.0.1
   */
  public boolean alreadyShutdown() {
    return this.status == ALREADY_SHUTDOWN;
  }

  /**
   * Returns whether the status type is 'SHUTDOWN_WITH_TERMINATION'.
   *
   * @return Whether the status type is
   *     {@link ExecutorThreadShutdownResult#SHUTDOWN_WITH_TERMINATION}.
   * @since 0.0.1
   */
  public boolean shutdownWithTermination() {
    return this.status == SHUTDOWN_WITH_TERMINATION;
  }

  /**
   * Returns whether the status type is 'SHUTDOWN_IMMEDIATE'.
   *
   * @return Whether the status type is
   *     {@link ExecutorThreadShutdownResult#SHUTDOWN_IMMEDIATE}.
   * @since 0.0.1
   */
  public boolean shutdownImmediate() {
    return this.status == SHUTDOWN_IMMEDIATE;
  }

  /**
   * Returns whether the status type is 'ERROR'.
   *
   * @return Whether the status type is
   *     {@link ExecutorThreadShutdownResult#ERROR_STATUS}.
   * @since 0.0.1
   */
  public boolean failed() {
    return this.status == ERROR_STATUS;
  }

  @Override
  public @NotNull String toString() {
    return switch (this.status) {
      case ALREADY_SHUTDOWN -> "0 (Thread Already Shutdown)";
      case SHUTDOWN_WITH_TERMINATION -> "1 (Thread Normal Shutdown)";
      case SHUTDOWN_IMMEDIATE -> "2 (Thread Shutdown Immediate)";
      case ERROR_STATUS -> "3 (Thread Shutdown Error)";
      default -> throw new IllegalStateException(this.status + " (Unexpected value)");
    };
  }
}
