package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class CloudSyncStatus {
    INITIALIZING,
    READY,
    SYNCING,
    SYNCED,
    OFFLINE,
    ERROR
}

data class CloudSyncState(
    val status: CloudSyncStatus = CloudSyncStatus.INITIALIZING,
    val currentUser: FirebaseUser? = null,
    val lastSyncedTimestamp: Long = 0L,
    val totalSyncedRecipes: Int = 0,
    val errorMessage: String? = null
)

class FirebaseSyncManager(
    private val context: Context,
    private val recipeDao: RecipeDao,
    private val scope: CoroutineScope
) {
    private val TAG = "FirebaseSyncManager"
    private var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    private var snapshotListener: ListenerRegistration? = null

    private val _syncState = MutableStateFlow(CloudSyncState())
    val syncState: StateFlow<CloudSyncState> = _syncState.asStateFlow()

    init {
        initializeFirebase()
    }

    private fun initializeFirebase() {
        try {
            val apps = FirebaseApp.getApps(context)
            val app = if (apps.isEmpty()) {
                FirebaseApp.initializeApp(context)
            } else {
                apps.firstOrNull() ?: FirebaseApp.getInstance()
            }

            if (app != null) {
                try {
                    firestore = FirebaseFirestore.getInstance(app)
                    auth = FirebaseAuth.getInstance(app)

                    val currentUser = auth?.currentUser
                    _syncState.value = _syncState.value.copy(
                        status = CloudSyncStatus.READY,
                        currentUser = currentUser
                    )

                    // Start real-time listening to family recipes collection
                    startRealtimeListener()
                } catch (inner: Exception) {
                    Log.i(TAG, "Firestore/Auth not configured on default app: ${inner.message}")
                    _syncState.value = _syncState.value.copy(
                        status = CloudSyncStatus.OFFLINE,
                        errorMessage = "Local database active (Offline mode)"
                    )
                }
            } else {
                _syncState.value = _syncState.value.copy(
                    status = CloudSyncStatus.OFFLINE,
                    errorMessage = "Local database active (Offline mode)"
                )
            }
        } catch (e: Exception) {
            Log.i(TAG, "Firebase unavailable or running in local offline mode: ${e.message}")
            _syncState.value = _syncState.value.copy(
                status = CloudSyncStatus.OFFLINE,
                errorMessage = "Local database active (Offline mode)"
            )
        }
    }

    fun startRealtimeListener() {
        val db = firestore ?: return
        snapshotListener?.remove()

        try {
            _syncState.value = _syncState.value.copy(status = CloudSyncStatus.SYNCING)
            snapshotListener = db.collection("family_recipes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen failed", error)
                        _syncState.value = _syncState.value.copy(
                            status = CloudSyncStatus.OFFLINE,
                            errorMessage = error.message
                        )
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        scope.launch(Dispatchers.IO) {
                            val cloudRecipes = mutableListOf<RecipeEntity>()
                            for (doc in snapshot.documents) {
                                val isDeleted = doc.getBoolean("isDeleted") ?: false
                                if (isDeleted) continue

                                val title = doc.getString("title") ?: continue
                                val url = doc.getString("url") ?: ""
                                val platform = doc.getString("platform") ?: "OTHER"
                                val videoId = doc.getString("videoId")
                                val isVegetarian = doc.getBoolean("isVegetarian") ?: true
                                val bio = doc.getString("bio") ?: ""
                                val uploaderName = doc.getString("uploaderName") ?: "Mom"
                                val category = doc.getString("category") ?: "Mains"
                                val notes = doc.getString("notes") ?: ""
                                val ingredients = doc.getString("ingredients") ?: ""
                                val cookTime = doc.getString("cookTime") ?: "30 mins"
                                val servings = doc.getString("servings") ?: "4 servings"
                                val isFavorite = doc.getBoolean("isFavorite") ?: false
                                val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                                val cloudId = doc.id

                                cloudRecipes.add(
                                    RecipeEntity(
                                        title = title,
                                        url = url,
                                        platform = platform,
                                        videoId = videoId,
                                        isVegetarian = isVegetarian,
                                        bio = bio,
                                        uploaderName = uploaderName,
                                        category = category,
                                        notes = notes,
                                        ingredients = ingredients,
                                        cookTime = cookTime,
                                        servings = servings,
                                        isFavorite = isFavorite,
                                        createdAt = createdAt,
                                        cloudId = cloudId
                                    )
                                )
                            }

                            if (cloudRecipes.isNotEmpty()) {
                                recipeDao.syncCloudRecipes(cloudRecipes)
                            } else {
                                // If cloud is completely empty, seed initial recipes to cloud
                                seedLocalRecipesToCloud()
                            }

                            withContext(Dispatchers.Main) {
                                _syncState.value = _syncState.value.copy(
                                    status = CloudSyncStatus.SYNCED,
                                    lastSyncedTimestamp = System.currentTimeMillis(),
                                    totalSyncedRecipes = cloudRecipes.size,
                                    errorMessage = null
                                )
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting snapshot listener: ${e.message}", e)
        }
    }

    suspend fun seedLocalRecipesToCloud() {
        val db = firestore ?: return
        try {
            val localCount = recipeDao.getRecipeCount()
            if (localCount > 0) {
                val localRecipes = recipeDao.getAllRecipesList()
                for (recipe in localRecipes) {
                    uploadRecipeToCloud(recipe)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding recipes to cloud: ${e.message}", e)
        }
    }

    suspend fun uploadRecipeToCloud(recipe: RecipeEntity): String? = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext null
        try {
            val docId = if (recipe.cloudId.isNotBlank()) recipe.cloudId else "recipe_${recipe.createdAt}_${recipe.title.hashCode()}"
            val recipeData = hashMapOf(
                "title" to recipe.title,
                "url" to recipe.url,
                "platform" to recipe.platform,
                "videoId" to recipe.videoId,
                "isVegetarian" to recipe.isVegetarian,
                "bio" to recipe.bio,
                "uploaderName" to recipe.uploaderName,
                "category" to recipe.category,
                "notes" to recipe.notes,
                "ingredients" to recipe.ingredients,
                "cookTime" to recipe.cookTime,
                "servings" to recipe.servings,
                "isFavorite" to recipe.isFavorite,
                "createdAt" to recipe.createdAt,
                "updatedAt" to System.currentTimeMillis(),
                "isDeleted" to false
            )

            db.collection("family_recipes").document(docId)
                .set(recipeData, SetOptions.merge())
                .await()

            docId
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading recipe to cloud: ${e.message}", e)
            null
        }
    }

    suspend fun deleteRecipeFromCloud(cloudId: String) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        if (cloudId.isBlank()) return@withContext
        try {
            db.collection("family_recipes").document(cloudId)
                .update("isDeleted", true)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error marking recipe deleted in cloud: ${e.message}", e)
        }
    }

    suspend fun signInWithGoogle(webClientId: String): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth?.signInWithCredential(authCredential)?.await()
                val user = authResult?.user

                withContext(Dispatchers.Main) {
                    _syncState.value = _syncState.value.copy(currentUser = user)
                }
                Result.success(user)
            } else {
                Result.failure(Exception("Unsupported credential type received"))
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Google Sign-In failed: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Authentication failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
        _syncState.value = _syncState.value.copy(currentUser = null)
    }

    fun cleanup() {
        snapshotListener?.remove()
    }
}
