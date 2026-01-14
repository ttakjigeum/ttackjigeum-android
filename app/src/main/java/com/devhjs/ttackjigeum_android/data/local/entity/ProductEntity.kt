package com.devhjs.ttackjigeum_android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "original_price")
    val originalPrice: Int,

    @ColumnInfo(name = "current_price")
    val currentPrice: Int,

    @ColumnInfo(name = "target_price")
    val targetPrice: Int,

    @ColumnInfo(name = "lowest_price")
    val lowestPrice: Int,

    @ColumnInfo(name = "average_price")
    val averagePrice: Int,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String
)