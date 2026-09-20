package com.example.elophia.data.model

data class Product(
    val id: String = "",
    val shopId: String = "",
    val sellerId: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imageUrl: String = "",
    val lastUpdated: Long = 0L,

    val minOrderQuantity: Int = 1,
    val wholesaleAvailable: Boolean = false,
    val bulkPricing: Map<String, Double>? = null,
    val searchKeywords: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isActive: Boolean = true,
    val views: Int = 0,
    val orders: Int = 0
)