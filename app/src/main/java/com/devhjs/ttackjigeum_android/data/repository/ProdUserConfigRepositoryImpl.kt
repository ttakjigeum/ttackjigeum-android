package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.local.dao.UserConfigDao
import com.devhjs.ttackjigeum_android.data.mapper.toDomain
import com.devhjs.ttackjigeum_android.data.mapper.toEntity
import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.RoomUserConfigRepository
// import com.google.firebase.firestore.FirebaseFirestore

class ProdUserConfigRepositoryImpl(
    private val userConfigDao: UserConfigDao,
    // private val firestore: FirebaseFirestore  // TODO: Inject Firestore when ready
) : RoomUserConfigRepository {

    override suspend fun getUserConfig(productId: Long): UserConfig? {
        // TODO: Try fetching from Firestore first or in parallel?
        return userConfigDao.findById(productId)?.toDomain()
    }

    override suspend fun getAllUserConfigs(): List<UserConfig> {
        // TODO: Sync with Firestore?
        return userConfigDao.getAll().map { it.toDomain() }
    }

    override suspend fun saveUserConfig(userConfig: UserConfig) {
        userConfigDao.insert(userConfig.toEntity())
        // TODO: Save to Firestore
    }

    override suspend fun updateUserConfig(userConfig: UserConfig) {
        userConfigDao.update(userConfig.toEntity())
        // TODO: Update Firestore
    }

    override suspend fun deleteUserConfig(productId: Long) {
        userConfigDao.deleteById(productId)
        // TODO: Delete from Firestore
    }

    override suspend fun deleteUserConfig(userConfig: UserConfig) {
        userConfigDao.delete(userConfig.toEntity())
        // TODO: Delete from Firestore
    }

    override suspend fun updateTargetPrice(productId: Long, targetPrice: Int) {
        userConfigDao.updateTargetPrice(productId, targetPrice)
        // TODO: Update target price in Firestore
    }
}
