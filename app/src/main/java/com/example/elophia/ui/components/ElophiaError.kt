package com.example.elophia.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ElophiaError(
    message: String,
    onDismiss: () -> Unit = {}
) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )

    TextButton(
        onClick = onDismiss,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text("Dismiss")
    }
}