# Calendar API Sync Samples

A set of samples that demonstrate how to utilize sync tokens and etags in the calendar API to sync
resources more efficiently.

## Introduction

To make it easier for your applications to stay in sync with your users' Google Calendar data the
API has added support for sync tokens. These tokens store information about the data you've already
retrieved, so that the next time you make a request you'll only be given the resources that have
been added, updated, or deleted since your last sync. These samples demonstrate how to sync
efficiently, utilizing sync tokens as well as resource versioning (etags).

## Prerequisites

Read the following guides:

- [Syncing Guide](https://developers.google.com/google-apps/calendar/v3/sync)
- [Resource Versioning Guide](https://developers.google.com/google-apps/calendar/v3/version-resources)

Setup your Java environment:

- Install [JDK 1.6 or higher](http://www.oracle.com/technetwork/java/javase/downloads)
- Install [Apache Maven](http://maven.apache.org)

## Getting Started

1. Edit `client_secrets.json` and set the client ID and secret. You can create an ID/secret pair
   using the [Google Developers Console](https://console.developers.google.com).
1. Run `mvn compile` to build the project.
1. Run one of the three samples:
   * Sync Token Sample:
     `mvn exec:java -Dexec.mainClass="com.google.api.services.samples.calendar.sync.SyncTokenSample"`
   * Conditional Modification Sample:
     `mvn exec:java -Dexec.mainClass="com.google.api.services.samples.calendar.sync.ConditionalModificationSample"`
   * Conditional Retrieval Sample:
     `mvn exec:java -Dexec.mainClass="com.google.api.services.samples.calendar.sync.ConditionalRetrievalSample"`
