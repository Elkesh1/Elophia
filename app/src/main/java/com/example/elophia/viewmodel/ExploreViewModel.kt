package com.example.elophia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elophia.data.model.Product
import com.example.elophia.data.model.ProductWithShop
import com.example.elophia.data.model.SearchFilters
import com.example.elophia.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ExploreState {
    object Idle : ExploreState()
    object Loading : ExploreState()
    data class Results(val products: List<ProductWithShop>) : ExploreState()
    data class Error(val message: String) : ExploreState()
}

class ExploreViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _exploreState = MutableStateFlow<ExploreState>(ExploreState.Idle)
    val exploreState: StateFlow<ExploreState> = _exploreState.asStateFlow()

    private val _filters = MutableStateFlow(SearchFilters())
    val filters: StateFlow<SearchFilters> = _filters.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    fun searchProducts(filters: SearchFilters) {
        viewModelScope.launch {
            _exploreState.value = ExploreState.Loading
            _filters.value = filters

            val result = repository.searchProducts(filters)

            _exploreState.value = if (result.isSuccess) {
                val products = result.getOrNull() ?: emptyList()
                val productsWithShop = mutableListOf<ProductWithShop>()

                for (product in products) {
                    val detailResult = repository.getProductWithShopDetails(product.id)
                    detailResult.getOrNull()?.let {
                        productsWithShop.add(it)
                    }
                }

                ExploreState.Results(productsWithShop)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                android.util.Log.e("ExploreViewModel", "Search failed: $errorMsg")
                ExploreState.Error(errorMsg)  // Still passes raw msg to state
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            val result = repository.getCategories()
            if (result.isSuccess) {
                val categories = result.getOrNull() ?: emptyList()
                _categories.value = categories
                // Nothing to set on _exploreState — categories are separate
            }
        }
    }

    fun updateFilters(filters: SearchFilters) {
        _filters.value = filters
    }

    fun clearFilters() {
        _filters.value = SearchFilters()
        _exploreState.value = ExploreState.Idle
    }

    fun searchWithCurrentFilters() {
        searchProducts(_filters.value)
    }

    fun calculateUnitPrice(product: Product, quantity: Int): Double {
        if (product.wholesaleAvailable && product.bulkPricing != null) {
            val bulkPricing = product.bulkPricing ?: emptyMap()
            val sortedTiers = bulkPricing.keys
                .mapNotNull { it.toIntOrNull() }
                .sortedDescending()

            for (tier in sortedTiers) {
                if (quantity >= tier) {
                    return bulkPricing[tier.toString()] ?: product.price
                }
            }
        }
        return product.price
    }

    fun canFulfillQuantity(product: Product, quantity: Int): Boolean {
        return product.quantity >= quantity
    }
}