package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val platform: String, // "YOUTUBE", "INSTAGRAM", "OTHER"
    val videoId: String? = null,
    val isVegetarian: Boolean = true, // Veg vs Non-Veg
    val bio: String = "", // Optional recipe bio/story
    val customThumbnailUrl: String? = null,
    val uploaderName: String = "Mom",
    val category: String = "Mains",
    val notes: String = "",
    val ingredients: String = "",
    val cookTime: String = "30 mins",
    val servings: String = "4 servings",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val cloudId: String = ""
)

