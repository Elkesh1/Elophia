package com.example.elophia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elophia.data.model.Shop
import com.example.elophia.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ShopState {

    data object Idle: ShopState
    data object Loading: ShopState
    data object NoShop: ShopState
    data class ShopFound(
        val shop: Shop
    ) : ShopState

    data class ShopsLoaded(
        val shops: List<Shop>
    ) : ShopState
    data class Success(
        val shopId: String
    ) : ShopState
    object DeleteSuccess : ShopState
    data class Error(
        val message: String
    ) : ShopState
}
class ShopViewModel : ViewModel() {

    private val repository = ShopRepository()

    private val _shopState = MutableStateFlow<ShopState>(ShopState.Idle)
    val shopState: StateFlow<ShopState> = _shopState.asStateFlow()

    fun createShop(
        name: String,
        description: String,
        address: String
    ) {
        viewModelScope.launch {

            _shopState.value = ShopState.Loading
            val result = repository.createShop(
                name = name,
                description = description,
                address = address
            )

            _shopState.value =
                if (result.isSuccess) {

                    ShopState.Success(
                        shopId = result.getOrThrow()
                    )

                } else {
                    ShopState.Error(
                        result.exceptionOrNull()
                            ?.message
                            ?: "Failed to create shop"
                    )
                }
        }
    }

    fun checkMyShop() {

        viewModelScope.launch {

            _shopState.value = ShopState.Loading

            val result = repository.getMyShop()

            if (result.isFailure) {

                _shopState.value = ShopState.Error(
                    result.exceptionOrNull()?.message
                        ?: "Failed to load shop"
                )

                return@launch
            }

            val shop = result.getOrNull()

            _shopState.value =
                if (shop != null) {
                    ShopState.ShopFound(shop)
                } else {
                    ShopState.NoShop
                }
        }
    }

    fun getShop(shopId: String) {

        viewModelScope.launch {

            _shopState.value = ShopState.Loading

            val result = repository.getShop(shopId)

            _shopState.value =
                if (result.isSuccess) {

                    ShopState.ShopFound(
                        shop = result.getOrThrow()
                    )

                } else {

                    ShopState.Error(
                        result.exceptionOrNull()?.message
                            ?: "Failed to load shop"
                    )
                }
        }
    }

    fun getMyShops() {
        viewModelScope.launch {
            _shopState.value = ShopState.Loading

            val result = repository.getMyShops()

            _shopState.value = if (result.isSuccess) {
                val shops = result.getOrNull() ?: emptyList()
                if (shops.isEmpty()) {
                    ShopState.NoShop
                } else {
                    ShopState.ShopsLoaded(shops)
                }
            } else {
                ShopState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load shops"
                )
            }
        }
    }

    fun deleteShop(shopId: String) {
        viewModelScope.launch {
            _shopState.value = ShopState.Loading

            val result = repository.deleteShop(shopId)

            _shopState.value = if (result.isSuccess) {
                ShopState.DeleteSuccess
            } else {
                ShopState.Error(
                   result.exceptionOrNull()?.message ?: "Failed to delete shop"
                )
            }

            if (result.isSuccess) {
                getMyShops()
            }
        }
    }
}