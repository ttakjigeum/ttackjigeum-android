package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.dto.ProductDto
import com.devhjs.ttackjigeum_android.data.mapper.toDomain
import com.devhjs.ttackjigeum_android.data.mapper.toDto
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProdProductRepositoryImpl(
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
            // Firestore 'in' query supports up to 10 values generally, but for simplicity we can try whereIn.
            // Or if large list, might need multiple queries. Assuming small list for now.
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
            // Using a generated ID for document, but storing the Logic ID inside
            firestore.collection(collectionRegex).add(productDto).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
