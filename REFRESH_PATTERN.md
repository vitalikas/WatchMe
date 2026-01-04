# 🔄 Refresh vs Load Pattern

## ✅ Fixed: Proper Cache Invalidation

Great catch! The refresh method now **properly invalidates the cache** and forces a fresh fetch.

---

## 🎯 The Difference

### `loadVideos()` - Smart Loading
```kotlin
// Uses cache if valid (< 5 minutes old)
// Only fetches from network if cache expired
```

**Flow:**
1. Check cache
2. If valid → return cached data ✅ (FAST)
3. If expired → fetch from network
4. Update cache
5. Return fresh data

**Use when:** Initial load, navigation

### `refreshVideos()` - Force Refresh  
```kotlin
// ALWAYS fetches fresh data
// Invalidates cache first
```

**Flow:**
1. **Invalidate cache** (mark as expired)
2. Fetch from network (forced)
3. Update cache with fresh data
4. Return fresh data

**Use when:** User pulls to refresh, explicit refresh button

---

## 📝 Implementation

### Repository Layer

```kotlin
// Get with cache
override suspend fun getVideos(): Result<List<Video>> {
    // Check cache first
    if (cache valid) return cache
    
    // Cache invalid, fetch fresh
    return fetchAndCache()
}

// Force refresh
override suspend fun refreshVideos(): Result<List<Video>> {
    cache.invalidate() // ← Force cache to be invalid
    return fetchAndCache()
}
```

### Cache Layer

```kotlin
override fun invalidate() {
    // Mark as expired WITHOUT clearing data
    // Allows fallback if network fails
    lastFetchTime = 0L
}

override fun clear() {
    // Completely remove all data
    cache.clear()
    lastFetchTime = 0L
}
```

**Why two methods?**
- `invalidate()`: Keep data for fallback (network fails → still show old data)
- `clear()`: Remove everything (logout, reset app)

---

## 🎯 ViewModel Changes

### Before (Wrong ❌)
```kotlin
private fun refreshVideos() {
    setState { copy(isLoading = true) }
    loadVideos() // ← Just calls load, uses cache!
}
```

**Problem:** Refresh uses cache if valid, so user doesn't see fresh data!

### After (Correct ✅)
```kotlin
private fun refreshVideos() {
    viewModelScope.launch {
        setState { copy(isLoading = true) }
        
        // Uses RefreshVideosUseCase instead
        refreshVideosUseCase().fold(
            onSuccess = { videos ->
                // Update state with fresh data
            },
            onFailure = { error ->
                // Handle error
            }
        )
    }
}
```

**Benefits:**
- Actually fetches fresh data
- Invalidates cache first
- Proper error handling
- User sees latest content

---

## 📊 Comparison Table

| Aspect | `loadVideos()` | `refreshVideos()` |
|--------|----------------|-------------------|
| **Cache Check** | ✅ Yes | ❌ No (invalidates) |
| **Network Call** | Only if cache expired | Always |
| **Speed** | Fast (if cached) | Slower (always network) |
| **Freshness** | Might be stale | Guaranteed fresh |
| **Use Case** | Initial load | User refresh |
| **User Trigger** | App launch, navigation | Pull to refresh |

---

## 🎯 When to Use Each

### Use `getVideos()` / `loadVideos()`:
- ✅ App startup
- ✅ Screen navigation
- ✅ Coming back from background
- ✅ When speed matters
- ✅ When slightly stale data is okay

### Use `refreshVideos()`:
- ✅ Pull-to-refresh gesture
- ✅ Explicit refresh button
- ✅ After making changes
- ✅ When user wants latest data
- ✅ After network reconnection

---

## 🔄 Pull-to-Refresh Example

### In Composable
```kotlin
val pullRefreshState = rememberPullRefreshState(
    refreshing = state.isLoading,
    onRefresh = { 
        viewModel.handleIntent(BrowseIntent.Refresh) // ← Triggers refresh
    }
)

Box(
    Modifier.pullRefresh(pullRefreshState)
) {
    LazyVerticalGrid { /* content */ }
    
    PullRefreshIndicator(
        refreshing = state.isLoading,
        state = pullRefreshState
    )
}
```

---

## 🎯 Cache Strategy Benefits

### Smart Caching
```kotlin
// First load (no cache)
loadVideos() → network call → cache saved → show data

// Second load (cache valid)
loadVideos() → return cache → instant! ⚡

// User refreshes
refreshVideos() → invalidate → network call → fresh data → cache updated

// Third load (cache valid again)
loadVideos() → return cache → instant! ⚡
```

### Network Failure Handling
```kotlin
refreshVideos() {
    cache.invalidate()
    try {
        fetch fresh data
        update cache
    } catch (NetworkError) {
        // Still return old cached data
        // Better than showing error!
        return cache.getVideos()
    }
}
```

**User sees:** Stale data > No data

---

## 🧪 Testing Scenarios

### Test 1: First Load
```kotlin
@Test
fun `first load fetches from network`() {
    // Cache empty
    viewModel.loadVideos()
    
    // Verify: network called
    verify(remoteDataSource).fetchVideos()
}
```

### Test 2: Second Load Uses Cache
```kotlin
@Test
fun `second load uses cache`() {
    // Cache valid
    viewModel.loadVideos()
    
    // Verify: network NOT called
    verify(remoteDataSource, never()).fetchVideos()
}
```

### Test 3: Refresh Bypasses Cache
```kotlin
@Test
fun `refresh always fetches from network`() {
    // Cache valid
    viewModel.refreshVideos()
    
    // Verify: network called even with valid cache
    verify(cache).invalidate()
    verify(remoteDataSource).fetchVideos()
}
```

---

## 💡 Interview Talking Points

### "Why separate load and refresh?"

*"They serve different purposes:*

- ***Load** is optimized for speed - uses cache if valid, only fetches if needed. Great for UX.*
- ***Refresh** guarantees freshness - always gets latest data when user explicitly requests it.*

*This pattern is common in production apps like Twitter, Instagram - they show cached content instantly, but refresh gives you latest."*

### "What if network fails during refresh?"

*"We invalidate cache but keep the data. If network fails, we return the stale cache as fallback. User sees old data rather than an error - better UX. We also show an error message so they know it's not fresh."*

### "How does this help performance?"

*"Cache reduces server load and improves response time. If we refetched on every screen view, that's expensive. But users also need fresh data sometimes. This pattern gives us both - speed AND freshness when needed."*

---

## ✅ Summary

**What Changed:**
- ✅ Added `refreshVideos()` to repository
- ✅ Added `invalidate()` to cache
- ✅ Created `RefreshVideosUseCase`
- ✅ Updated ViewModel to actually refresh
- ✅ Load and Refresh are now distinct

**Benefits:**
- Proper cache invalidation
- True fresh data on refresh
- Better user experience
- Network failure handling
- Production-ready pattern

**This is how real apps like Twitter, Instagram, and TV2 Play handle it!** 🎯🚀
