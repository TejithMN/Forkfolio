package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CloudSyncState
import com.example.data.CloudSyncStatus
import com.example.data.FirebaseSyncManager
import com.example.data.RecipeEntity
import com.example.data.RecipeRepository
import com.example.util.UrlParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ForkfolioTab {
    ALL_RECIPES,
    FAVORITES,
    FAMILY_UPLOADS
}

class ForkfolioViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    private val sharedPrefs = application.getSharedPreferences("forkfolio_prefs", Context.MODE_PRIVATE)

    private val _currentTab = MutableStateFlow(ForkfolioTab.ALL_RECIPES)
    val currentTab: StateFlow<ForkfolioTab> = _currentTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedPlatform = MutableStateFlow("ALL") // ALL, YOUTUBE, INSTAGRAM
    val selectedPlatform: StateFlow<String> = _selectedPlatform.asStateFlow()

    private val _selectedDietary = MutableStateFlow("ALL") // ALL, VEG, NON_VEG
    val selectedDietaryFilter: StateFlow<String> = _selectedDietary.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _adminPassword = MutableStateFlow(
        sharedPrefs.getString("admin_pin", null) ?: "impriya".also {
            sharedPrefs.edit().putString("admin_pin", it).apply()
        }
    )
    val adminPassword: StateFlow<String> = _adminPassword.asStateFlow()

    private val _selectedRecipeForDetail = MutableStateFlow<RecipeEntity?>(null)
    val selectedRecipeForDetail: StateFlow<RecipeEntity?> = _selectedRecipeForDetail.asStateFlow()

    private val _selectedRecipeForEdit = MutableStateFlow<RecipeEntity?>(null)
    val selectedRecipeForEdit: StateFlow<RecipeEntity?> = _selectedRecipeForEdit.asStateFlow()

    private val _showAddEditDialog = MutableStateFlow(false)
    val showAddEditDialog: StateFlow<Boolean> = _showAddEditDialog.asStateFlow()

    private val _showAdminLoginDialog = MutableStateFlow(false)
    val showAdminLoginDialog: StateFlow<Boolean> = _showAdminLoginDialog.asStateFlow()

    private val _showAdminSettingsDialog = MutableStateFlow(false)
    val showAdminSettingsDialog: StateFlow<Boolean> = _showAdminSettingsDialog.asStateFlow()

    private val _adminLoginError = MutableStateFlow<String?>(null)
    val adminLoginError: StateFlow<String?> = _adminLoginError.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        val syncManager = FirebaseSyncManager(application, db.recipeDao(), viewModelScope)
        repository = RecipeRepository(db.recipeDao(), syncManager)
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    val syncState: StateFlow<CloudSyncState> = repository.syncState

    val allRecipesRaw: StateFlow<List<RecipeEntity>> = repository.allRecipes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class FilterCriteria(
        val tab: ForkfolioTab,
        val category: String,
        val platform: String,
        val dietary: String,
        val query: String
    )

    private val filterCriteria: Flow<FilterCriteria> = combine(
        _currentTab,
        _selectedCategory,
        _selectedPlatform,
        _selectedDietary,
        _searchQuery
    ) { tab, category, platform, dietary, query ->
        FilterCriteria(tab, category, platform, dietary, query)
    }

    val filteredRecipes: StateFlow<List<RecipeEntity>> = combine(
        allRecipesRaw,
        filterCriteria
    ) { recipes, criteria ->
        recipes.filter { recipe ->
            // Tab filtering
            val matchesTab = when (criteria.tab) {
                ForkfolioTab.ALL_RECIPES -> true
                ForkfolioTab.FAVORITES -> recipe.isFavorite
                ForkfolioTab.FAMILY_UPLOADS -> recipe.uploaderName.isNotBlank()
            }

            // Category filtering
            val matchesCategory = if (criteria.category == "All") true else recipe.category.equals(criteria.category, ignoreCase = true)

            // Platform filtering
            val matchesPlatform = if (criteria.platform == "ALL") true else recipe.platform.equals(criteria.platform, ignoreCase = true)

            // Dietary filtering (Veg vs Non-Veg)
            val matchesDietary = when (criteria.dietary) {
                "VEG" -> recipe.isVegetarian
                "NON_VEG" -> !recipe.isVegetarian
                else -> true
            }

            // Search query
            val matchesQuery = if (criteria.query.isBlank()) true else {
                val q = criteria.query.trim().lowercase()
                recipe.title.lowercase().contains(q) ||
                        recipe.bio.lowercase().contains(q) ||
                        recipe.notes.lowercase().contains(q) ||
                        recipe.uploaderName.lowercase().contains(q) ||
                        recipe.ingredients.lowercase().contains(q) ||
                        recipe.category.lowercase().contains(q)
            }

            matchesTab && matchesCategory && matchesPlatform && matchesDietary && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: ForkfolioTab) {
        _currentTab.value = tab
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectPlatform(platform: String) {
        _selectedPlatform.value = platform
    }

    fun selectDietaryFilter(dietary: String) {
        _selectedDietary.value = dietary
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe.id, recipe.isFavorite)
            // Update active detail recipe state if open
            if (_selectedRecipeForDetail.value?.id == recipe.id) {
                _selectedRecipeForDetail.value = _selectedRecipeForDetail.value?.copy(isFavorite = !recipe.isFavorite)
            }
        }
    }

    fun openAdminLogin() {
        _adminLoginError.value = null
        _showAdminLoginDialog.value = true
    }

    fun closeAdminLogin() {
        _showAdminLoginDialog.value = false
        _adminLoginError.value = null
    }

    fun verifyAdminPassword(input: String): Boolean {
        if (input.trim() == _adminPassword.value.trim()) {
            _isAdminMode.value = true
            _showAdminLoginDialog.value = false
            _adminLoginError.value = null
            _toastMessage.value = "Admin mode unlocked! You can now add, edit, or remove recipes."
            return true
        } else {
            _adminLoginError.value = "Incorrect password. Please try again."
            return false
        }
    }

    fun lockAdmin() {
        _isAdminMode.value = false
        _toastMessage.value = "Admin mode locked."
    }

    fun openAdminSettings() {
        _showAdminSettingsDialog.value = true
    }

    fun closeAdminSettings() {
        _showAdminSettingsDialog.value = false
    }

    fun updateAdminPassword(oldPass: String, newPass: String): Boolean {
        if (oldPass.trim() == _adminPassword.value.trim()) {
            if (newPass.length < 3) {
                _toastMessage.value = "Password must be at least 3 characters"
                return false
            }
            _adminPassword.value = newPass.trim()
            sharedPrefs.edit().putString("admin_pin", newPass.trim()).apply()
            _toastMessage.value = "Admin password updated successfully!"
            _showAdminSettingsDialog.value = false
            return true
        } else {
            _toastMessage.value = "Current password does not match!"
            return false
        }
    }

    fun openRecipeDetail(recipe: RecipeEntity) {
        _selectedRecipeForDetail.value = recipe
    }

    fun closeRecipeDetail() {
        _selectedRecipeForDetail.value = null
    }

    fun openAddRecipe() {
        _selectedRecipeForEdit.value = null
        _showAddEditDialog.value = true
    }

    fun openEditRecipe(recipe: RecipeEntity) {
        _selectedRecipeForEdit.value = recipe
        _showAddEditDialog.value = true
    }

    fun closeAddEditDialog() {
        _showAddEditDialog.value = false
        _selectedRecipeForEdit.value = null
    }

    fun saveRecipe(
        id: Long,
        title: String,
        url: String,
        uploaderName: String,
        category: String,
        isVegetarian: Boolean,
        bio: String,
        notes: String,
        ingredients: String,
        cookTime: String,
        servings: String
    ) {
        viewModelScope.launch {
            val trimmedUrl = url.trim()
            val platform = UrlParser.detectPlatform(trimmedUrl)
            val videoId = if (platform == "YOUTUBE") UrlParser.extractYouTubeVideoId(trimmedUrl) else null

            val recipe = RecipeEntity(
                id = if (id > 0) id else 0,
                title = title.trim().ifEmpty { "Family Recipe" },
                url = trimmedUrl,
                platform = platform,
                videoId = videoId,
                isVegetarian = isVegetarian,
                bio = bio.trim(),
                uploaderName = uploaderName.trim().ifEmpty { "Mom" },
                category = category.trim().ifEmpty { "Mains" },
                notes = notes.trim(),
                ingredients = ingredients.trim(),
                cookTime = cookTime.trim().ifEmpty { "20 mins" },
                servings = servings.trim().ifEmpty { "4 servings" },
                isFavorite = _selectedRecipeForEdit.value?.isFavorite ?: false,
                createdAt = _selectedRecipeForEdit.value?.createdAt ?: System.currentTimeMillis()
            )

            if (id > 0) {
                repository.update(recipe)
                if (_selectedRecipeForDetail.value?.id == id) {
                    _selectedRecipeForDetail.value = recipe
                }
                _toastMessage.value = "Recipe updated successfully!"
            } else {
                repository.insert(recipe)
                _toastMessage.value = "New recipe added to Forkfolio!"
            }
            _showAddEditDialog.value = false
            _selectedRecipeForEdit.value = null
        }
    }

    fun deleteRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.delete(recipe)
            if (_selectedRecipeForDetail.value?.id == recipe.id) {
                _selectedRecipeForDetail.value = null
            }
            _toastMessage.value = "Recipe removed from Forkfolio"
        }
    }

    fun forceSyncCloud() {
        viewModelScope.launch {
            _toastMessage.value = "Syncing family recipes with cloud..."
            repository.forceSyncNow()
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        repository.getSyncManager().cleanup()
    }
}

