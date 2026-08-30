package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.RecipeEntity
import com.example.ui.components.YouTubePlayerView
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.CreamSurfaceVariant
import com.example.ui.theme.EspressoDark
import com.example.ui.theme.EspressoLight
import com.example.ui.theme.EspressoMedium
import com.example.ui.theme.FavoriteRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InstagramGradientEnd
import com.example.ui.theme.InstagramGradientStart
import com.example.ui.theme.InstagramPurple
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.YouTubeRed
import com.example.util.UrlParser

@Composable
fun RecipeDetailDialog(
    recipe: RecipeEntity,
    isAdminMode: Boolean,
    onDismiss: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val checkedIngredients = remember { mutableStateListOf<String>() }

    // Parse ingredients into list
    val ingredientList = remember(recipe.ingredients) {
        if (recipe.ingredients.isBlank()) emptyList()
        else {
            recipe.ingredients.split("\n", ",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamBackground),
            color = CreamBackground
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar for Recipe Detail
                Surface(
                    color = CreamSurface,
                    border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("back_from_detail_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Recipe",
                                    tint = EspressoDark
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Family Recipe",
                                color = GoldDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Favorite Button
                            IconButton(
                                onClick = onFavoriteToggle,
                                modifier = Modifier.testTag("detail_favorite_button")
                            ) {
                                Icon(
                                    imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (recipe.isFavorite) "Remove from favorites" else "Add to favorites",
                                    tint = if (recipe.isFavorite) FavoriteRed else EspressoMedium,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Share / Copy Link Button
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Check out ${recipe.title} on Forkfolio: ${recipe.url}")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Recipe"))
                                },
                                modifier = Modifier.testTag("share_recipe_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Recipe",
                                    tint = EspressoDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Main Scrollable Recipe Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    // In-App Video Player / Media Box
                    if (recipe.platform == "YOUTUBE" && !recipe.videoId.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            YouTubePlayerView(
                                videoId = recipe.videoId,
                                fallbackUrl = recipe.url
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = YouTubeRed.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, YouTubeRed.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            com.example.ui.components.launchYouTubeIntent(
                                                context,
                                                recipe.videoId ?: "",
                                                recipe.url
                                            )
                                        }
                                        .testTag("open_in_youtube_app_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInNew,
                                            contentDescription = null,
                                            tint = YouTubeRed,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Watch in YouTube App",
                                            color = YouTubeRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    } else if (recipe.platform == "INSTAGRAM") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CreamSurface),
                            border = BorderStroke(1.dp, GoldBorder)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(InstagramGradientStart, InstagramGradientEnd)
                                        )
                                    )
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.25f),
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.VideoLibrary,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Instagram Culinary Reel",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Uploaded by ${recipe.uploaderName}",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.url))
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = InstagramPurple
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("open_instagram_link_button")
                                    ) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Open on Instagram", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Recipe Metadata & Title Section
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        // Platform, Category, and Veg/Non-Veg Badges Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (recipe.platform == "YOUTUBE") YouTubeRed else InstagramPurple
                            ) {
                                Text(
                                    text = if (recipe.platform == "YOUTUBE") "YouTube Video" else "Instagram Reel",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Veg / Non-Veg Badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (recipe.isVegetarian) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                border = BorderStroke(1.dp, if (recipe.isVegetarian) Color(0xFF2E7D32) else Color(0xFFC62828))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    com.example.ui.components.VegIndicatorIcon(isVeg = recipe.isVegetarian, size = 12)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (recipe.isVegetarian) "Vegetarian (Veg)" else "Non-Vegetarian",
                                        color = if (recipe.isVegetarian) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldContainer,
                                border = BorderStroke(0.5.dp, GoldPrimary)
                            ) {
                                Text(
                                    text = recipe.category,
                                    color = OnGoldContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Title
                        Text(
                            text = recipe.title,
                            color = EspressoDark,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp
                        )

                        // Recipe Bio / Story Card (If present)
                        if (recipe.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldContainer.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = GoldDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = recipe.bio,
                                        color = EspressoDark,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Info Cards Row (Uploader, Time, Servings)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Uploader Card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                                border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = GoldDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Shared By",
                                        color = EspressoLight,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = recipe.uploaderName,
                                        color = EspressoDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Cook Time Card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                                border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = GoldDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Cook Time",
                                        color = EspressoLight,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = recipe.cookTime,
                                        color = EspressoDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Servings Card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                                border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = GoldDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Portions",
                                        color = EspressoLight,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = recipe.servings,
                                        color = EspressoDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Mom's Secret Family Notes / Tips Card
                        if (recipe.notes.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = GoldContainer.copy(alpha = 0.6f)),
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FormatQuote,
                                            contentDescription = null,
                                            tint = GoldDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Mom's Cooking Secrets & Tips",
                                            color = GoldDark,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = recipe.notes,
                                        color = EspressoDark,
                                        fontSize = 14.sp,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }

                        // Key Ingredients Section with Interactive Checklist
                        if (ingredientList.isNotEmpty()) {
                            Text(
                                text = "Ingredients Checklist",
                                color = EspressoDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap items to cross them off while prepping:",
                                color = EspressoLight,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                                border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ingredientList.forEach { ingredient ->
                                        val isChecked = checkedIngredients.contains(ingredient)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    if (isChecked) checkedIngredients.remove(ingredient)
                                                    else checkedIngredients.add(ingredient)
                                                }
                                                .padding(vertical = 6.dp, horizontal = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = if (isChecked) GoldPrimary else GoldBorder,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = ingredient,
                                                color = if (isChecked) EspressoLight else EspressoDark,
                                                fontSize = 14.sp,
                                                fontWeight = if (isChecked) FontWeight.Normal else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Original Recipe Link Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CreamSurfaceVariant.copy(alpha = 0.5f)),
                            border = BorderStroke(0.5.dp, GoldBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Original Link",
                                        color = EspressoLight,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = recipe.url,
                                        color = GoldDark,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.url))
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, GoldPrimary),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldDark),
                                    modifier = Modifier.testTag("open_external_browser_button")
                                ) {
                                    Text("Open Link", fontSize = 12.sp)
                                }
                            }
                        }

                        // Admin Action Section (If in Admin Mode)
                        if (isAdminMode) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = GoldContainer.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, GoldBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Admin Actions (Mom Mode)",
                                        color = GoldDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = onEditClick,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, GoldDark),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldDark),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("detail_admin_edit_button")
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Edit Recipe")
                                        }

                                        Button(
                                            onClick = onDeleteClick,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = FavoriteRed,
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("detail_admin_delete_button")
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Delete")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
