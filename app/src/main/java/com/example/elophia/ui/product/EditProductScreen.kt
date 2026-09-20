package com.example.elophia.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import com.example.elophia.data.model.Product
import com.example.elophia.viewmodel.ProductState
import com.example.elophia.viewmodel.ProductViewModel

@Composable
fun EditProductScreen(
    productId: String,
    onProductUpdated: () -> Unit,
    productViewModel: ProductViewModel = viewModel()
) {

    LaunchedEffect(productId) {
        productViewModel.getProductById(productId)
    }
    val productState by productViewModel.productState.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    // Store the loaded product
    var currentProduct by remember { mutableStateOf<Product?>(null) }

    when (val state = productState) {
        is ProductState.ProductLoaded -> {
            val product = state.product
            currentProduct = product
            LaunchedEffect(product) {
                name = product.name
                description = product.description
                category = product.category
                price = product.price.toString()
                quantity = product.quantity.toString()
            }
        }
        else -> Unit
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Edit Product"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Show loading/error states
        when (val state = productState) {
            ProductState.Loading -> {
                Text("Loading product...")
                Spacer(modifier = Modifier.height(16.dp))
            }
            is ProductState.Error -> {
                Text("Error: ${state.message}")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { productViewModel.getProductById(productId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Retry")
                }
                return
            }
            else -> Unit
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = {
                Text("Product Name")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Description")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Category")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Price")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = {
                quantity = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Quantity")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {

                val productPrice =
                    price.toDoubleOrNull()

                val productQuantity =
                    quantity.toIntOrNull()

                if (
                    name.isNotBlank() &&
                    category.isNotBlank() &&
                    productPrice != null &&
                    productQuantity != null &&
                    currentProduct != null  // ← Make sure we have a product
                ) {

                    productViewModel.updateProduct(
                        currentProduct!!.copy(  // ← FIXED: Use currentProduct
                            name = name,
                            description = description,
                            category = category,
                            price = productPrice,
                            quantity = productQuantity
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = productState) {

            ProductState.Idle -> Unit

            ProductState.Loading -> {
                Text("Saving...")
            }

            ProductState.UpdateSuccess -> {

                LaunchedEffect(Unit) {
                    onProductUpdated()
                }
            }

            is ProductState.Error -> {
                Text(state.message)
            }

            else -> Unit
        }
    }
}