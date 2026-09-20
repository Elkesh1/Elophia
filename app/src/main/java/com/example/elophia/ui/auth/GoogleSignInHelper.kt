package com.example.elophia.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

object GoogleSignInHelper {

    suspend fun signIn(
        context: Context,
        webClientId: String,
        onIdToken: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)   // show all Google accounts
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result: GetCredentialResponse =
                credentialManager.getCredential(
                    context = context,
                    request = request
                )

            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                onIdToken(googleIdTokenCredential.idToken)
            } else {
                onError("Unexpected credential type")
            }
        } catch (e: GetCredentialException) {
            onError(e.message ?: "Google sign-in failed")
        } catch (e: Exception) {
            onError(e.message ?: "Google sign-in failed")
        }
    }
}