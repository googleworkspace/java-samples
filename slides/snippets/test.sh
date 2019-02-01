#!/bin/bash
export GOOGLE_APPLICATION_CREDENTIALS="$(pwd)/../application_credentials.json";
./gradlew test