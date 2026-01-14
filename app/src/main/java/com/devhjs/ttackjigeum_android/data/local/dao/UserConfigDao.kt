package com.devhjs.ttackjigeum_android.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devhjs.ttackjigeum_android.data.local.entity.UserConfigEntity

@Dao
interface UserConfigDao {

    @Query("SELECT * FROM user_configs WHERE product_id = :productId")
    suspend fun findById(productId: Long): UserConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userConfig: UserConfigEntity)

    @Update
    suspend fun update(userConfig: UserConfigEntity)

    @Delete
    suspend fun delete(userConfig: UserConfigEntity)

    @Query("DELETE FROM user_configs WHERE product_id = :productId")
    suspend fun deleteById(productId: Long)

    @Query("DELETE FROM user_configs WHERE product_id IN (:productIds)")
    suspend fun deleteByIds(productIds: List<Long>)
}