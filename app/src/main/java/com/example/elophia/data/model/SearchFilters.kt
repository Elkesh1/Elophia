package com.example.elophia.data.model

data class SearchFilters(
    val query: String = "",
    val category: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val quantityNeeded: Int = 1,
    val purchaseType: PurchaseType = PurchaseType.ALL,
    val maxDistance: Int = 50,  // in km
    val sortBy: SortOption = SortOption.RELEVANCE,
    val inStockOnly: Boolean = true,
    val canFulfillOnly: Boolean = true,
    val hasWholesale: Boolean = false
)

enum class PurchaseType {
    ALL, RETAIL, WHOLESALE, BULK
}

enum class SortOption {
    RELEVANCE,
    PRICE_LOW_HIGH,
    PRICE_HIGH_LOW,
    DISTANCE_NEAREST,
    STOCK_HIGHEST
}
data class ProductWithShop(
    val product: Product,
    val shop: Shop
)

