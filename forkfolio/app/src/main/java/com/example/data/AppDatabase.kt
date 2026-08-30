package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [RecipeEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "forkfolio_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialRecipes(database.recipeDao())
                    }
                }
            }
        }

        suspend fun populateInitialRecipes(dao: RecipeDao) {
            val initialRecipes = listOf(
                RecipeEntity(
                    title = "Mom's Royal Butter Chicken",
                    url = "https://www.youtube.com/watch?v=a03U45jFxOI",
                    platform = "YOUTUBE",
                    videoId = "a03U45jFxOI",
                    isVegetarian = false,
                    bio = "Passed down through three generations in our family. Mom perfected this aromatic gravy for Sunday family gatherings and holiday feasts.",
                    uploaderName = "Mom",
                    category = "Mains",
                    notes = "Family favorite! Mom's secret: marinate the chicken for at least 2 hours with smoked paprika and kasuri methi before searing in butter.",
                    ingredients = "Chicken breast, Greek yogurt, Butter, Heavy cream, Tomato puree, Garam masala, Kasuri methi, Ginger-garlic paste",
                    cookTime = "45 mins",
                    servings = "5 servings",
                    isFavorite = true,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3
                ),
                RecipeEntity(
                    title = "Viral Creamy Baked Feta Pasta",
                    url = "https://www.instagram.com/reel/C1234567890",
                    platform = "INSTAGRAM",
                    videoId = null,
                    isVegetarian = true,
                    bio = "Discovered on Reels and adapted by Cousin Aisha with toasted pine nuts and extra sweet cherry tomatoes.",
                    uploaderName = "Cousin Aisha",
                    category = "Quick Bites",
                    notes = "Super quick weeknight dinner! Cousin Aisha made this last Sunday and everyone loved it. Bake tomatoes and feta at 200°C until blistered.",
                    ingredients = "Block of feta cheese, Cherry tomatoes, Olive oil, Garlic cloves, Fresh basil, Rigatoni pasta, Red pepper flakes",
                    cookTime = "25 mins",
                    servings = "4 servings",
                    isFavorite = true,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2
                ),
                RecipeEntity(
                    title = "Fluffy Cloud Japanese Souffle Pancakes",
                    url = "https://www.youtube.com/watch?v=b4d8_u3gQf4",
                    platform = "YOUTUBE",
                    videoId = "b4d8_u3gQf4",
                    isVegetarian = true,
                    bio = "Aunt Priya's Sunday brunch specialty when the cousins sleep over. Incredibly airy and melt-in-the-mouth delicious.",
                    uploaderName = "Aunt Priya",
                    category = "Breakfast",
                    notes = "Whisk the egg whites until stiff peaks form. Cook on low flame with 2 drops of water and cover with lid for maximum fluffiness!",
                    ingredients = "Eggs separated, Milk, Vanilla extract, Cake flour, Sugar, Baking powder, Maple syrup, Fresh strawberries",
                    cookTime = "20 mins",
                    servings = "2 servings",
                    isFavorite = false,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 1
                ),
                RecipeEntity(
                    title = "Golden Saffron & Pistachio Kulfi",
                    url = "https://www.youtube.com/watch?v=kJQP7kiw5Fk",
                    platform = "YOUTUBE",
                    videoId = "kJQP7kiw5Fk",
                    isVegetarian = true,
                    bio = "Mom's signature dessert for Diwali and summer celebrations, rich with Kashmiri saffron and crushed Iranian pistachios.",
                    uploaderName = "Mom",
                    category = "Desserts",
                    notes = "Mom's festive dessert recipe. Simmer full-fat milk slowly until reduced by half with crushed green cardamom and saffron strands.",
                    ingredients = "Full fat milk, Condensed milk, Saffron threads, Cardamom powder, Chopped pistachios & almonds",
                    cookTime = "40 mins",
                    servings = "6 servings",
                    isFavorite = true,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 12
                ),
                RecipeEntity(
                    title = "Crispy Parmesan Garlic Smashed Potatoes",
                    url = "https://www.instagram.com/reel/C8901234567",
                    platform = "INSTAGRAM",
                    videoId = null,
                    isVegetarian = true,
                    bio = "Rohan's game-night snack that disappears in minutes! Ultra crispy exterior with fluffy potato goodness.",
                    uploaderName = "Cousin Rohan",
                    category = "Snacks",
                    notes = "Boil baby potatoes until fork tender, smash with a glass bottom, drizzle with garlic butter & parmesan, and bake until ultra crispy!",
                    ingredients = "Baby yellow potatoes, Olive oil, Melted butter, Minced garlic, Grated parmesan cheese, Fresh rosemary, Sea salt",
                    cookTime = "35 mins",
                    servings = "4 servings",
                    isFavorite = false,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 6
                ),
                RecipeEntity(
                    title = "Auntie's Special Cardamom Masala Chai",
                    url = "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
                    platform = "YOUTUBE",
                    videoId = "fJ9rUzIMcZQ",
                    isVegetarian = true,
                    bio = "The comforting morning ritual that brings everyone to the kitchen table every monsoon and winter morning.",
                    uploaderName = "Aunt Priya",
                    category = "Drinks",
                    notes = "Crush whole spices fresh! Boil with crushed ginger for 3 minutes before adding Assam black tea and whole milk.",
                    ingredients = "Assam black tea, Whole milk, Fresh ginger, Green cardamom, Cloves, Cinnamon stick, Raw cane sugar",
                    cookTime = "10 mins",
                    servings = "4 cups",
                    isFavorite = true,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 30
                )
            )
            dao.insertAll(initialRecipes)
        }
    }
}
