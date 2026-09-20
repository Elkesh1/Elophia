package com.example.elophia.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elophia.data.model.ProductWithShop
import com.example.elophia.viewmodel.ExploreState
import com.example.elophia.viewmodel.ExploreViewModel

@Composable
fun ExploreScreen(
    exploreViewModel: ExploreViewModel = viewModel()
) {
    val exploreState by exploreViewModel.exploreState.collectAsState()
    val filters by exploreViewModel.filters.collectAsState()
    val categories by exploreViewModel.categories.collectAsState()

    // Local search input state (so typing doesn't trigger search on every keystroke)
    var searchQuery by remember { mutableStateOf(filters.query) }
    var quantityNeeded by remember { mutableStateOf(filters.quantityNeeded.toString()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Default categories if none loaded
    val displayCategories = remember(categories) {
        listOf("All") + categories
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ─── 1. Search Bar ─────────────────────────────
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search products...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ─── 2. Quantity Input ─────────────────────────
        OutlinedTextField(
            value = quantityNeeded,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() }) {
                    quantityNeeded = input
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Quantity needed") },
            placeholder = { Text("e.g. 50") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Search
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ─── 3. Category Chips ──────────────────────────
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayCategories) { category ->
                val isSelected = when (category) {
                    "All" -> filters.category == null
                    else -> filters.category == category
                }
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newFilters = filters.copy(
                            category = if (category == "All") null else category
                        )
                        exploreViewModel.updateFilters(newFilters)
                    },
                    label = { Text(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ─── 4. Search + Filter Row ────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val qty = quantityNeeded.toIntOrNull() ?: 1
                    val newFilters = filters.copy(
                        query = searchQuery.trim(),
                        quantityNeeded = qty
                    )
                    exploreViewModel.searchProducts(newFilters)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search")
            }

            IconButton(
                onClick = { showFilterSheet = true },
                modifier = Modifier
                    .height(48.dp)
                    .width(48.dp)
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Filters"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ─── 5. Results ────────────────────────────────
        when (val state = exploreState) {

            ExploreState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Start searching for products",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Type a product name, choose a category, or set your quantity.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            ExploreState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Searching...")
                    }
                }
            }

            is ExploreState.Results -> {
                if (state.products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No products found",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Try different keywords, a wider price range, or lower quantity.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    exploreViewModel.clearFilters()
                                    searchQuery = ""
                                    quantityNeeded = "1"
                                }
                            ) {
                                Text("Clear Filters")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "${state.products.size} result(s) found",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        items(state.products) { item ->
                            ProductSearchCard(
                                productWithShop = item,
                                quantityNeeded = filters.quantityNeeded,
                                exploreViewModel = exploreViewModel
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            is ExploreState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Couldn't load products",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please check your connection and try again.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val qty = quantityNeeded.toIntOrNull() ?: 1
                                val newFilters = filters.copy(
                                    query = searchQuery.trim(),
                                    quantityNeeded = qty
                                )
                                exploreViewModel.searchProducts(newFilters)
                            }
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }
    }
    // ─── 6. Filter Bottom Sheet (placeholder for now) ───
    if (showFilterSheet) {
        FilterPlaceholderDialog(
            onDismiss = { showFilterSheet = false }
        )
    }
}

/**
 * Temporary filter dialog. We'll replace this with a proper
 * FilterBottomSheet in Step 8.
 */
@Composable
private fun FilterPlaceholderDialog(
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filters") },
        text = {
            Text("Advanced filters coming in Step 8: price range, distance, sort, wholesale only...")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

/**
 * Temporary product card. We'll extract this to its own
 * file (ProductCard.kt) in Step 9.
 */
@Composable
private fun ProductSearchCard(
    productWithShop: ProductWithShop,
    quantityNeeded: Int,
    exploreViewModel: ExploreViewModel
) {
    val product = productWithShop.product
    val shop = productWithShop.shop

    val unitPrice = exploreViewModel.calculateUnitPrice(product, quantityNeeded)
    val totalPrice = unitPrice * quantityNeeded
    val canFulfill = exploreViewModel.canFulfillQuantity(product, quantityNeeded)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = shop.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "₦${"%.2f".format(product.price)} per unit",
                style = MaterialTheme.typography.bodyMedium
            )

            if (unitPrice != product.price) {
                Text(
                    text = "Bulk price: ₦${"%.2f".format(unitPrice)} per unit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (canFulfill) {
                    "✅ Can fulfill $quantityNeeded units"
                } else {
                    "⚠️ Only ${product.quantity} units available"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (canFulfill) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total: ₦${"%.2f".format(totalPrice)}",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}