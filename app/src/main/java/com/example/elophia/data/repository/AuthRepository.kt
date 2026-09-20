package com.example.elophia.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun isSignedIn(): Boolean = auth.currentUser != null

    suspend fun signUpWithEmail(
        fullName: String,
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val uid = result.user?.uid
                ?: throw Exception("User ID is missing")

            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build()
            result.user?.updateProfile(profileUpdates)?.await()

            //Create Firestore profile
            val user = hashMapOf(
                "uid" to uid,
                "fullName" to fullName,
                "email" to email,
                "phone" to "",
                "photoUrl" to "",
                "createdAt" to System.currentTimeMillis()
            )

            firestore
                .collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email,password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // GOOGLE
    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // Already signed in = link Google to current account
                currentUser.linkWithCredential(credential).await()
            } else {
                // Not signed in - sign in with Google
                val result = auth.signInWithCredential(credential).await()
                val user = result.user
                    ?: throw Exception("Google signin returned no user")

                // Ensure Firestore profile exists
                ensureUserProfile(
                    uid = user.uid,
                    fullName = user.displayName ?: "",
                    email = user.email ?: "",
                    phone = user.phoneNumber ?: "",
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // PHONE

    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<Unit> {
        return try {
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // Already signed in  - link phone to current account
                currentUser.linkWithCredential(credential).await()
            } else {
                // Not signed in - sign in with phone
                val result = auth.signInWithCredential(credential).await()
                val user = result.user
                    ?: throw Exception("Phone sign-in returned no user")

                ensureUserProfile(
                    uid = user.uid,
                    fullName = user.displayName ?: "",
                    email = user.email ?: "",
                    phone = user.phoneNumber ?: "",
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // HELPERS

    private suspend fun ensureUserProfile(
        uid: String,
        fullName: String,
        email: String,
        phone: String,
        photoUrl: String
    ) {
        val docRef = firestore.collection("users").document(uid)
        val snapshot = docRef.get().await()

        if (!snapshot.exists()) {
            val user = hashMapOf(
                "uid" to uid,
                "fullName" to fullName,
                "email" to email,
                "phone" to phone,
                "photoUrl" to photoUrl,
                "createdAt" to System.currentTimeMillis()
            )
            docRef.set(user).await()
        }
    }

    fun sendPhoneOtp(
        activity: Activity,
        phoneNumber: String,
        onCodeSent: (verificationId: String) -> Unit,
        onAutoVerified: (PhoneAuthCredential) -> Unit,
        onError: (String) -> Unit
    ) {
       val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

           override fun onVerificationCompleted(credential: PhoneAuthCredential) {
               onAutoVerified(credential)
           }

           override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
               onError(e.message ?: "Verification failed")
           }

           override fun onCodeSent(
               verificationId: String,
               token: PhoneAuthProvider.ForceResendingToken
           ) {
               onCodeSent(verificationId)
           }
       }

        PhoneAuthProvider
            .getInstance(auth)
            .verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                activity,
                callbacks
            )
    }

    suspend fun verifyPhoneOtp(
        verificationId: String,
        otpCode: String
    ): Result<Unit> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            signInWithPhoneCredential(credential)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}