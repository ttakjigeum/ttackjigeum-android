package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.dto.ProductDto
import com.devhjs.ttackjigeum_android.data.mapper.toDomain
import com.devhjs.ttackjigeum_android.data.mapper.toDto
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await

class FirestoreProductRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ProductRepository {


    private val collectionRegex = "products"

    override suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = firestore.collection(collectionRegex).get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(ProductDto::class.java)?.toDomain()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getProductById(id: Long): Product? {
        return try {
            val snapshot = firestore.collection(collectionRegex)
                .whereEqualTo("id", id)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                snapshot.documents[0].toObject(ProductDto::class.java)?.toDomain()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getProductByIds(ids: List<Long>): List<Product> =
        kotlinx.coroutines.coroutineScope {
            if (ids.isEmpty()) return@coroutineScope emptyList()
            return@coroutineScope try {
                ids.chunked(10).map { chunk ->
                    async {
                        val snapshot = firestore.collection(collectionRegex)
                            .whereIn("id", chunk)
                            .get()
                            .await()

                        snapshot.documents.mapNotNull { document ->
                            document.toObject(ProductDto::class.java)?.toDomain()
                        }
                    }
                }.awaitAll().flatten()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    override suspend fun addProduct(product: Product) {
        try {
            val productDto = product.toDto()
            // 문서에는 생성된 ID를 사용하지만, 내부에는 로직 ID를 저장합니다.
            firestore.collection(collectionRegex).add(productDto).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteProduct(id: Long) {
        try {
            val snapshot = firestore.collection(collectionRegex)
                .whereEqualTo("id", id)
                .get()
                .await()

            for (document in snapshot.documents) {
                document.reference.delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
