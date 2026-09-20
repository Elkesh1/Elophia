package com.example.elophia.ui.auth

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elophia.ui.components.ElophiaError
import com.example.elophia.ui.components.ElophiaLoading
import com.example.elophia.ui.components.ElophiaLogo
import com.example.elophia.viewmodel.AuthViewModel
import com.example.elophia.viewmodel.PhoneAuthState

@Composable
fun PhoneAuthScreen(
    onVerified: () -> Unit,
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity

    val phoneAuthState by authViewModel.phoneAuthState.collectAsState()

    var phoneNumber by remember { mutableStateOf("+234") }
    var otpCode by remember { mutableStateOf("") }
    var currentVerificationId by remember { mutableStateOf<String?>(null) }

    // When OTP is sent, remember the verificationId
    LaunchedEffect(phoneAuthState) {
        when (val state = phoneAuthState) {
            is PhoneAuthState.OtpSent ->  {
                currentVerificationId = state.verificationId
            }
            is PhoneAuthState.Verified -> {
                authViewModel.resetPhoneAuthState()
                onVerified()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        ElophiaLogo(
            showTagline = false,
            size = 80
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Determine which step we're on
        val isOtpStep = currentVerificationId != null

        if (!isOtpStep) {
            Text(
                text = "Enter your phone number",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We'll send you a code to verify",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { input ->
                    // Only digits and +
                    if (input.all { it.isDigit() || it == '+' }) {
                        phoneNumber = input
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone number") },
                placeholder = { Text("+2348012345678") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (phoneNumber.length >= 8) {
                        authViewModel.sendPhoneOtp(
                            activity = activity,
                            phoneNumber = phoneNumber.trim()
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = phoneAuthState !is PhoneAuthState.SendingOtp,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Send Code", fontWeight = FontWeight.Medium)
            }
        } else {
            Text(
                text = "Enter the code",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We sent a code to $phoneNumber",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = otpCode,
                onValueChange = { input ->
                    if (input.all { it.isDigit() } && input.length <= 6) {
                        otpCode = input
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("6-digit code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val vid = currentVerificationId
                    if (vid != null && otpCode.length >= 4) {
                        authViewModel.verifyPhoneOtp(vid, otpCode.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = phoneAuthState !is PhoneAuthState.Verifying,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Verify", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    // Reset to phone entry
                    currentVerificationId = null
                    otpCode = ""
                    authViewModel.resetPhoneAuthState()
                }
            ) {
                Text(
                    text = "← Change phone number",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading states
        when (phoneAuthState) {
            PhoneAuthState.SendingOtp -> {
                ElophiaLoading(message = "Sending code...")
            }

            PhoneAuthState.Verifying -> {
                ElophiaLoading(message = "Verifying...")
            }
            else -> Unit
        }

        // Error
        if (phoneAuthState is PhoneAuthState.Error) {
            ElophiaError(
                message = (phoneAuthState as PhoneAuthState.Error).message,
                onDismiss = {
                    authViewModel.resetPhoneAuthState()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to auth entry
        if (!isOtpStep) {
            TextButton(onClick = onBackClick) {
                Text(
                    text = "← Use a different method",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}