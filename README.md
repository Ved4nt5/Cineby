# Cineby TV

Production-focused Android TV OTT app built with Kotlin + Jetpack Compose.

## Highlights
- Android TV-first UI with dark premium layout, remote focus indicators, and smooth Compose animations.
- Home, details, search, settings, and ExoPlayer playback screens.
- Dynamic source URL manager with validation, fallback list, connection test, and import/export support.
- MVVM + Repository + Hilt + Coroutines/Flow architecture.
- Retrofit/OkHttp networking, Room watch history/favorites, Coil image loading, and prefetch/caching hooks.

## Tech Stack
- Kotlin, Jetpack Compose (Material 3 + TV foundations)
- Hilt DI
- Retrofit + OkHttp
- Room
- DataStore
- Media3 ExoPlayer
- Coil

## Project Structure
- `/app/src/main/java/com/cineby/tv/data` data layer (network/local/repository/source manager)
- `/app/src/main/java/com/cineby/tv/presentation` view models
- `/app/src/main/java/com/cineby/tv/ui` Compose UI, navigation, player, and components

## Build
```bash
./gradlew assembleDebug
```

## Test
```bash
./gradlew testDebugUnitTest
```

## Notes
- Default source is `https://cineby.at`.
- Fallback source switching is automatic when active source is unavailable.
- Import/export source config is JSON-based and available from Settings.
