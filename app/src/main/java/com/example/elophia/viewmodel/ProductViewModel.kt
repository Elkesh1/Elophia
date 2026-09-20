package com.example.elophia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elophia.data.model.Product
import com.example.elophia.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProductState {
    data object Idle : ProductState

    data object Loading : ProductState

    data class ProductsLoaded(
        val products: List<Product>
    ) : ProductState

    data class ProductLoaded(
        val product: Product
    ) : ProductState

    data object Success : ProductState

    data object UpdateSuccess : ProductState

    data object DeleteSuccess : ProductState

    data class Error(
        val message: String
    ) : ProductState
}
class ProductViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _productState = MutableStateFlow<ProductState>(ProductState.Idle)
    val productState: StateFlow<ProductState> = _productState.asStateFlow()

    fun addProduct(
        shopId: String,
        name: String,
        description: String,
        category: String,
        price: Double,
        quantity: Int
    ) {

        viewModelScope.launch {
            _productState.value = ProductState.Loading

            val result = repository.addProduct(
                shopId = shopId,
                name = name,
                description = description,
                category = category,
                price = price,
                quantity = quantity
            )

            _productState.value = if (result.isSuccess) {
                ProductState.Success
            } else {
                ProductState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to add product"
                )
            }
        }
    }

    fun getProductsByShop(shopId: String) {

        viewModelScope.launch {

            _productState.value = ProductState.Loading

            val result = repository.getProductByShop(shopId)

            _productState.value =
                if (result.isSuccess) {

                    ProductState.ProductsLoaded(
                        products = result.getOrThrow()
                    )
                } else {

                    ProductState.Error(
                        result.exceptionOrNull()?.message
                            ?: "Failed to load products"
                    )
                }
        }
    }

    fun updateProduct(
        product: Product
    ) {

        viewModelScope.launch {

            _productState.value = ProductState.Loading

            val result = repository.updateProduct(product)

            _productState.value =
                if (result.isSuccess) {

                    ProductState.UpdateSuccess

                } else {

                    ProductState.Error(
                        result.exceptionOrNull()?.message
                            ?: "Failed to update product"
                    )
                }
        }
    }

    fun deleteProduct(
        productId: String
    ) {

        viewModelScope.launch {
            _productState.value = ProductState.Loading

            val result = repository.deleteProduct(productId)

            _productState.value =
                if (result.isSuccess) {

                    ProductState.DeleteSuccess
                } else {

                    ProductState.Error(
                        result.exceptionOrNull()?.message
                            ?: "Failed to delete product"
                    )
                }
        }
    }

    fun getProductById(productId: String) {

        viewModelScope.launch {

            _productState.value = ProductState.Loading

            val result = repository.getProductById(productId)

            _productState.value =
                if (result.isSuccess) {

                    val product = result.getOrNull()

                    if (product != null) {
                        ProductState.ProductLoaded(product)
                    } else {
                        ProductState.Error("Product not found")
                    }
                } else {

                    ProductState.Error(
                        result.exceptionOrNull()?.message
                            ?: "Failed to load product"
                    )
                }
        }
    }
}