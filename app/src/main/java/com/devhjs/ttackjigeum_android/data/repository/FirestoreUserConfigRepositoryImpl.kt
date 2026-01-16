package com.devhjs.ttackjigeum_android.data.repository

import android.util.Log
import com.devhjs.ttackjigeum_android.data.dto.UserConfigDto
import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreUserConfigRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserConfigRepository {

    private val userId = "1" // Fixed User ID for now

    private fun getUserCollection() =
        firestore.collection("users").document(userId).collection("product")

    override suspend fun getUserConfig(productId: Long): UserConfig? {
        return try {
            val snapshot = getUserCollection().document(productId.toString()).get().await()
            val dto = snapshot.toObject(UserConfigDto::class.java)
            if (dto != null) {
                UserConfig(
                    productId = dto.productId ?: productId,
                    targetPrice = dto.targetPrice ?: 0,
                    notificationEnabled = dto.notificationEnabled ?: false
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreUserConfig", "Error getting user config", e)
            null
        }
    }

    override suspend fun getAllUserConfigs(): List<UserConfig> {
        return try {
            val snapshot = getUserCollection().get().await()
            snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(UserConfigDto::class.java)
                dto?.let {
                    UserConfig(
                        productId = it.productId ?: doc.id.toLongOrNull() ?: 0L,
                        targetPrice = it.targetPrice ?: 0,
                        notificationEnabled = it.notificationEnabled ?: false
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreUserConfig", "Error getting all user configs", e)
            emptyList()
        }
    }

    override fun getAllUserConfigsFlow(): Flow<List<UserConfig>> = callbackFlow {
        val listener = getUserCollection().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val configs = snapshot.documents.mapNotNull { doc ->
                    val dto = doc.toObject(UserConfigDto::class.java)
                    dto?.let {
                        UserConfig(
                            productId = it.productId ?: doc.id.toLongOrNull() ?: 0L,
                            targetPrice = it.targetPrice ?: 0,
                            notificationEnabled = it.notificationEnabled ?: false
                        )
                    }
                }
                trySend(configs)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun saveUserConfig(userConfig: UserConfig) {
        val dto = UserConfigDto(
            productId = userConfig.productId,
            targetPrice = userConfig.targetPrice,
            notificationEnabled = userConfig.notificationEnabled
        )
        try {
            getUserCollection().document(userConfig.productId.toString())
                .set(dto, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreUserConfig", "Error saving user config", e)
        }
    }

    override suspend fun updateUserConfig(userConfig: UserConfig) {
        saveUserConfig(userConfig)
    }

    override suspend fun deleteUserConfig(productId: Long) {
        try {
            getUserCollection().document(productId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e("FirestoreUserConfig", "Error deleting user config", e)
        }
    }

    override suspend fun deleteUserConfig(userConfig: UserConfig) {
        deleteUserConfig(userConfig.productId)
    }

    override suspend fun updateTargetPrice(productId: Long, targetPrice: Int) {
        try {
            getUserCollection().document(productId.toString())
                .update("targetPrice", targetPrice)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreUserConfig", "Error updating target price", e)
        }
    }
}
