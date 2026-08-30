package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Long): Flow<RecipeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    suspend fun getAllRecipesList(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE cloudId = :cloudId LIMIT 1")
    suspend fun getRecipeByCloudId(cloudId: String): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE title = :title LIMIT 1")
    suspend fun getRecipeByTitle(title: String): RecipeEntity?

    @androidx.room.Transaction
    suspend fun syncCloudRecipes(cloudRecipes: List<RecipeEntity>) {
        for (cloudRecipe in cloudRecipes) {
            val existing = if (cloudRecipe.cloudId.isNotBlank()) {
                getRecipeByCloudId(cloudRecipe.cloudId) ?: getRecipeByTitle(cloudRecipe.title)
            } else {
                getRecipeByTitle(cloudRecipe.title)
            }

            if (existing != null) {
                // Update existing record, preserving user's local favorite preference if already set
                updateRecipe(
                    cloudRecipe.copy(
                        id = existing.id,
                        isFavorite = existing.isFavorite
                    )
                )
            } else {
                insertRecipe(cloudRecipe.copy(id = 0))
            }
        }
    }

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int
}
