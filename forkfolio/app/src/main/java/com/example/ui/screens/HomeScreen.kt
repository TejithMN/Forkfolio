package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.CloudSyncStatus
import com.example.data.RecipeEntity
import com.example.ui.ForkfolioTab
import com.example.ui.ForkfolioViewModel
import com.example.ui.components.AddEditRecipeDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.AdminSettingsDialog
import com.example.ui.components.FamilySyncDialog
import com.example.ui.components.RecipeCard
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.CreamSurfaceVariant
import com.example.ui.theme.EspressoDark
import com.example.ui.theme.EspressoLight
import com.example.ui.theme.EspressoMedium
import com.example.ui.theme.FavoriteRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderSubtle
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InstagramPurple
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.YouTubeRed

@Composable
fun HomeScreen(
    viewModel: ForkfolioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val recipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()
    val allRecipes by viewModel.allRecipesRaw.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedPlatform by viewModel.selectedPlatform.collectAsStateWithLifecycle()
    val selectedDietaryFilter by viewModel.selectedDietaryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
    val selectedDetailRecipe by viewModel.selectedRecipeForDetail.collectAsStateWithLifecycle()
    val selectedEditRecipe by viewModel.selectedRecipeForEdit.collectAsStateWithLifecycle()
    val showAddEditDialog by viewModel.showAddEditDialog.collectAsStateWithLifecycle()
    val showAdminLoginDialog by viewModel.showAdminLoginDialog.collectAsStateWithLifecycle()
    val showAdminSettingsDialog by viewModel.showAdminSettingsDialog.collectAsStateWithLifecycle()
    val adminLoginError by viewModel.adminLoginError.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()

    var recipeToDelete by remember { mutableStateOf<RecipeEntity?>(null) }
    var showFamilySyncDialog by remember { mutableStateOf(false) }
    var logoTapCount by remember { mutableStateOf(0) }
    var lastLogoTapTime by remember { mutableStateOf(0L) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    val categories = listOf("All", "Mains", "Breakfast", "Desserts", "Snacks", "Drinks", "Quick Bites")
    val favoriteCount = allRecipes.count { it.isFavorite }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground),
        containerColor = CreamBackground,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            NavigationBar(
                containerColor = CreamSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == ForkfolioTab.ALL_RECIPES,
                    onClick = { viewModel.selectTab(ForkfolioTab.ALL_RECIPES) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = "All Recipes"
                        )
                    },
                    label = { Text("All Recipes", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnGoldContainer,
                        selectedTextColor = GoldDark,
                        indicatorColor = GoldContainer,
                        unselectedIconColor = EspressoMedium,
                        unselectedTextColor = EspressoLight
                    ),
                    modifier = Modifier.testTag("nav_all_recipes_tab")
                )

                NavigationBarItem(
                    selected = currentTab == ForkfolioTab.FAVORITES,
                    onClick = { viewModel.selectTab(ForkfolioTab.FAVORITES) },
                    icon = {
                        Box {
                            Icon(
                                imageVector = if (currentTab == ForkfolioTab.FAVORITES) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorites",
                                tint = if (currentTab == ForkfolioTab.FAVORITES) FavoriteRed else EspressoMedium
                            )
                            if (favoriteCount > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = FavoriteRed,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(14.dp)
                                ) {
                                    Text(
                                        text = "$favoriteCount",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    },
                    label = { Text("Favorites", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FavoriteRed,
                        selectedTextColor = GoldDark,
                        indicatorColor = GoldContainer,
                        unselectedIconColor = EspressoMedium,
                        unselectedTextColor = EspressoLight
                    ),
                    modifier = Modifier.testTag("nav_favorites_tab")
                )

                NavigationBarItem(
                    selected = currentTab == ForkfolioTab.FAMILY_UPLOADS,
                    onClick = { viewModel.selectTab(ForkfolioTab.FAMILY_UPLOADS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Family Uploads"
                        )
                    },
                    label = { Text("Family Wall", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnGoldContainer,
                        selectedTextColor = GoldDark,
                        indicatorColor = GoldContainer,
                        unselectedIconColor = EspressoMedium,
                        unselectedTextColor = EspressoLight
                    ),
                    modifier = Modifier.testTag("nav_family_tab")
                )
            }
        },
        floatingActionButton = {
            // Upload button is strictly visible only when Admin mode is unlocked!
            if (isAdminMode) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.openAddRecipe() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Recipe",
                            tint = Color.White
                        )
                    },
                    text = {
                        Text(
                            text = "Add Recipe",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = GoldPrimary,
                    elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(6.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("add_recipe_fab")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Top Bar
            Surface(
                color = CreamSurface,
                border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand Title & Logo (Secret Admin Access Trigger: Long Press or 3 Rapid Taps)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .pointerInput(isAdminMode) {
                                    detectTapGestures(
                                        onLongPress = {
                                            if (!isAdminMode) {
                                                viewModel.openAdminLogin()
                                            }
                                        },
                                        onTap = {
                                            if (!isAdminMode) {
                                                val now = System.currentTimeMillis()
                                                if (now - lastLogoTapTime < 500) {
                                                    logoTapCount++
                                                    if (logoTapCount >= 3) {
                                                        logoTapCount = 0
                                                        viewModel.openAdminLogin()
                                                    }
                                                } else {
                                                    logoTapCount = 1
                                                }
                                                lastLogoTapTime = now
                                            }
                                        }
                                    )
                                }
                                .testTag("brand_logo_header")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldContainer,
                                border = BorderStroke(1.dp, GoldBorder),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Restaurant,
                                        contentDescription = "Forkfolio Emblem",
                                        tint = GoldDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Forkfolio",
                                    color = EspressoDark,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Mom's Family Recipe Book",
                                    color = GoldDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Top Header Actions: Cloud Sync & Admin Panel Access (Visible only when unlocked)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Cloud Sync Status Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when (syncState.status) {
                                    CloudSyncStatus.SYNCED -> Color(0xFFE8F5E9)
                                    CloudSyncStatus.SYNCING -> GoldContainer
                                    else -> CreamSurfaceVariant.copy(alpha = 0.6f)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    when (syncState.status) {
                                        CloudSyncStatus.SYNCED -> Color(0xFF81C784)
                                        CloudSyncStatus.SYNCING -> GoldPrimary
                                        else -> GoldBorder.copy(alpha = 0.5f)
                                    }
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showFamilySyncDialog = true }
                                    .testTag("cloud_sync_status_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (syncState.status == CloudSyncStatus.SYNCED) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                        contentDescription = "Cloud Sync Status",
                                        tint = when (syncState.status) {
                                            CloudSyncStatus.SYNCED -> Color(0xFF2E7D32)
                                            CloudSyncStatus.SYNCING -> GoldDark
                                            else -> EspressoMedium
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when (syncState.status) {
                                            CloudSyncStatus.SYNCED -> "Live Synced"
                                            CloudSyncStatus.SYNCING -> "Syncing..."
                                            else -> "Cloud Sync"
                                        },
                                        color = when (syncState.status) {
                                            CloudSyncStatus.SYNCED -> Color(0xFF2E7D32)
                                            CloudSyncStatus.SYNCING -> GoldDark
                                            else -> EspressoMedium
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Admin Settings button only appears when Admin Mode is active
                            if (isAdminMode) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldContainer,
                                    border = BorderStroke(1.dp, GoldPrimary),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.openAdminSettings() }
                                        .testTag("admin_panel_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LockOpen,
                                            contentDescription = "Admin Mode Settings",
                                            tint = GoldDark,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Admin Active",
                                            color = OnGoldContainer,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Admin Banner if Active
                    if (isAdminMode) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldContainer,
                            border = BorderStroke(1.dp, GoldPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = null,
                                        tint = GoldDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Admin Active • Full Add / Edit / Delete Access",
                                        color = OnGoldContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Lock",
                                        color = FavoriteRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { viewModel.lockAdmin() }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Search Bar & Filters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search recipes, bio, ingredients, Mom, cousins...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GoldDark
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = EspressoMedium
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CreamSurface,
                        unfocusedContainerColor = CreamSurface,
                        focusedIndicatorColor = GoldPrimary,
                        unfocusedIndicatorColor = GoldBorder
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recipe_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Platform & Veg/Non-Veg Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val platforms = listOf(
                        Triple("ALL", "All", Icons.Default.Restaurant),
                        Triple("YOUTUBE", "YouTube", Icons.Default.PlayArrow),
                        Triple("INSTAGRAM", "Instagram", Icons.Default.VideoLibrary)
                    )

                    platforms.forEach { (pKey, pLabel, pIcon) ->
                        val isSelected = selectedPlatform == pKey
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) {
                                when (pKey) {
                                    "YOUTUBE" -> YouTubeRed
                                    "INSTAGRAM" -> InstagramPurple
                                    else -> GoldPrimary
                                }
                            } else CreamSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color.Transparent else GoldBorder.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.selectPlatform(pKey) }
                                .testTag("platform_chip_$pKey")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = pIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else GoldDark,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = pLabel,
                                    color = if (isSelected) Color.White else EspressoDark,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Veg / Non-Veg Quick Dietary Toggle Chips
                    val dietaryOptions = listOf(
                        Triple("ALL", "All", Color(0xFF6D4C41)),
                        Triple("VEG", "Veg", Color(0xFF2E7D32)),
                        Triple("NON_VEG", "Non-Veg", Color(0xFFC62828))
                    )

                    dietaryOptions.forEach { (dKey, dLabel, dColor) ->
                        val isDietSelected = selectedDietaryFilter == dKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDietSelected) dColor else CreamSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isDietSelected) dColor else GoldBorder.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.selectDietaryFilter(dKey) }
                                .testTag("dietary_chip_$dKey")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (dKey != "ALL") {
                                    com.example.ui.components.VegIndicatorIcon(
                                        isVeg = dKey == "VEG",
                                        size = 11
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                }
                                Text(
                                    text = dLabel,
                                    color = if (isDietSelected) Color.White else EspressoDark,
                                    fontSize = 10.sp,
                                    fontWeight = if (isDietSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) GoldDark else CreamSurfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) GoldPrimary else GoldBorderSubtle
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.selectCategory(cat) }
                                .testTag("category_chip_$cat")
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else EspressoMedium,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Recipe List / Grid Section
            if (recipes.isEmpty()) {
                // Friendly Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldContainer,
                            border = BorderStroke(1.dp, GoldBorder),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (currentTab == ForkfolioTab.FAVORITES) Icons.Default.FavoriteBorder else Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = GoldDark,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (currentTab == ForkfolioTab.FAVORITES) "No Favorites Yet" else "No Recipes Found",
                            color = EspressoDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (currentTab == ForkfolioTab.FAVORITES) {
                                "Tap the heart icon on any recipe to save it to your family favorites list!"
                            } else {
                                "Try searching for another dish, ingredient, or category."
                            },
                            color = EspressoMedium,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            } else {
                // Grid of Recipe Cards
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 300.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("recipe_grid")
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            isAdminMode = isAdminMode,
                            onCardClick = { viewModel.openRecipeDetail(recipe) },
                            onFavoriteToggle = { viewModel.toggleFavorite(recipe) },
                            onEditClick = { viewModel.openEditRecipe(recipe) },
                            onDeleteClick = { recipeToDelete = recipe }
                        )
                    }
                }
            }
        }
    }

    // Recipe Detail Dialog
    selectedDetailRecipe?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            isAdminMode = isAdminMode,
            onDismiss = { viewModel.closeRecipeDetail() },
            onFavoriteToggle = { viewModel.toggleFavorite(recipe) },
            onEditClick = {
                viewModel.closeRecipeDetail()
                viewModel.openEditRecipe(recipe)
            },
            onDeleteClick = {
                recipeToDelete = recipe
            }
        )
    }

    // Add / Edit Recipe Dialog
    if (showAddEditDialog) {
        AddEditRecipeDialog(
            recipeToEdit = selectedEditRecipe,
            onDismiss = { viewModel.closeAddEditDialog() },
            onSaveRecipe = { id, title, url, uploaderName, category, notes, ingredients, cookTime, servings, isVegetarian, bio ->
                viewModel.saveRecipe(
                    id, title, url, uploaderName, category, notes, ingredients, cookTime, servings, isVegetarian, bio
                )
            }
        )
    }

    // Admin Login Password Dialog
    if (showAdminLoginDialog) {
        AdminLoginDialog(
            errorMessage = adminLoginError,
            onDismiss = { viewModel.closeAdminLogin() },
            onSubmitPassword = { input ->
                viewModel.verifyAdminPassword(input)
            }
        )
    }

    // Admin Settings Dialog (Change password)
    if (showAdminSettingsDialog) {
        AdminSettingsDialog(
            onDismiss = { viewModel.closeAdminSettings() },
            onUpdatePassword = { oldPass, newPass ->
                viewModel.updateAdminPassword(oldPass, newPass)
            }
        )
    }

    // Delete Confirmation Dialog
    recipeToDelete?.let { targetRecipe ->
        AlertDialog(
            onDismissRequest = { recipeToDelete = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = CreamSurface,
            title = {
                Text(
                    text = "Delete Recipe?",
                    color = EspressoDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove '${targetRecipe.title}' from Forkfolio?",
                    color = EspressoMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRecipe(targetRecipe)
                        recipeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FavoriteRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_delete_recipe_button")
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { recipeToDelete = null }) {
                    Text("Cancel", color = EspressoMedium)
                }
            }
        )
    }

    // Family Cloud Sync Details Dialog
    if (showFamilySyncDialog) {
        FamilySyncDialog(
            syncState = syncState,
            onDismiss = { showFamilySyncDialog = false },
            onForceSync = { viewModel.forceSyncCloud() }
        )
    }
}
