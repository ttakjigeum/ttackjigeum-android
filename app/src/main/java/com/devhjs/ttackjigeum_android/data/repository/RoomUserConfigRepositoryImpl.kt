package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.local.dao.UserConfigDao
import com.devhjs.ttackjigeum_android.data.mapper.toDomain
import com.devhjs.ttackjigeum_android.data.mapper.toEntity
import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.RoomUserConfigRepository

class RoomUserConfigRepositoryImpl(
    private val userConfigDao: UserConfigDao,
) : RoomUserConfigRepository {

    override suspend fun getUserConfig(productId: Long): UserConfig? {
        return userConfigDao.findById(productId)?.toDomain()
    }

    override suspend fun getAllUserConfigs(): List<UserConfig> {
        return userConfigDao.getAll().map { it.toDomain() }
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
}
