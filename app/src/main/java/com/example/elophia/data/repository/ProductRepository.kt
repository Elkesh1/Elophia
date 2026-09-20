package com.example.elophia.data.repository

import android.app.DownloadManager
import com.example.elophia.data.model.Product
import com.example.elophia.data.model.ProductWithShop
import com.example.elophia.data.model.SearchFilters
import com.example.elophia.data.model.Shop
import com.example.elophia.data.model.SortOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun addProduct(
        shopId: String,
        name: String,
        description: String,
        category: String,
        price: Double,
        quantity: Int
    ): Result<Unit> {

        return try {

            val user = auth.currentUser ?: return Result.failure(
                Exception("User is not signed in")
            )

            val productRef = firestore
                .collection("products")
                .document()

            val product = Product(
                id = productRef.id,
                shopId = shopId,
                sellerId = user.uid,
                name = name,
                description = description,
                category = category,
                price = price,
                quantity = quantity,
                lastUpdated = System.currentTimeMillis()
            )

            productRef.set(product).await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun getProductByShop(
        shopId: String
    ): Result<List<Product>> {

        return try {

            val snapshot = firestore
                .collection("products")
                .whereEqualTo("shopId", shopId)
                .get()
                .await()

            val products = snapshot.toObjects(Product::class.java)

            Result.success(products)
        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun updateProduct(
        product: Product
    ): Result<Unit> {

        return try {

            firestore
                .collection("products")
                .document(product.id)
                .set(
                    product.copy(
                        lastUpdated = System.currentTimeMillis()
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun deleteProduct(
        productId: String
    ): Result<Unit> {

        return try {

            firestore
                .collection("products")
                .document(productId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun getProductById(
        productId: String
    ): Result<Product> {

        return try {

            val snapshot = firestore
                .collection("products")
                .document(productId)
                .get()
                .await()

            val product =snapshot.toObject(Product::class.java)

            if (product != null) {
                Result.success(product)
            } else {
                Result.failure(
                    Exception("Product not found")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(filters: SearchFilters): Result<List<Product>> {
        return try {
            var query = firestore
                .collection("products")
                .whereEqualTo("isActive", true)

            // Apply category filter
           if (filters.query.isNotBlank()) {
               val searchTerm = filters.query.lowercase().trim()
               query = query.whereArrayContains("searchKeywords", searchTerm)
           }

            filters.category?.let { category ->
                if (category != "All") {
                    query = query.whereEqualTo("category", category)
                }
            }

            // Apply price range
            filters.minPrice?.let {
                query = query.whereGreaterThanOrEqualTo("price", it)
            }
            filters.maxPrice?.let {
                query = query.whereLessThanOrEqualTo("price", it)
            }

            // Apply stock filter
            if (filters.inStockOnly) {
                query = query.whereGreaterThan("quantity", 0)
            }

            // Apply sort
            query = when (filters.sortBy) {
                SortOption.PRICE_LOW_HIGH -> query.orderBy("price", Query.Direction.ASCENDING)
                SortOption.PRICE_HIGH_LOW -> query.orderBy("price", Query.Direction.DESCENDING)
                SortOption.STOCK_HIGHEST -> query.orderBy("quantity", Query.Direction.DESCENDING)
                else -> query.orderBy("lastUpdated", Query.Direction.DESCENDING)
            }

            // Execute query
            val snapshot = query.get().await()
            val products = snapshot.toObjects(Product::class.java)
                .filter { it.id.isNotEmpty() }

            // Post-process: Apply quantity fulfillment filter
            val filteredProducts = if (filters.canFulfillOnly) {
                products.filter {
                    it.quantity >= filters.quantityNeeded
                }
            } else {
                products
            }

            // Post-process: Apply wholesale filter
            val finalProducts = if (filters.hasWholesale) {
                filteredProducts.filter { it.wholesaleAvailable }
            } else {
                filteredProducts
            }

            Result.success(finalProducts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductWithShopDetails(productId: String): Result<ProductWithShop> {
        // Combine product + shop data for detail view
        return try {
            val productDoc = firestore
                .collection("products")
                .document(productId)
                .get()
                .await()

            val product = productDoc.toObject(Product::class.java)
                ?: return Result.failure(Exception("Product not found"))

            val shopDoc = firestore
                .collection("shops")
                .document(product.shopId)
                .get()
                .await()

            val shop = shopDoc.toObject(Shop::class.java)
                ?: return Result.failure(Exception("Shop not found"))

            Result.success(ProductWithShop(product, shop))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<String>> {
        return try {
            val snapshot = firestore
                .collection("products")
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val categories = snapshot.documents
                .mapNotNull { it.getString("category") }
                .distinct()
                .sorted()

            Result.success(categories)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateSearchKeywords(product: Product): List<String> {
        val keywords = mutableListOf<String>()

        // Add product name words
        keywords.addAll(product.name.lowercase().split(" "))

        // Add category
        keywords.add(product.category.lowercase())

        // Add description words (limit to important ones)
        product.description.split(" ")
            .filter { it.length > 3 }
            .map { it.lowercase() }
            .let { keywords.addAll(it) }

        // Add custom tags based on product properties
        when (product.category.lowercase()) {
            "electronics" -> {
                keywords.addAll(listOf("tech", "gadget", "device", "electronics"))
            }
            "fashion" -> {
                keywords.addAll(listOf("clothing", "wear", "style", "fashion"))
            }
            "groceries" -> {
                keywords.addAll(listOf("food", "grocery", "kitchen"))
            }
            "building" -> {
                keywords.addAll(listOf("construction", "hardware", "build"))
            }
        }

        // Remove duplicate and empty strings
        return keywords
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    }
}