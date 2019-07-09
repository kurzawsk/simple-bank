#!/bin/sh
mvn sonar:sonar \
-Dsonar.projectKey=kurzawsk_simple-bank \
-Dsonar.organization=kurzawsk-github \
-Dsonar.host.url=https://sonarcloud.io \
-Dsonar.junit.reportPaths=target/surfire-reports \
-Dsonar.jacoco.reportPaths=target/jacoco.exec \
-Dsonar.login=${SONAR_LOGIN}