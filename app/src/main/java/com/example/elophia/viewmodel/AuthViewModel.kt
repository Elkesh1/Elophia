package com.example.elophia.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elophia.data.repository.AuthRepository
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class PhoneAuthState {
    object Idle : PhoneAuthState()
    object SendingOtp : PhoneAuthState()
    data class OtpSent(val verificationId: String) : PhoneAuthState()
    object Verifying : PhoneAuthState()
    object Verified : PhoneAuthState()
    data class Error(val message: String) : PhoneAuthState()
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _phoneAuthState = MutableStateFlow<PhoneAuthState>(PhoneAuthState.Idle)
    val phoneAuthState: StateFlow<PhoneAuthState> = _phoneAuthState.asStateFlow()

    // ═══════════════════════════════════════════════
    // EMAIL
    // ═══════════════════════════════════════════════

    fun signUpWithEmail(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUpWithEmail(fullName, email, password)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success },
                onFailure = { AuthState.Error(it.message ?: "Sign up failed") }
            )
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signInWithEmail(email, password)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success },
                onFailure = { AuthState.Error(it.message ?: "Sign in failed") }
            )
        }
    }

    // ═══════════════════════════════════════════════
    // GOOGLE
    // ═══════════════════════════════════════════════

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signInWithGoogle(idToken)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success },
                onFailure = { AuthState.Error(it.message ?: "Google sign-in failed") }
            )
        }
    }

    // ═══════════════════════════════════════════════
    // PHONE
    // ═══════════════════════════════════════════════

    fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signInWithPhoneCredential(credential)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success },
                onFailure = { AuthState.Error(it.message ?: "Phone sign-in failed") }
            )
        }
    }

    fun sendPhoneOtp(activity: Activity, phoneNumber: String) {
        _phoneAuthState.value = PhoneAuthState.SendingOtp

        repository.sendPhoneOtp(
            activity = activity,
            phoneNumber = phoneNumber,
            onCodeSent = { verificationId ->
                _phoneAuthState.value = PhoneAuthState.OtpSent(verificationId)
            },
            onAutoVerified = { credential ->
                // Firebase auto-verified - sign in directly
                viewModelScope.launch {
                    _phoneAuthState.value = PhoneAuthState.Verifying
                    val result = repository.signInWithPhoneCredential(credential)
                    _phoneAuthState.value = result.fold(
                        onSuccess = { PhoneAuthState.Verified },
                        onFailure = {
                            PhoneAuthState.Error(it.message ?: "Sign-in failed")
                        }
                    )
                }
            },
            onError = { message ->
                _phoneAuthState.value = PhoneAuthState.Error(message)
            }
        )
    }

    fun verifyPhoneOtp(verificationId: String, otpCode: String) {
        viewModelScope.launch {
            _phoneAuthState.value = PhoneAuthState.Verifying
            val result = repository.verifyPhoneOtp(verificationId, otpCode)
            _phoneAuthState.value = result.fold(
                onSuccess = { PhoneAuthState.Verified },
                onFailure = {
                    PhoneAuthState.Error(it.message ?: "Invalid code")
                }
            )
        }
    }

    fun resetPhoneAuthState() {
        _phoneAuthState.value = PhoneAuthState.Idle
    }
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}