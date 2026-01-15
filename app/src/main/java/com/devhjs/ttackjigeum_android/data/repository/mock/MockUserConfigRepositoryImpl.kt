package com.devhjs.ttackjigeum_android.data.repository.mock

import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.RoomUserConfigRepository

class MockUserConfigRepositoryImpl : RoomUserConfigRepository {
    private val userConfigs = MockData.MockUserConfigs

    override suspend fun getUserConfig(productId: Long): UserConfig? {
        return userConfigs.find { it.productId == productId }
    }

    override suspend fun getAllUserConfigs(): List<UserConfig> {
        return userConfigs.toList()
    }

    override suspend fun saveUserConfig(userConfig: UserConfig) {
        val index = userConfigs.indexOfFirst { it.productId == userConfig.productId }
        if (index != -1) {
            userConfigs[index] = userConfig
        } else {
            userConfigs.add(userConfig)
        }
    }

    override suspend fun updateUserConfig(userConfig: UserConfig) {
        val index = userConfigs.indexOfFirst { it.productId == userConfig.productId }
        if (index != -1) {
            userConfigs[index] = userConfig
        }
    }

    override suspend fun deleteUserConfig(productId: Long) {
        userConfigs.removeAll { it.productId == productId }
    }

    override suspend fun deleteUserConfig(userConfig: UserConfig) {
        userConfigs.removeAll { it.productId == userConfig.productId }
    }
}
