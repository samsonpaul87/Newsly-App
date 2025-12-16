# Newsly

A minimal, modern Android news app focused on reading news quickly and comfortably.

## Features

- **Top Headlines**: Browse the latest news from top sources
- **Search**: Find articles by keyword
- **Bookmarks**: Save articles for offline reading
- **Clean Design**: Material Design 3 with focus on readability
- **Share**: Easily share articles with friends

## Architecture

Newsly follows Clean Architecture principles with MVVM pattern:

```
app/
├── data/
│   ├── remote/
│   │   ├── api/          # Retrofit API interface
│   │   ├── dto/          # Data Transfer Objects
│   │   └── mapper/       # DTO to Domain mappers
│   ├── local/
│   │   └── database/     # Room database for bookmarks
│   └── repository/       # Repository implementations
│
├── domain/
│   ├── model/            # Domain models
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Business logic use cases
│
├── presentation/
│   ├── ui/
│   │   ├── activities/   # Activities
│   │   ├── fragments/    # Fragments
│   │   └── adapters/     # RecyclerView adapters
│   ├── viewmodel/        # ViewModels
│   └── state/            # UI state classes
│
└── di/                   # Hilt dependency injection modules
```

## Tech Stack

- **Language**: Kotlin
- **UI**: XML layouts with Material Design 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp
- **Database**: Room (for bookmarks)
- **Images**: Coil
- **Async**: Kotlin Coroutines + StateFlow
- **Navigation**: Jetpack Navigation Component

## Setup

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

### API Key Configuration

1. Get a free API key from [NewsAPI.org](https://newsapi.org/)
2. Open `local.properties` in the project root
3. Add your API key:
   ```properties
   NEWS_API_KEY=your_api_key_here
   ```

### Build and Run

1. Clone the repository
2. Open the project in Android Studio
3. Configure your API key as described above
4. Sync Gradle files
5. Run the app on an emulator or device (API 24+)

## Project Structure

### Data Layer
- `NewsApiService`: Retrofit interface for NewsAPI endpoints
- `NewsResponseDto`, `ArticleDto`: DTOs for API responses
- `ArticleMapper`: Converts DTOs to domain models
- `BookmarkEntity`: Room entity for local storage
- `BookmarkDao`: Data Access Object for bookmark operations

### Domain Layer
- `Article`: Core domain model representing a news article
- `NewsRepository`: Interface for news operations
- `BookmarkRepository`: Interface for bookmark operations
- Use Cases: `GetTopHeadlinesUseCase`, `SearchNewsUseCase`, `GetBookmarksUseCase`, `ToggleBookmarkUseCase`

### Presentation Layer
- `HomeViewModel`: Manages home screen state and logic
- `BookmarksViewModel`: Manages bookmarks screen state
- `ArticleDetailViewModel`: Manages article detail screen
- `ArticleAdapter`: RecyclerView adapter for article lists

## Screens

1. **Home Screen**
   - Top headlines list
   - Pull-to-refresh
   - Search functionality
   - Loading, error, and empty states

2. **Article Detail**
   - Hero image
   - Title, source, date, author
   - Article content
   - Share, bookmark, and open in browser actions

3. **Bookmarks**
   - List of saved articles
   - Offline access
   - Empty state when no bookmarks

## Testing

Run unit tests:
```bash
./gradlew test
```

Tests cover:
- ViewModels (HomeViewModel, BookmarksViewModel)
- Repositories (NewsRepository, BookmarkRepository)

## API Limits

NewsAPI free tier has the following limits:
- 100 requests per day
- Results limited to 100 articles per request
- Requests from browser (localhost/dev) may be blocked

For production use, consider upgrading to a paid plan.

## License

This project is for educational purposes.

## Credits

- News data provided by [NewsAPI.org](https://newsapi.org/)
- Icons from Material Design Icons
