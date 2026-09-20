package com.example.elophia.ui.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.elophia.ui.components.ElophiaLogo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    // Check auth state when the splash appears
    LaunchedEffect(Unit) {
        // Small delay so the user sees the brand (not a flash)
        delay(1200)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            onNavigateToHome()
        } else {
            onNavigateToAuth()
        }
    }

    // ---- UI -----
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo + brand name
            ElophiaLogo(
                showTagline = true,
                size = 120
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading spinner (gold by default via theme)
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Connecting...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}