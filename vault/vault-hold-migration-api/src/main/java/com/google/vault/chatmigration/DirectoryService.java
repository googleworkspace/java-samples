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
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.OrgUnit;
import com.google.api.services.admin.directory.model.OrgUnits;
import com.google.api.services.admin.directory.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DirectoryService {
  private static final Logger logger = Logger.getLogger(DirectoryService.class.getName());

  private Directory directoryService;
  private Map<String, OrgUnit> orgUnits;
  private Map<String, User> users = new HashMap<>();

  public DirectoryService(Directory directoryService) {
    this.directoryService = directoryService;
    getOrgUnits();
  }

  public String getEmail(String accountId) {
    User user = users.get(accountId);
    if (user == null) {
      try {
        user =
            RetryableTemplate.callWithRetry(
                () -> directoryService.users().get(accountId).execute());
        users.put(accountId, user);
      } catch (ExecutionException e) {
        logger.log(Level.WARNING, "Unable to get email address for account Id " + accountId, e);
      } catch (RetryException e) {
        logger.log(Level.WARNING, "Unable to get email address for account Id " + accountId, e);
      }
    }
    return user != null ? user.getPrimaryEmail() : accountId;
  }

  public OrgUnit getOrgUnit(String orgUnitId) {
    return orgUnits.get(orgUnitId);
  }

  private void getOrgUnits() {
    OrgUnits response = null;
    try {
      response = directoryService.orgunits().list("my_customer").setType("all").execute();
      orgUnits =
          response.getOrganizationUnits().stream()
              .collect(Collectors.toMap(OrgUnit::getOrgUnitId, Function.identity()));
    } catch (IOException e) {
      logger.log(Level.WARNING, "Unable to get org units", e);
    }
  }
}
