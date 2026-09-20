package com.example.elophia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elophia.R


@Composable
fun ElophiaLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = true,
    size: Int = 120
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo image
        Image(
            painter = painterResource(id = R.drawable.elophia_splash),
            contentDescription = "Elophia Logo",
            modifier = Modifier.size(size.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Brand name
        Text(
            text = "ELOPHIA",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        // Gold underline accent
        Spacer(modifier = Modifier.height(6.dp))
        GoldUnderline()

        // Tagline
        if (showTagline) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Your local marketplace",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Small gold underline accent used under the brand name.
 */

@Composable
private fun GoldUnderline() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(width = 60.dp, height = 3.dp)
            .then(
                Modifier
            )
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier) {
            drawRoundRect(
                color = androidx.compose.ui.graphics.Color(0xFFE5B95C),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
            )
        }
    }
}