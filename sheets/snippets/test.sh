#!/bin/bash
export GOOGLE_APPLICATION_CREDENTIALS="$(pwd)/../application_credentials.json";
./gradlew test --rerun-tasks # --rerun-tasks forces UP-TO-DATE tests to re-run
