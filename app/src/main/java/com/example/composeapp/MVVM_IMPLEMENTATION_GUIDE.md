# MVVM Architecture Implementation Guide

This document outlines the complete MVVM architecture implementation for the Study Cafe app using coroutines and LiveData, based on the provided OpenAPI specifications.

## Architecture Overview

The implementation follows the MVVM (Model-View-ViewModel) pattern with the following layers:

### 1. **Model Layer**
- **Network Models** (`ApiModels.kt`): Data classes representing API responses
- **Room Entities** (`CafeEntity.kt`): Local database entities  
- **Repositories** (`CafeRepository.kt`, `UserRepository.kt`): Data access abstraction

### 2. **View Layer**
- **Composables**: UI components that observe LiveData from ViewModels
- **MainActivity**: Entry point that sets up dependencies

### 3. **ViewModel Layer**
- **CafeViewModel**: Manages cafe-related UI state and business logic
- **UserViewModel**: Manages user-related UI state and business logic

## Key Components

### Network Layer

#### API Service (`ApiService.kt`)
```kotlin
interface ApiService {
    @GET("api/v1/cafes/")
    suspend fun getAllCafes(): List<Cafe>
    
    @GET("api/v1/cafes/search/")
    suspend fun searchCafes(@Query("query") query: String): List<Cafe>
    
    @POST("api/v1/login")
    suspend fun login(@Body credentials: UserLogin): LoginResponse
    
    // ... other endpoints based on OpenAPI spec
}
```

#### Network Module (`NetworkModule.kt`)
- Configures Retrofit with OkHttp client
- Provides API service instance
- Includes error handling utilities (`safeApiCall`)

### Repository Layer

#### Cafe Repository (`CafeRepository.kt`)
```kotlin
class CafeRepository(
    private val cafeDao: CafeDao,
    private val apiService: ApiService
) {
    fun getAllCafesLiveData(): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        // First emit cached data
        val cachedCafes = cafeDao.getAllCafesSync()
        if (cachedCafes.isNotEmpty()) {
            emit(ApiResult.Success(cachedCafes))
        }
        
        // Then fetch from API
        val apiResult = safeApiCall { apiService.getAllCafes() }
        apiResult.onSuccess { apiCafes ->
            val entities = apiCafes.map { it.toEntity() }
            cafeDao.deleteAllCafes()
            cafeDao.insertCafes(entities)
            emit(ApiResult.Success(entities))
        }
    }
}
```

### ViewModel Layer

#### Cafe ViewModel (`CafeViewModel.kt`)
```kotlin
class CafeViewModel(private val repository: CafeRepository) : ViewModel() {
    val allCafes: LiveData<ApiResult<List<CafeEntity>>> = repository.getAllCafesLiveData()
    
    private val _searchQuery = MutableLiveData<String>()
    val searchResults: LiveData<ApiResult<List<CafeEntity>>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) {
            repository.getAllCafesLiveData()
        } else {
            repository.searchCafesLiveData(query)
        }
    }
    
    fun searchCafes(query: String) {
        _searchQuery.value = query
    }
    
    fun refreshCafes() {
        viewModelScope.launch {
            // Handle refresh logic
        }
    }
}
```

## Key Features

### 1. **Coroutines Integration**
- All repository methods use `suspend` functions for network calls
- `withContext(Dispatchers.IO)` ensures database operations run on IO thread
- `viewModelScope.launch` for UI-triggered operations

### 2. **LiveData Usage**
- `liveData` builder for reactive data streams
- `switchMap` for transforming LiveData based on user inputs
- Automatic lifecycle management in Composables

### 3. **Error Handling**
- `ApiResult` sealed class wraps success/error states
- `safeApiCall` utility handles network exceptions
- Graceful fallback to cached data when API fails

### 4. **Caching Strategy**
- Room database serves as single source of truth
- API data automatically cached to database
- Offline-first approach with network sync

## Usage Examples

### In MainActivity
```kotlin
class MainActivity : ComponentActivity() {
    private val cafeViewModel: CafeViewModel by viewModels { viewModelFactory }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize dependencies
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CafeRepository(
            cafeDao = database.cafeDao(),
            apiService = NetworkModule.apiService
        )
        viewModelFactory = ViewModelFactory(repository)
        
        setContent {
            AppContent(cafeViewModel)
        }
    }
}
```

### In Composables
```kotlin
@Composable
fun AppContent(viewModel: CafeViewModel) {
    val cafesResult by viewModel.allCafes.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    
    when (cafesResult) {
        is ApiResult.Success -> {
            val cafes = cafesResult.data
            MainContent(
                cafeList = cafes,
                onRefresh = { viewModel.refreshCafes() },
                onSearch = { query -> viewModel.searchCafes(query) }
            )
        }
        is ApiResult.Error -> {
            ErrorScreen(message = cafesResult.message)
        }
        null -> LoadingScreen()
    }
}
```

## API Integration

The implementation is based on the Study Spot Reviews API with endpoints for:

- **Cafes**: CRUD operations, search, nearby locations, filtering
- **Users**: Authentication, profile management, user search
- **Reviews**: Create, read, update, delete reviews with photos
- **Bookmarks**: Manage user favorites
- **Files**: Upload photos and profile pictures

### Base URL
```kotlin
private const val BASE_URL = "https://study-cafe-api-p3evt.ondigitalocean.app/"
```

## Benefits

1. **Separation of Concerns**: Clear separation between UI, business logic, and data
2. **Testability**: Each layer can be unit tested independently
3. **Reactive UI**: Automatic UI updates when data changes
4. **Offline Support**: Works without internet connection using cached data
5. **Error Resilience**: Graceful handling of network failures
6. **Scalability**: Easy to add new features and endpoints

## Best Practices Implemented

- Repository pattern for data access abstraction
- LiveData for reactive programming
- Coroutines for asynchronous operations
- Dependency injection via ViewModelFactory
- Error handling with sealed classes
- Offline-first architecture
- Lifecycle-aware components

This implementation provides a robust, scalable foundation for the Study Cafe app that follows Android development best practices and ensures a smooth user experience. 