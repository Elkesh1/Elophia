package com.example.elophia.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elophia.R
import com.example.elophia.ui.components.ElophiaError
import com.example.elophia.ui.components.ElophiaLoading
import com.example.elophia.ui.components.ElophiaLogo
import com.example.elophia.viewmodel.AuthState
import com.example.elophia.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthEntryScreen(
    onGoogleSuccess: () -> Unit,
    onPhoneClick: () -> Unit,
    onEmailClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authState by authViewModel.authState.collectAsState()

    // Navigate on Google success
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authViewModel.resetState()
            onGoogleSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElophiaLogo(
            showTagline = true,
            size = 100
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Welcome to Elophia",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose how you'd like to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // ─── Google ─────────────────────────────
        Button(
            onClick = {
                val webClientId = context.getString(R.string.default_web_client_id)
                scope.launch {
                    GoogleSignInHelper.signIn(
                        context = context,
                        webClientId = webClientId,
                        onIdToken = { idToken ->
                            authViewModel.signInWithGoogle(idToken)
                        },
                        onError = { message ->
                            // We can't easily set the error in the ViewModel here,
                            // so log it (and optionally show a toast later)
                            android.util.Log.e("GoogleSignIn", message)
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = authState !is AuthState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Continue with Google",
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ─── Phone ──────────────────────────────
        OutlinedButton(
            onClick = onPhoneClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = authState !is AuthState.Loading
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Continue with Phone",
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ─── Email ──────────────────────────────
        OutlinedButton(
            onClick = onEmailClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = authState !is AuthState.Loading
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Continue with Email",
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Loading
        if (authState is AuthState.Loading) {
            ElophiaLoading(message = "Signing you in...")
        }

        // Error
        if (authState is AuthState.Error) {
            ElophiaError(
                message = (authState as AuthState.Error).message,
                onDismiss = { authViewModel.resetState() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "By continuing, you agree to Elophia's Terms of Service and Privacy Policy.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}