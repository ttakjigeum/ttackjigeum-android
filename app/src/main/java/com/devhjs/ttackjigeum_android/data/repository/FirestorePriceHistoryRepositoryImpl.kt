package com.devhjs.ttackjigeum_android.data.repository

import android.util.Log
import com.devhjs.ttackjigeum_android.data.dto.PriceHistoryDto
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestorePriceHistoryRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PriceHistoryRepository {

    override suspend fun getPriceHistories(productId: Long): List<PriceHistory> {
        return try {
            val snapshot = firestore.collection("priceHistorys")
                .whereEqualTo("productId", productId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(PriceHistoryDto::class.java)
                if (dto?.datetime != null && dto.price != null) {
                    PriceHistory(
                        datetime = dto.datetime,
                        price = dto.price
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirestorePriceHistory", "Error getting price histories", e)
            emptyList()
        }
    }
}
