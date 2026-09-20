package com.example.elophia.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elophia.viewmodel.ProductState
import com.example.elophia.viewmodel.ProductViewModel
import com.example.elophia.viewmodel.ShopState
import com.example.elophia.viewmodel.ShopViewModel

@Composable
fun MyShopScreen(
    shopId: String,
    onAddProductClick: () -> Unit,
    onEditProductClick: (String) -> Unit,
    onDeleteProduct: (String) -> Unit,
    shopViewModel: ShopViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel()
) {
    val shopState by shopViewModel.shopState.collectAsState()
    val productState by productViewModel.productState.collectAsState()

    // Delete dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDeleteId by remember { mutableStateOf<String?>(null) }
    var productToDeleteName by remember { mutableStateOf("") }

    LaunchedEffect(shopId) {
        shopViewModel.getShop(shopId)
        productViewModel.getProductsByShop(shopId)
    }

    // Handle delete click - shows dialog instead of immediate delete
    val handleDeleteClick: (String) -> Unit = { productId ->
        val product = (productState as? ProductState.ProductsLoaded)
            ?.products
            ?.find { it.id == productId }
        productToDeleteName = product?.name ?: "this product"
        productToDeleteId = productId
        showDeleteDialog = true
    }

    when (val state = shopState) {

        ShopState.Loading -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {

                CircularProgressIndicator()

                Text(
                    text = "Loading shop..."
                )
            }
        }

        is ShopState.ShopFound -> {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                item {

                    Text(
                        text = state.shop.name
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = state.shop.description ?: ""
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = state.shop.address
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Button(
                        onClick = onAddProductClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Product")
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    Text(
                        text = "Your Products"
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                when (val products = productState) {

                    ProductState.Loading -> {

                        item {
                            CircularProgressIndicator()
                        }
                    }

                    is ProductState.ProductsLoaded -> {

                        if (products.products.isEmpty()) {

                            item {
                                Text(
                                    text = "You haven't added any products yet."
                                )
                            }

                        } else {

                            items(products.products) { product ->

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical = 6.dp
                                        )
                                ) {

                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {

                                        Text(
                                            text = product.name
                                        )

                                        Spacer(
                                            modifier = Modifier.height(4.dp)
                                        )

                                        Text(
                                            text = product.description
                                        )

                                        Spacer(
                                            modifier = Modifier.height(4.dp)
                                        )

                                        Text(
                                            text = "₦${product.price}"
                                        )

                                        Text(
                                            text = "Quantity: ${product.quantity}"
                                        )

                                        Spacer(modifier = Modifier.height(12.dp)
                                        )

                                        Button(
                                            onClick = {
                                                onEditProductClick(product.id)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Edit")
                                        }

                                        Spacer(modifier = Modifier.height(8.dp)
                                        )

                                        Button(
                                            onClick = {
                                                handleDeleteClick(product.id)  // ← Use handleDeleteClick
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Delete")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is ProductState.Error -> {

                        item {
                            Text(
                                text = products.message
                            )
                        }
                    }

                    else -> {
                        // Nothing yet
                    }
                }
            }
        }

        is ShopState.Error -> {

            Text(
                text = state.message,
                modifier = Modifier.padding(24.dp)
            )
        }

        else -> {
            // Idle, NoShop and Success
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && productToDeleteId != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                productToDeleteId = null
            },
            title = {
                Text(
                    text = "Delete Product",
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '$productToDeleteName'?\n\nThis action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        productToDeleteId?.let { productId ->
                            productViewModel.deleteProduct(productId)
                            // Refresh the product list after deletion
                            productViewModel.getProductsByShop(shopId)
                        }
                        showDeleteDialog = false
                        productToDeleteId = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        productToDeleteId = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}