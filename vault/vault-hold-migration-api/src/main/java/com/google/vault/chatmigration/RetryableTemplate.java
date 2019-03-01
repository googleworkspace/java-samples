package com.google.vault.chatmigration;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetryableTemplate {
  private static Logger logger = Logger.getLogger(RetryableTemplate.class.getName());
  private static ImmutableList<Integer> DONT_RETRY = ImmutableList.of(400, 401, 404, 409);

  public static <T> T callWithRetry(Callable<T> callable)
      throws ExecutionException, RetryException {
    Retryer<T> retryer =
        RetryerBuilder.<T>newBuilder()
            .retryIfException(
                input -> {
                  if (input instanceof GoogleJsonResponseException) {
                    GoogleJsonResponseException jsonException = (GoogleJsonResponseException) input;
                    int responseCode = jsonException.getDetails().getCode();
                    if (DONT_RETRY.contains(responseCode)) {
                      logger.log(
                          Level.WARNING,
                          "Encountered Non Retryable Error: " + jsonException.getMessage());
                      return false;
                    } else {
                      logger.log(
                          Level.WARNING,
                          "Encountered retryable error: "
                              + jsonException.getMessage()
                              + ".Retrying...");
                      return true;
                    }
                  } else {
                    logger.log(
                        Level.WARNING,
                        "Encountered error: " + input.getMessage() + ". Retrying...");
                    return true;
                  }
                })
            .withWaitStrategy(WaitStrategies.fixedWait(40, TimeUnit.SECONDS))
            .withStopStrategy(StopStrategies.stopAfterAttempt(1000))
            .build();
    return retryer.call(callable);
  }
}
