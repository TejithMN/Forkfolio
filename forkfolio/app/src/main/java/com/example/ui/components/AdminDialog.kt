package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import com.example.data.CloudSyncState
import com.example.data.CloudSyncStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.OnGoldContainer

@Composable
fun AdminLoginDialog(
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmitPassword: (String) -> Boolean
) {
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CreamSurface,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = GoldContainer,
                    border = BorderStroke(1.dp, GoldBorder),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Admin Security",
                            tint = GoldDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Admin Access",
                    color = EspressoDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Enter password to add or edit family recipes",
                    color = EspressoMedium,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter admin password") },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (passwordInput.isNotBlank()) {
                                onSubmitPassword(passwordInput)
                            }
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                tint = GoldDark
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.3f),
                        focusedIndicatorColor = GoldPrimary,
                        unfocusedIndicatorColor = GoldBorder,
                        focusedLabelColor = GoldDark,
                        cursorColor = GoldPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        color = FavoriteRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (passwordInput.isNotBlank()) {
                        onSubmitPassword(passwordInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("submit_admin_password_button")
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Unlock", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = EspressoMedium)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AdminSettingsDialog(
    onDismiss: () -> Unit,
    onUpdatePassword: (String, String) -> Boolean
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CreamSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = GoldContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Change Admin Password",
                    color = EspressoDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = oldPass,
                    onValueChange = {
                        oldPass = it
                        localError = null
                    },
                    label = { Text("Current Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.2f),
                        focusedIndicatorColor = GoldPrimary,
                        unfocusedIndicatorColor = GoldBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newPass,
                    onValueChange = {
                        newPass = it
                        localError = null
                    },
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.2f),
                        focusedIndicatorColor = GoldPrimary,
                        unfocusedIndicatorColor = GoldBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = {
                        confirmPass = it
                        localError = null
                    },
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = CreamSurfaceVariant.copy(alpha = 0.2f),
                        focusedIndicatorColor = GoldPrimary,
                        unfocusedIndicatorColor = GoldBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (localError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = localError ?: "",
                        color = FavoriteRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPass != confirmPass) {
                        localError = "New passwords do not match!"
                    } else if (newPass.length < 3) {
                        localError = "Password must be at least 3 characters"
                    } else {
                        val success = onUpdatePassword(oldPass, newPass)
                        if (!success) {
                            localError = "Current password was incorrect"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Update Password", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = EspressoMedium)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FamilySyncDialog(
    syncState: CloudSyncState,
    onDismiss: () -> Unit,
    onForceSync: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault()) }
    val lastSyncText = if (syncState.lastSyncedTimestamp > 0) {
        dateFormat.format(Date(syncState.lastSyncedTimestamp))
    } else {
        "Just now"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CreamSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = GoldContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Family Cloud Sync",
                        color = EspressoDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Live sync across all cousins' devices",
                        color = EspressoMedium,
                        fontSize = 12.sp
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CreamSurfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status:",
                                color = EspressoDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = when (syncState.status) {
                                    CloudSyncStatus.SYNCED -> Color(0xFF2E7D32)
                                    CloudSyncStatus.SYNCING -> GoldPrimary
                                    CloudSyncStatus.OFFLINE -> EspressoMedium
                                    else -> GoldDark
                                }
                            ) {
                                Text(
                                    text = when (syncState.status) {
                                        CloudSyncStatus.SYNCED -> "● Live Connected"
                                        CloudSyncStatus.SYNCING -> "⟳ Syncing..."
                                        CloudSyncStatus.OFFLINE -> "Local Cache Active"
                                        else -> "● Ready"
                                    },
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Last Synced:",
                                color = EspressoMedium,
                                fontSize = 12.sp
                            )
                            Text(
                                text = lastSyncText,
                                color = EspressoDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Recipes in Cloud:",
                                color = EspressoMedium,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (syncState.totalSyncedRecipes > 0) "${syncState.totalSyncedRecipes} recipes synced" else "Family archive synced",
                                color = EspressoDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "✨ How this works for cousins:\nWhenever Mom/Admin adds, modifies, or removes a recipe, the change is automatically uploaded to the cloud and instantly reflected on all cousins' phones when they open Forkfolio.",
                    color = EspressoDark,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onForceSync()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sync Now", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = EspressoMedium)
            ) {
                Text("Close")
            }
        }
    )
}
