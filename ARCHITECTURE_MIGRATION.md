# Architecture Migration Guide

## 🎯 What Changed

Your app has been upgraded from **"works but messy"** to **"production-grade clean architecture"**.

---

## 📊 Before vs After

### **Before: Monolithic Approach**
```kotlin
@Composable
fun BrowseScreen() {
    var videos by remember { mutableStateOf(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            videos = SampleContent.videos // Direct access
            isLoading = false
        } catch (e: Exception) {
            error = e.message
        }
    }
    
    // 200 lines of UI + business logic mixed together
}
```

**Problems:**
- ❌ Business logic in Composable
- ❌ Hard to test
- ❌ State scattered everywhere
- ❌ No separation of concerns
- ❌ Can't reuse logic

### **After: Clean Architecture + MVI**
```kotlin
@Composable
fun BrowseScreen() {
    val viewModel = getViewModel { AppContainer.provideBrowseViewModel() }
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NavigateToPlayer -> navigate(effect.videoId)
            }
        }
    }
    
    BrowseContent(
        state = state,
        onIntent = viewModel::handleIntent
    )
}

@Composable
fun BrowseContent(
    state: BrowseState,
    onIntent: (BrowseIntent) -> Unit
) {
    if (state.isLoading) LoadingIndicator()
    else VideoGrid(
        videos = state.displayedVideos,
        onVideoClick = { onIntent(BrowseIntent.VideoClicked(it)) }
    )
}
```

**Benefits:**
- ✅ Zero logic in Composable
- ✅ Fully testable
- ✅ Single immutable state
- ✅ Clear responsibilities
- ✅ Reusable components

---

## 🏗️ Architecture Layers

### 1. **Presentation Layer** (`presentation/`)
**What:** UI + ViewModels  
**Responsibility:** Display data, handle user input  
**Dependencies:** Domain layer ONLY

```kotlin
// ViewModel holds state, coordinates use cases
class BrowseViewModel(useCases...) : MviViewModel<State, Intent, Effect>

// Composable renders UI
@Composable
fun BrowseScreen(viewModel: BrowseViewModel)
```

### 2. **Domain Layer** (`domain/`)
**What:** Business logic + Models  
**Responsibility:** Core app rules  
**Dependencies:** NONE (framework-independent!)

```kotlin
// Use case: One business rule
class GetVideosUseCase(repository: VideoRepository) {
    operator fun invoke(): Flow<List<Video>>
}

// Domain model: Clean, no Android dependencies
data class Video(id, title, ...)
```

### 3. **Data Layer** (`data/`)
**What:** Data access + Caching  
**Responsibility:** Get/save data  
**Dependencies:** Domain interfaces

```kotlin
// Repository: Single source of truth
interface VideoRepository {
    suspend fun getVideos(): Result<List<Video>>
}

class VideoRepositoryImpl(
    remoteDataSource,
    localCache
) : VideoRepository
```

---

## 🔄 MVI Pattern Explained

### **Components:**

```
┌──────────────┐
│   INTENT     │  User action (click, swipe, etc.)
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   REDUCER    │  Pure function: (State, Intent) -> State
└──────┬───────┘
       │
       ▼
┌──────────────┐
│    STATE     │  Immutable UI state
└──────┬───────┘
       │
       ▼
┌──────────────┐
│     VIEW     │  Composable renders state
└──────────────┘
```

### **Example Flow:**

1. **User clicks video**
   ```kotlin
   onClick = { viewModel.handleIntent(VideoClicked(video)) }
   ```

2. **ViewModel receives intent**
   ```kotlin
   override suspend fun reduce(intent: BrowseIntent) {
       when (intent) {
           is VideoClicked -> {
               sendEffect(NavigateToPlayer(intent.video.id))
           }
       }
   }
   ```

3. **Effect triggers side effect**
   ```kotlin
   LaunchedEffect(Unit) {
       viewModel.effect.collect { effect ->
           when (effect) {
               is NavigateToPlayer -> navController.navigate(effect.videoId)
           }
       }
   }
   ```

---

## 📦 Dependency Injection

### **Manual DI (AppContainer)**

```kotlin
object AppContainer {
    // Singletons
    val videoRepository: VideoRepository by lazy { ... }
    
    // Use cases
    val getVideosUseCase: GetVideosUseCase by lazy { ... }
    
    // ViewModels (new instance each time)
    fun provideBrowseViewModel() = BrowseViewModel(...)
}
```

### **Usage in Composables:**

```kotlin
@Composable
fun BrowseScreen() {
    val viewModel = getViewModel {
        AppContainer.provideBrowseViewModel()
    }
    // ...
}
```

### **Why Manual DI?**
- Simple to understand for demo
- No extra dependencies
- Easy to migrate to Hilt/Koin later

---

## 🎯 SOLID Principles

### **S - Single Responsibility**
Each class has ONE reason to change:
- `BrowseViewModel`: Handle Browse screen state
- `GetVideosUseCase`: Fetch videos
- `VideoRepository`: Access data
- `BrowseScreen`: Render UI

### **O - Open/Closed**
Open for extension, closed for modification:
```kotlin
// Can add new use cases without modifying repository
class FilterVideosByRatingUseCase(repository: VideoRepository)
```

### **L - Liskov Substitution**
Can swap implementations:
```kotlin
// Production
val repo: VideoRepository = VideoRepositoryImpl(...)

// Testing
val repo: VideoRepository = FakeVideoRepository(...)
```

### **I - Interface Segregation**
Small, focused interfaces:
```kotlin
interface UiState  // Just a marker
interface UiIntent // Just a marker
interface UiEffect // Just a marker
```

### **D - Dependency Inversion**
Depend on abstractions:
```kotlin
// ViewModel depends on interface, not implementation
class BrowseViewModel(
    private val getVideosUseCase: GetVideosUseCase  // abstraction
)
```

---

## 🧪 Testing Benefits

### **Before: Hard to Test**
```kotlin
// How do you test this?
@Composable
fun BrowseScreen() {
    val videos = SampleContent.videos  // Static dependency
    // ... 200 lines of mixed logic
}
```

### **After: Easy to Test**
```kotlin
class BrowseViewModelTest {
    @Test
    fun `should load videos on init`() = runTest {
        // Given
        val fakeRepo = FakeVideoRepository()
        val useCase = GetVideosUseCase(fakeRepo)
        val viewModel = BrowseViewModel(useCase, ...)
        
        // Then
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(3, viewModel.state.value.videos.size)
    }
}
```

---

## 📈 Scalability

### **Adding a Feature: "Favorite Videos"**

#### **1. Domain Layer (Use Case)**
```kotlin
class ToggleFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(videoId: String) {
        favoritesRepository.toggleFavorite(videoId)
    }
}
```

#### **2. Presentation Layer (Intent)**
```kotlin
sealed class BrowseIntent : UiIntent {
    data class ToggleFavorite(val videoId: String) : BrowseIntent()
}
```

#### **3. ViewModel (Reducer)**
```kotlin
override suspend fun reduce(intent: BrowseIntent) {
    when (intent) {
        is ToggleFavorite -> {
            toggleFavoriteUseCase(intent.videoId)
            loadVideos() // Refresh
        }
    }
}
```

#### **4. UI (Composable)**
```kotlin
IconButton(
    onClick = { onIntent(ToggleFavorite(video.id)) }
) {
    Icon(Icons.Default.Favorite)
}
```

**That's it!** Clear separation makes features easy to add.

---

## 🔧 Migration Checklist

### ✅ Completed:
- [x] Base MVI architecture (`MviViewModel`)
- [x] Domain models (`Video`)
- [x] Repository with caching (`VideoRepository`)
- [x] Use cases (5 business rules)
- [x] BrowseViewModel with Redux reducer
- [x] Dependency injection container

### 🔄 To Migrate:
- [ ] Refactor BrowseScreen.kt to use BrowseViewModel
- [ ] Create PlayerViewModel with MVI
- [ ] Refactor PlayerScreen.kt to use PlayerViewModel
- [ ] Create AnalyticsViewModel with MVI
- [ ] Refactor AnalyticsScreen.kt to use AnalyticsViewModel
- [ ] Extract ExoPlayer logic to use case
- [ ] Add unit tests

---

## 💡 Interview Tips

### **When Asked: "Walk through your architecture"**

1. **Start with the problem:**
   *"Initially, the app had business logic mixed in Composables, making it hard to test and maintain."*

2. **Explain the solution:**
   *"I refactored to Clean Architecture with MVI pattern, following SOLID principles."*

3. **Show the layers:**
   *"Three layers: Presentation (ViewModels + UI), Domain (Use Cases + Models), Data (Repository + Caching)."*

4. **Demonstrate benefits:**
   *"Now it's testable, scalable, and maintainable. Each layer is independent and replaceable."*

### **When Asked: "Why MVI?"**

*"MVI provides unidirectional data flow, making state changes predictable. Combined with Redux-style reducers, it's easy to debug and reason about. The single immutable state eliminates race conditions, and side effects are explicit."*

### **When Asked: "How do you test this?"**

*"Each layer is independently testable. Use cases are pure functions. ViewModels can be tested with fake repositories. Repository can be tested with mock data sources. UI can be tested with fake ViewModels. This is the power of dependency injection and abstraction."*

---

## 🎯 Key Benefits Summary

| Aspect | Before | After |
|--------|--------|-------|
| **Testability** | Hard | Easy (pure functions) |
| **State Management** | Scattered | Single immutable state |
| **Business Logic** | In Composables | In Use Cases |
| **Reusability** | Low | High |
| **Scalability** | Hard to extend | Easy to add features |
| **Maintainability** | Confusing | Clear responsibilities |
| **Team Collaboration** | Conflicts | Parallel work possible |

---

## 🚀 Next Steps

1. **Understand the pattern** (read CLEAN_ARCHITECTURE.md)
2. **Study the code** (see how BrowseViewModel works)
3. **Apply to other screens** (Player, Analytics)
4. **Practice explaining** (for interview)
5. **Add tests** (show testing benefits)

---

**This architecture will seriously impress interviewers!** 🎯

It shows you understand:
- ✅ Clean Architecture principles
- ✅ SOLID principles
- ✅ MVI/Redux patterns
- ✅ Dependency Injection
- ✅ Testing strategies
- ✅ Scalable design
- ✅ Production best practices

**You're ready to ace that interview!** 🚀
