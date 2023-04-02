/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
