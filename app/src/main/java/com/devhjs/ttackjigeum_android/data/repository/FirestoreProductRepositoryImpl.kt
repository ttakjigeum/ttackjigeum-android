package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.dto.ProductDto
import com.devhjs.ttackjigeum_android.data.mapper.toDomain
import com.devhjs.ttackjigeum_android.data.mapper.toDto
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
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

    override suspend fun getProductByIds(ids: List<Long>): List<Product> {
        if (ids.isEmpty()) return emptyList()
        return try {
            // Firestore의 'in' 쿼리는 일반적으로 최대 10개의 값을 지원하지만, 여기서는 간단하게 whereIn을 시도합니다.
            // 리스트가 큰 경우 여러 쿼리가 필요할 수 있습니다. 현재는 작은 리스트라고 가정합니다.
            val snapshot = firestore.collection(collectionRegex)
                .whereIn("id", ids)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(ProductDto::class.java)?.toDomain()
            }
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
