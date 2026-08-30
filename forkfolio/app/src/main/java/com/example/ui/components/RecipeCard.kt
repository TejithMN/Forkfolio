package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.RecipeEntity
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.CreamSurfaceVariant
import com.example.ui.theme.EspressoDark
import com.example.ui.theme.EspressoLight
import com.example.ui.theme.EspressoMedium
import com.example.ui.theme.FavoriteRed
import com.example.ui.theme.FavoriteRedContainer
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
fun RecipeCard(
    recipe: RecipeEntity,
    isAdminMode: Boolean,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasVideoThumbnail = recipe.platform == "YOUTUBE" && !recipe.videoId.isNullOrEmpty()
    val thumbnailUrl = if (hasVideoThumbnail) {
        UrlParser.getYouTubeThumbnailUrl(recipe.videoId!!)
    } else {
        null
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onCardClick() }
            .testTag("recipe_card_${recipe.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurface),
        border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Media Preview Banner Section (Video intro frame / thumbnail)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(
                        if (recipe.platform == "INSTAGRAM") {
                            Brush.linearGradient(
                                listOf(InstagramGradientStart, InstagramGradientEnd)
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(Color(0xFF2C241E), Color(0xFF15100C))
                            )
                        }
                    )
            ) {
                if (hasVideoThumbnail && thumbnailUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Video Intro Banner for ${recipe.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Dark gradient overlay for readable badge
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.25f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )
                    // Play icon badge in center
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(46.dp)
                            .background(GoldPrimary.copy(alpha = 0.9f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Recipe Video",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                } else if (recipe.platform == "INSTAGRAM") {
                    // Instagram card visual
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.VideoLibrary,
                                    contentDescription = "Instagram Reel",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Instagram Reel",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Recipe",
                            tint = GoldLight,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Top Floating Badges: Platform + Veg/Non-Veg (Left) & Favorite Button (Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Platform Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (recipe.platform == "YOUTUBE") YouTubeRed else InstagramPurple
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (recipe.platform == "YOUTUBE") Icons.Default.PlayArrow else Icons.Default.VideoLibrary,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (recipe.platform == "YOUTUBE") "YouTube" else "Instagram",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Veg / Non-Veg Indicator Badge on Banner
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.95f),
                            border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VegIndicatorIcon(isVeg = recipe.isVegetarian, size = 13)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (recipe.isVegetarian) "VEG" else "NON-VEG",
                                    color = if (recipe.isVegetarian) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Top Right: Favorite Button
                    Surface(
                        shape = CircleShape,
                        color = CreamSurface.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.8f))
                    ) {
                        IconButton(
                            onClick = { onFavoriteToggle() },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("favorite_button_${recipe.id}")
                        ) {
                            Icon(
                                imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (recipe.isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (recipe.isFavorite) FavoriteRed else EspressoMedium,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Bottom Left: Category Pill inside Banner
                Surface(
                    shape = RoundedCornerShape(topEnd = 8.dp),
                    color = GoldContainer,
                    border = BorderStroke(0.5.dp, GoldPrimary),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = recipe.category,
                        color = OnGoldContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Recipe Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Title
                Text(
                    text = recipe.title,
                    color = EspressoDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp
                )

                // Recipe Bio / Story (if provided) or notes
                if (recipe.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recipe.bio,
                        color = GoldDark,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 16.sp
                    )
                } else if (recipe.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recipe.notes,
                        color = EspressoMedium,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Metadata Row: Uploader and Cook Time & Portions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Uploader info
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldContainer,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = GoldDark,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = recipe.uploaderName,
                            color = EspressoDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Cook Time
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = recipe.cookTime,
                                color = EspressoMedium,
                                fontSize = 11.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = recipe.servings,
                                color = EspressoMedium,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Admin Controls (Edit & Delete) if Admin mode is active
                if (isAdminMode) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GoldContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, GoldBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Admin Controls",
                                color = GoldDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp)
                            )

                            IconButton(
                                onClick = { onEditClick() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("admin_edit_recipe_${recipe.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Recipe",
                                    tint = GoldDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { onDeleteClick() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("admin_delete_recipe_${recipe.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Recipe",
                                    tint = FavoriteRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

