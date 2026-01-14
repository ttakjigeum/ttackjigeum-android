package com.devhjs.ttackjigeum_android.domain.repository

import com.devhjs.ttackjigeum_android.domain.model.UserConfig

interface UserConfigRepository {
    suspend fun getUserConfig(productId: Long): UserConfig?
    suspend fun saveUserConfig(userConfig: UserConfig)
    suspend fun updateUserConfig(userConfig: UserConfig)
    suspend fun deleteUserConfig(productId: Long)
    suspend fun deleteUserConfig(userConfig: UserConfig)
}
