package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.RecipeEntity
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
import com.example.util.UrlParser

@Composable
fun AddEditRecipeDialog(
    recipeToEdit: RecipeEntity?,
    onDismiss: () -> Unit,
    onSaveRecipe: (
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
    ) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isEdit = recipeToEdit != null

    var title by remember { mutableStateOf(recipeToEdit?.title ?: "") }
    var url by remember { mutableStateOf(recipeToEdit?.url ?: "") }
    var isVegetarian by remember { mutableStateOf(recipeToEdit?.isVegetarian ?: true) }
    var bio by remember { mutableStateOf(recipeToEdit?.bio ?: "") }
    var uploaderName by remember { mutableStateOf(recipeToEdit?.uploaderName ?: "Mom") }
    var selectedCategory by remember { mutableStateOf(recipeToEdit?.category ?: "Mains") }
    var notes by remember { mutableStateOf(recipeToEdit?.notes ?: "") }
    var ingredients by remember { mutableStateOf(recipeToEdit?.ingredients ?: "") }
    var cookTime by remember { mutableStateOf(recipeToEdit?.cookTime ?: "25 mins") }
    var servings by remember { mutableStateOf(recipeToEdit?.servings ?: "4 servings") }

    var titleError by remember { mutableStateOf(false) }
    var urlError by remember { mutableStateOf(false) }

    val categories = listOf("Mains", "Breakfast", "Desserts", "Snacks", "Drinks", "Quick Bites")
    val defaultUploaders = listOf("Mom", "Cousin Aisha", "Cousin Rohan", "Aunt Priya", "Uncle Dev")

    // Dynamic platform detection based on entered URL
    val detectedPlatform = UrlParser.detectPlatform(url)
    val extractedVideoId = if (detectedPlatform == "YOUTUBE") UrlParser.extractYouTubeVideoId(url) else null
    val isShort = UrlParser.isYouTubeShort(url)
    val isReel = UrlParser.isInstagramReel(url)

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
                .fillMaxWidth(0.94f)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = CreamSurface,
            border = BorderStroke(1.5.dp, GoldBorder),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GoldContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.RestaurantMenu,
                                    contentDescription = null,
                                    tint = GoldDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isEdit) "Edit Recipe" else "Add Recipe",
                            color = EspressoDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_add_recipe_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = EspressoMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Recipe Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = false
                        },
                        label = { Text("Recipe Title *") },
                        placeholder = { Text("e.g. Mom's Golden Saffron Biryani") },
                        isError = titleError,
                        supportingText = if (titleError) { { Text("Recipe title is required", color = FavoriteRed) } } else null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.3f),
                            focusedIndicatorColor = GoldPrimary,
                            unfocusedIndicatorColor = GoldBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recipe_title_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Recipe Link (Video / Short / Reel) with 1-Tap Paste Button
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Video / Short / Reel Link *",
                                color = EspressoDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Quick Paste from Clipboard button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldContainer,
                                border = BorderStroke(1.dp, GoldBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        clipboardManager.getText()?.text?.let { clipboardText ->
                                            if (clipboardText.isNotBlank()) {
                                                url = clipboardText.trim()
                                                urlError = false
                                            }
                                        }
                                    }
                                    .testTag("paste_link_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Paste from clipboard",
                                        tint = GoldDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Paste Link",
                                        color = OnGoldContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = url,
                            onValueChange = {
                                url = it
                                urlError = false
                            },
                            placeholder = { Text("Paste YouTube video, Short, or Instagram Reel link...") },
                            isError = urlError,
                            supportingText = if (urlError) { { Text("Valid recipe URL is required", color = FavoriteRed) } } else null,
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Link, contentDescription = null, tint = GoldDark)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Next
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.5f),
                                unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.3f),
                                focusedIndicatorColor = GoldPrimary,
                                unfocusedIndicatorColor = GoldBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recipe_url_input")
                        )
                    }

                    // Platform & Thumbnail Banner Preview Feedback
                    if (url.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (detectedPlatform) {
                                "YOUTUBE" -> YouTubeRed.copy(alpha = 0.1f)
                                "INSTAGRAM" -> InstagramPurple.copy(alpha = 0.1f)
                                else -> GoldContainer.copy(alpha = 0.5f)
                            },
                            border = BorderStroke(
                                1.dp,
                                when (detectedPlatform) {
                                    "YOUTUBE" -> YouTubeRed.copy(alpha = 0.4f)
                                    "INSTAGRAM" -> InstagramPurple.copy(alpha = 0.4f)
                                    else -> GoldBorder
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (detectedPlatform) {
                                            "YOUTUBE" -> Icons.Default.PlayArrow
                                            "INSTAGRAM" -> Icons.Default.VideoLibrary
                                            else -> Icons.Default.Link
                                        },
                                        contentDescription = null,
                                        tint = when (detectedPlatform) {
                                            "YOUTUBE" -> YouTubeRed
                                            "INSTAGRAM" -> InstagramPurple
                                            else -> GoldDark
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when {
                                            detectedPlatform == "YOUTUBE" && isShort -> "✓ YouTube Short Detected"
                                            detectedPlatform == "YOUTUBE" && extractedVideoId != null -> "✓ YouTube Video Detected (In-App Player Ready)"
                                            detectedPlatform == "INSTAGRAM" && isReel -> "✓ Instagram Reel Detected"
                                            detectedPlatform == "INSTAGRAM" -> "✓ Instagram Culinary Post Detected"
                                            else -> "Web Recipe Link"
                                        },
                                        color = when (detectedPlatform) {
                                            "YOUTUBE" -> YouTubeRed
                                            "INSTAGRAM" -> InstagramPurple
                                            else -> EspressoDark
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // If YouTube videoId exists, display the actual Video Thumbnail Banner!
                                if (extractedVideoId != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16f / 9f)
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(UrlParser.getYouTubeThumbnailUrl(extractedVideoId))
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Video Intro Banner",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            // Play badge
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.6f),
                                                modifier = Modifier
                                                    .align(Alignment.Center)
                                                    .size(36.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dietary Selection: Veg or Non-Veg
                    Text(
                        text = "Dietary Preference (Veg / Non-Veg) *",
                        color = EspressoDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Vegetarian Option
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isVegetarian) Color(0xFFE8F5E9) else CreamSurfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                if (isVegetarian) 1.5.dp else 1.dp,
                                if (isVegetarian) Color(0xFF2E7D32) else GoldBorderSubtle
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { isVegetarian = true }
                                .testTag("dietary_veg_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VegIndicatorIcon(isVeg = true, size = 18)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Vegetarian",
                                        color = if (isVegetarian) Color(0xFF1B5E20) else EspressoDark,
                                        fontSize = 13.sp,
                                        fontWeight = if (isVegetarian) FontWeight.Bold else FontWeight.Medium
                                    )
                                    Text(
                                        text = "Pure Veg",
                                        color = if (isVegetarian) Color(0xFF2E7D32) else EspressoLight,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Non-Vegetarian Option
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (!isVegetarian) Color(0xFFFFEBEE) else CreamSurfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                if (!isVegetarian) 1.5.dp else 1.dp,
                                if (!isVegetarian) Color(0xFFC62828) else GoldBorderSubtle
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { isVegetarian = false }
                                .testTag("dietary_nonveg_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VegIndicatorIcon(isVeg = false, size = 18)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Non-Vegetarian",
                                        color = if (!isVegetarian) Color(0xFFB71C1C) else EspressoDark,
                                        fontSize = 13.sp,
                                        fontWeight = if (!isVegetarian) FontWeight.Bold else FontWeight.Medium
                                    )
                                    Text(
                                        text = "Meat / Poultry / Fish",
                                        color = if (!isVegetarian) Color(0xFFC62828) else EspressoLight,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Recipe Bio / Story (Optional)
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Recipe Bio / Story (Optional)") },
                        placeholder = { Text("Share the story, memory, or who inspired this recipe (optional)...") },
                        minLines = 2,
                        maxLines = 3,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.2f),
                            focusedIndicatorColor = GoldPrimary,
                            unfocusedIndicatorColor = GoldBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recipe_bio_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Selection Chips
                    Text(
                        text = "Category",
                        color = EspressoDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GoldPrimary else CreamSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) GoldDark else GoldBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color.White else EspressoDark,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Uploader Name Selection / Input
                    Text(
                        text = "Uploaded By",
                        color = EspressoDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        defaultUploaders.forEach { name ->
                            val isSelected = uploaderName.equals(name, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GoldContainer else CreamSurfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, if (isSelected) GoldPrimary else GoldBorderSubtle),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { uploaderName = name }
                            ) {
                                Text(
                                    text = name,
                                    color = if (isSelected) OnGoldContainer else EspressoMedium,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uploaderName,
                        onValueChange = { uploaderName = it },
                        placeholder = { Text("Custom uploader name...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.2f),
                            focusedIndicatorColor = GoldPrimary,
                            unfocusedIndicatorColor = GoldBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cook Time and Servings Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = cookTime,
                            onValueChange = { cookTime = it },
                            label = { Text("Cook Time") },
                            placeholder = { Text("e.g. 25 mins") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.4f),
                                unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.2f),
                                focusedIndicatorColor = GoldPrimary,
                                unfocusedIndicatorColor = GoldBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = servings,
                            onValueChange = { servings = it },
                            label = { Text("Servings") },
                            placeholder = { Text("e.g. 4 people") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.4f),
                                unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.2f),
                                focusedIndicatorColor = GoldPrimary,
                                unfocusedIndicatorColor = GoldBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Key Ingredients
                    OutlinedTextField(
                        value = ingredients,
                        onValueChange = { ingredients = it },
                        label = { Text("Key Ingredients (comma or line separated)") },
                        placeholder = { Text("e.g. Basmati rice, Saffron, Ghee, Cashews, Cardamom") },
                        minLines = 2,
                        maxLines = 4,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.2f),
                            focusedIndicatorColor = GoldPrimary,
                            unfocusedIndicatorColor = GoldBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mom's Special Notes & Family Tweaks
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Mom's Notes & Secret Family Tweaks") },
                        placeholder = { Text("e.g. Fry onions until deep golden brown for authentic aroma...") },
                        minLines = 2,
                        maxLines = 4,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.2f),
                            focusedIndicatorColor = GoldPrimary,
                            unfocusedIndicatorColor = GoldBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = EspressoMedium)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            var valid = true
                            if (title.isBlank()) {
                                titleError = true
                                valid = false
                            }
                            if (url.isBlank()) {
                                urlError = true
                                valid = false
                            }
                            if (valid) {
                                onSaveRecipe(
                                    recipeToEdit?.id ?: 0L,
                                    title,
                                    url,
                                    uploaderName,
                                    selectedCategory,
                                    isVegetarian,
                                    bio,
                                    notes,
                                    ingredients,
                                    cookTime,
                                    servings
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_recipe_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEdit) "Update Recipe" else "Add to Forkfolio",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VegIndicatorIcon(
    isVeg: Boolean,
    size: Int = 16,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isVeg) Color(0xFF2E7D32) else Color(0xFFC62828)
    val dotColor = if (isVeg) Color(0xFF2E7D32) else Color(0xFFC62828)

    Surface(
        shape = RoundedCornerShape(3.dp),
        color = Color.Transparent,
        border = BorderStroke(1.5.dp, borderColor),
        modifier = modifier.size(size.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isVeg) {
                // Green Circle Dot
                Surface(
                    shape = CircleShape,
                    color = dotColor,
                    modifier = Modifier.size((size * 0.5f).dp)
                ) {}
            } else {
                // Brown/Red Triangle / Polygon dot
                Surface(
                    shape = RoundedCornerShape(1.dp),
                    color = dotColor,
                    modifier = Modifier.size((size * 0.45f).dp)
                ) {}
            }
        }
    }
}

