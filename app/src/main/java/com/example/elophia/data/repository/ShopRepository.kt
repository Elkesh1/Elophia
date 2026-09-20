package com.example.elophia.data.repository

import android.util.Log.e
import com.example.elophia.data.model.Shop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ShopRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createShop(
        name: String,
        description: String,
        address: String,
    ): Result<String> {

        return try {

            val user = auth.currentUser
                ?: return Result.failure(
                    Exception("User is not signed in")
            )

            val shopref = firestore
                .collection("shops")
                .document()

            val shop = Shop(
                id = shopref.id,
                ownerId = user.uid,
                name = name,
                description = description,
                address = address
            )

            shopref.set(shop).await()

            Result.success(shopref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyShop(): Result<Shop?> {

        return try {

            val user = auth.currentUser
                ?: return Result.failure(
                    Exception("User is not signed in")
                )

            val snapShot = firestore
                .collection("shops")
                .whereEqualTo("ownerId", user.uid)
                .limit(1)
                .get()
                .await()

            val shop = snapShot.documents
                .firstOrNull()
                ?.toObject(Shop::class.java)

            Result.success(shop)
        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun getShop(
        shopId: String
    ): Result<Shop> {

        return try {

            val snapshot = firestore
                .collection("shops")
                .document(shopId)
                .get()
                .await()

            val shop = snapshot.toObject(Shop::class.java)

            if (shop != null) {
                Result.success(shop)
            } else {
                Result.failure(
                    Exception("Shop not found")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun getMyShops(): Result<List<Shop>> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("User is not signed in"))

            val snapshot = firestore
                .collection("shops")
                .whereEqualTo("ownerId", user.uid)
                .get()
                .await()

            val shops = snapshot.documents.mapNotNull {
                it.toObject(Shop::class.java)
            }

            Result.success(shops)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteShop(shopId: String): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("User is not signed in"))

            // Verify user owns this shop
            val shopDoc = firestore
                .collection("shops")
                .document(shopId)
                .get()
                .await()

            val shop = shopDoc.toObject(Shop::class.java)
            if (shop?.ownerId != user.uid) {
                return Result.failure(Exception("you don't own this shop"))
            }

            // Delete the shop
            firestore
                .collection("shops")
                .document(shopId)
                .delete()
                .await()
            // Delete all products belonging to this shop
            val productSnaphot = firestore
                .collection("products")
                .whereEqualTo("shopId", shopId)
                .get()
                .await()

            val batch = firestore.batch()
            productSnaphot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}