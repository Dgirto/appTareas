# Fix Gradle and AGP Version Incompatibility

The project is currently experiencing a sync error because the Gradle version (8.14.5) is incompatible with the Java version (25) used by the IDE. According to the error message, Gradle 8.14.5 supports only up to Java 24.

To fix this, I will upgrade the Gradle wrapper to version 9.7.1 and the Android Gradle Plugin (AGP) to version 9.4.0, which are compatible with Java 25.

## Proposed Changes

### Build Configuration

#### [MODIFY] [gradle-wrapper.properties](file:///C:/Users/Alumno-ETI/Desktop/appTareas/gradle/wrapper/gradle-wrapper.properties)
- Upgrade `distributionUrl` from `gradle-8.14.5-bin.zip` to `gradle-9.7.1-bin.zip`.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/Alumno-ETI/Desktop/appTareas/build.gradle.kts)
- Upgrade Android Gradle Plugin version from `8.2.2` to `9.4.0`.

## Verification Plan

### Automated Tests
- Run `./gradlew help` to verify that Gradle can now run with the current JVM.
- Perform a Gradle Sync to ensure the project configuration is valid.
