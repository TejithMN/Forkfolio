package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val syncManager: FirebaseSyncManager
) {
    val allRecipes: Flow<List<RecipeEntity>> = recipeDao.getAllRecipes()
    val favoriteRecipes: Flow<List<RecipeEntity>> = recipeDao.getFavoriteRecipes()
    val syncState: StateFlow<CloudSyncState> = syncManager.syncState

    fun getRecipeById(id: Long): Flow<RecipeEntity?> = recipeDao.getRecipeById(id)

    suspend fun insert(recipe: RecipeEntity): Long {
        val localId = recipeDao.insertRecipe(recipe)
        val generatedCloudId = syncManager.uploadRecipeToCloud(recipe.copy(id = localId))
        if (generatedCloudId != null && recipe.cloudId.isBlank()) {
            recipeDao.updateRecipe(recipe.copy(id = localId, cloudId = generatedCloudId))
        }
        return localId
    }

    suspend fun update(recipe: RecipeEntity) {
        recipeDao.updateRecipe(recipe)
        syncManager.uploadRecipeToCloud(recipe)
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        recipeDao.updateFavorite(id, !currentFavorite)
    }

    suspend fun delete(recipe: RecipeEntity) {
        recipeDao.deleteRecipe(recipe)
        if (recipe.cloudId.isNotBlank()) {
            syncManager.deleteRecipeFromCloud(recipe.cloudId)
        }
    }

    suspend fun deleteById(id: Long) {
        val recipe = recipeDao.getRecipeById(id)
        recipeDao.deleteById(id)
    }

    suspend fun forceSyncNow() {
        syncManager.seedLocalRecipesToCloud()
        syncManager.startRealtimeListener()
    }

    suspend fun ensureInitialData() {
        if (recipeDao.getRecipeCount() == 0) {
            AppDatabase.populateInitialRecipes(recipeDao)
            syncManager.seedLocalRecipesToCloud()
        }
    }

    fun getSyncManager(): FirebaseSyncManager = syncManager
}
