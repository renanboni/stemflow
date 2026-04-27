# Stemflow

Stemflow is an Android app test built for Moises.ai with Kotlin and Jetpack
Compose. It searches the iTunes API, shows track results, plays previews, keeps
a small recently played list, and lets users open the album for a track.

## Tech

- Kotlin
- Jetpack Compose + Material 3
- Navigation 3
- Hilt
- Room
- Retrofit + kotlinx.serialization
- Paging 3
- Media3
- Coil
- JUnit, Turbine, Room tests, Paging tests

## Project Layout

```text
app/                 App entry point and navigation
core/common/         Shared utilities, dispatchers, connectivity, clock
core/data/           Repository implementations and local/remote coordination
core/database/       Room database, DAOs, entities
core/designsystem/   Theme, shared UI primitives, icons
core/domain/         Domain models and repository contracts
core/network/        iTunes API service, DTOs, remote data source
core/testing/        Test fixtures, rules, fakes
core/ui/             Reusable app-level UI components
feature/album/       Album screen and state
feature/library/     Search and recently played screen
feature/player/      Preview player screen and playback state
```

## Running

Open the project in Android Studio and run the `app` configuration, or use:

```bash
./gradlew :app:installDebug
```

The app uses the public iTunes Search API, so search and album loading need a
network connection. Recently played tracks are stored locally with Room.

## Tests

Run the JVM unit tests:

```bash
./gradlew testDebugUnitTest
```

Compile the Android test sources:

```bash
./gradlew :core:data:compileDebugAndroidTestKotlin
```

There are instrumentation tests for the Room DAOs and album repository. Run them
from Android Studio or with a connected device/emulator:

```bash
./gradlew connectedDebugAndroidTest
```

## Notes

The iTunes Search API does not page like a typical offset-based API. The search
paging source works around that by requesting a larger result limit and emitting
only the new items it has not seen yet.

Album loading is cache-first: if an album is already stored locally, it is shown
immediately while a refresh runs. If there is no cached album and the refresh
fails, the album screen shows an error with retry.
