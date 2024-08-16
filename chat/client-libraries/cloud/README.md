# Google Chat API - Cloud Client library samples

## Setup

Add `service_account.json` and/or `client_secrets.json` to the current
folder depending on the credentials used by the samples to run:

1. `service_account.json` for
    [app credentials](https://developers.google.com/workspace/chat/authenticate-authorize-chat-app)

1. `client_secrets.json` for
    [user credentials](https://developers.google.com/workspace/chat/authenticate-authorize-chat-user)

## Run with Maven

Execute
`mvn exec:java -Dexec.mainClass="replace.with.the.sample.mainClass"`
wih the main class of the sample.

For example, to run the sample `CreateMessageAppCred`, your should run
`mvn exec:java -Dexec.mainClass="com.google.workspace.api.chat.samples.CreateMessageAppCred"`.

## Run with Gradle

Execute `gradle run` after setting the main class of the sample in the `build.gradle` file.

For example, to run the sample `CreateMessageAppCred`, your should use
`com.google.workspace.api.chat.samples.CreateMessageAppCred`.
