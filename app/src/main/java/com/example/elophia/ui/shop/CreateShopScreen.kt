package com.example.elophia.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import com.example.elophia.ui.components.ElophiaError
import com.example.elophia.ui.components.ElophiaLoading
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
import com.example.elophia.ui.components.ElophiaTopBar
import com.example.elophia.viewmodel.ShopState
import com.example.elophia.viewmodel.ShopViewModel

@Composable
fun CreateShopScreen(
    onShopCreated: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    shopViewModel: ShopViewModel = viewModel()
) {

    var shopName by remember { mutableStateOf("") }

    var description by remember { mutableStateOf("") }

    var address by remember { mutableStateOf("") }

    val shopState by shopViewModel.shopState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ElophiaTopBar(
            title = "Create Shop",
            onBackClick = onBackClick
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create your shop",
            )
            Spacer(modifier = Modifier.padding(24.dp)
            )

            OutlinedTextField(
                value = shopName,
                onValueChange = {
                    shopName = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Shop name")
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
                value = address,
                onValueChange = {
                    address = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Shop address")
                }
            )

            Spacer(modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = {
                    if (
                        shopName.isNotBlank() &&
                        address.isNotBlank()
                    ) {
                        shopViewModel.createShop(
                            name = shopName,
                            description = description,
                            address = address
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Shop")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = shopState) {

                ShopState.Idle -> {
                    // Nothing happening
                }

                ShopState.Loading -> {
                    Text("Creating shop...")
                }

                is ShopState.Success -> {
                    LaunchedEffect(state.shopId) {
                        onShopCreated(state.shopId)
                    }
                }

                is ShopState.Error -> {
                    Text(
                        text = state.message
                    )
                }

                else -> {

                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CreateShopScreenPreview() {
    CreateShopScreen()
}