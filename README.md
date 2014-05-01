Calendar API Sync Samples
=========================

A set of samples that demonstrate how to utilize sync tokens and etags in the calendar API to sync
resources more efficiently.

Introduction
------------

To make it easier for your applications to stay in sync with your users' Google Calendar data the
API has added support for sync tokens. These tokens store information about the data you've already
retrieved, so that the next time you make a request you'll only be given the resources that have
been added, updated, or deleted since your last sync. These samples demonstrate how to sync 
efficiently, utilizing sync tokens as well as resource versioning (etags).

Prerequisites
--------------

Read the following guides:

- [Syncing Guide](https://developers.google.com/google-apps/calendar/v3/sync)
- [Resource Versioning Guide](https://developers.google.com/google-apps/calendar/v3/version-resources)

Setup your Java environment:

- Install [JDK 1.6 or higher](http://www.oracle.com/technetwork/java/javase/downloads)
- Install [Apache Maven](http://maven.apache.org)

Getting Started
---------------

1. Edit `client_secrets.json` and set the client ID and secret. You can create an ID/secret pair
   using the [Google Developers Console](https://console.developers.google.com).
2. Run `mvn compile` to build the project.
3. Run one of the three samples:
   * Sync Token Sample:
     `mvn exec:java -Dexec.mainClass="com.google.api.services.samples.calendar.sync.SyncTokenSample"`
   * Conditional Modification Sample:
     `mvn exec:java -Dexec.mainClass="com.google.api.services.samples.calendar.sync.ConditionalModificationSample"`
   * Conditional Retrieval Sample:
     `mvn exec:java -Dexec.mainClass="com.google.api.services.samples.calendar.sync.ConditionalRetrievalSample"`

Support
-------

- Stack Overflow Tag: [google-calendar](http://stackoverflow.com/questions/tagged/google-calendar)
- Issue Tracker: [apps-api-issues](https://code.google.com/a/google.com/p/apps-api-issues/issues/list)

If you've found an error in this sample, please file an issue:
https://github.com/googlesamples/calendar-sync/issues

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub.

License
-------

Copyright 2014 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.