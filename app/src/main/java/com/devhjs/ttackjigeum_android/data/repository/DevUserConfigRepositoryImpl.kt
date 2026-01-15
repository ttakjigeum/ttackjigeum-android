package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.local.dao.UserConfigDao
import com.devhjs.ttackjigeum_android.data.mapper.toDomain
import com.devhjs.ttackjigeum_android.data.mapper.toEntity
import com.devhjs.ttackjigeum_android.data.repository.mock.MockData
import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.RoomUserConfigRepository

class DevUserConfigRepositoryImpl(
    private val userConfigDao: UserConfigDao,
) : RoomUserConfigRepository {

    override suspend fun getUserConfig(productId: Long): UserConfig? {
        return userConfigDao.findById(productId)?.toDomain()
    }

    override suspend fun getAllUserConfigs(): List<UserConfig> {
        val currentList = userConfigDao.getAll().map { it.toDomain() }
        if (currentList.isEmpty()) {
            // Seed with Mock Data if empty
            MockData.MockUserConfigs.forEach { mockConfig ->
                userConfigDao.insert(mockConfig.toEntity())
            }
            return userConfigDao.getAll().map { it.toDomain() }
        }
        return currentList
    }

    override suspend fun saveUserConfig(userConfig: UserConfig) {
        userConfigDao.insert(userConfig.toEntity())
    }

    override suspend fun updateUserConfig(userConfig: UserConfig) {
        userConfigDao.update(userConfig.toEntity())
    }

    override suspend fun deleteUserConfig(productId: Long) {
        userConfigDao.deleteById(productId)
    }

    override suspend fun deleteUserConfig(userConfig: UserConfig) {
        userConfigDao.delete(userConfig.toEntity())
    }

    override suspend fun updateTargetPrice(productId: Long, targetPrice: Int) {
        userConfigDao.updateTargetPrice(productId, targetPrice)
    }
}
