package com.example.elophia.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.elophia.viewmodel.ShopState
import com.example.elophia.viewmodel.ShopViewModel

@Composable
fun ShopScreen(
    shopState: ShopState,
    onCreateShopClick: () -> Unit,
    onManageShopClick: (String) -> Unit,
    shopViewModel: ShopViewModel? = null
) {
    // Delete dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var shopToDeleteId by remember { mutableStateOf<String?>(null) }
    var shopToDeleteName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = shopState) {

            ShopState.Idle -> {
                Text("Loading...")
            }

            ShopState.Loading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Loading your shops...")
            }

            ShopState.NoShop -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "You don't have a shop yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onCreateShopClick
                    ) {
                        Text("Create Your First Shop")
                    }
                }
            }

            is ShopState.ShopsLoaded -> {
                Text(
                    text = "My Shops",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                if (state.shops.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("You don't have any shops yet")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onCreateShopClick) {
                            Text("Create Your First Shop")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.shops) { shop ->
                            ShopCard(
                                shopName = shop.name,
                                shopAddress = shop.address,
                                onManageClick = { onManageShopClick(shop.id) },
                                onDeleteClick = {
                                    shopToDeleteName = shop.name
                                    shopToDeleteId = shop.id
                                    showDeleteDialog = true
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onCreateShopClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Add Another Shop")
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }

            is ShopState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Error loading shops",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { shopViewModel?.getMyShops() }
                    ) {
                        Text("Retry")
                    }
                }
            }

            is ShopState.ShopFound -> {
                Text(
                    text = "My Shop",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                ShopCard(
                    shopName = state.shop.name,
                    shopAddress = state.shop.address,
                    onManageClick = { onManageShopClick(state.shop.id) },
                    onDeleteClick = {
                        shopToDeleteName = state.shop.name
                        shopToDeleteId = state.shop.id
                        showDeleteDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onCreateShopClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Add Another Shop")
                }
            }

            is ShopState.DeleteSuccess -> {
                // Handle delete success - already refreshing in ViewModel
            }

            is ShopState.Success -> {
                // Shop creation navigation is handled separately.
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && shopToDeleteId != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                shopToDeleteId = null
            },
            title = {
                Text(
                    text = "Delete Shop",
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '$shopToDeleteName'?\n\nThis will also delete ALL products in this shop.\n\nThis action cannot be undone!"
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        shopToDeleteId?.let { shopId ->
                            shopViewModel?.deleteShop(shopId)
                        }
                        showDeleteDialog = false
                        shopToDeleteId = null
                    }
                ) {
                    Text("Delete Shop")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        shopToDeleteId = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ShopCard(
    shopName: String,
    shopAddress: String,
    onManageClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = shopName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = shopAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Shop",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onManageClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Shop")
            }
        }
    }
}