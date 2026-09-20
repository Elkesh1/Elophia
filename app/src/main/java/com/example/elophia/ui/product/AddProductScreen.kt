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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elophia.ui.components.ElophiaLoading
import com.example.elophia.viewmodel.ProductState
import com.example.elophia.viewmodel.ProductViewModel

@Composable
fun AddProductScreen(
    shopId: String,
    onProductAdded: () -> Unit = {},
    productViewModel: ProductViewModel = viewModel()
) {

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    val productState by productViewModel.productState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Add Product"
        )

        Spacer(modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Product name")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp)
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

        Spacer(modifier = Modifier.height(12.dp)
        )

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

        Spacer(modifier = Modifier.height(12.dp)
        )

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

        Spacer(modifier = Modifier.height(12.dp)
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

        Spacer(modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {

                val productPrice = price.toDoubleOrNull()
                val productQuantity = quantity.toIntOrNull()

                if (name.isNotBlank() &&
                    category.isNotBlank() &&
                    productPrice != null &&
                    productQuantity != null
                    ) {

                    productViewModel.addProduct(
                        shopId = shopId,
                        name = name,
                        description = description,
                        category = category,
                        price = productPrice,
                        quantity = productQuantity
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(text = "Add Product"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = productState) {

            ProductState.Idle -> {
                // Nothing happening
            }

            ProductState.Loading -> {
                ElophiaLoading(
                    message = "Adding product..."
                )
            }

            ProductState.Success -> {
                LaunchedEffect(Unit) {
                    onProductAdded()
                }
            }

            is ProductState.Error -> {
                Text(
                    text = state.message
                )
            }

            else -> {
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddProductScreenPreview() {
    AddProductScreen(
        shopId = "preview-shop-id"
    )
}