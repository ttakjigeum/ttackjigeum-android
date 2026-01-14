package com.devhjs.ttackjigeum_android.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devhjs.ttackjigeum_android.data.local.dao.ProductDao
import com.devhjs.ttackjigeum_android.data.local.dao.UserConfigDao
import com.devhjs.ttackjigeum_android.data.local.entity.ProductEntity
import com.devhjs.ttackjigeum_android.data.local.entity.UserConfigEntity

@Database(
    entities = [
        ProductEntity::class,
        UserConfigEntity::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun userConfigDao(): UserConfigDao
}
