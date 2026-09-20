package com.example.elophia.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
) {
    val categories = listOf(
        "Groceries",
        "Electronics",
        "Fashion",
        "Building",
        "Beauty",
        "Agriculture"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // Top bar

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Elophia"
            )

            Row {

                IconButton(
                    onClick = {
                        // Notifications later
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }

                IconButton(
                    onClick = {
                        // Profile later
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile"
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "What are you looking for?"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // Search

        OutlinedTextField(
            value = "",
            onValueChange = {
                // Search functionality later
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search products...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Browse Categories"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(categories) { category ->

                Card {

                    Text(
                        text = category,
                        modifier = Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 12.dp
                        )
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Text(
            text = "Nearby Shops"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Example Grocery Shop"
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Groceries • 2.4 km away"
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Price updated 2 days ago"
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}