package com.runanywhere.startup_hackathon20.data.firebase

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.runanywhere.startup_hackathon20.R
import kotlinx.coroutines.tasks.await

/**
 * Manages Firebase Authentication
 * Handles Google Sign-In, Email/Password, and user session
 */
class FirebaseAuthManager(private val context: Context) {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestoreManager = FirestoreManager()

    companion object {
        private const val TAG = "FirebaseAuthManager"
    }

    /**
     * Get Google Sign-In Client
     * You need to add your Web Client ID from Firebase Console
     * to strings.xml as: <string name="default_web_client_id">YOUR_WEB_CLIENT_ID</string>
     */
    fun getGoogleSignInClient(): GoogleSignInClient {
        val webClientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            Log.w(TAG, "Web Client ID not configured properly, Google Sign-In may not work")
            "YOUR_WEB_CLIENT_ID_HERE" // fallback
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * Sign in with Google Account
     */
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null) {
                // Create/update user in Firestore
                firestoreManager.createOrUpdateUser(user)
                Log.d(TAG, "Google sign-in successful: ${user.email}")
                Result.success(user)
            } else {
                Result.failure(Exception("Sign-in failed: No user returned"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed", e)
            Result.failure(e)
        }
    }

    /**
     * Sign in with Email and Password
     */
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return Result.failure(Exception("Email and password cannot be empty"))
            }

            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                Log.d(TAG, "Email sign-in successful: ${user.email}")
                Result.success(user)
            } else {
                Result.failure(Exception("Sign-in failed: No user returned"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Email sign-in failed", e)
            Result.failure(e)
        }
    }

    /**
     * Register with Email and Password
     */
    suspend fun registerWithEmail(
        email: String,
        password: String,
        name: String,
        countryCode: String = "",
        phone: String = ""
    ): Result<FirebaseUser> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return Result.failure(Exception("Email and password cannot be empty"))
            }

            if (password.length < 6) {
                return Result.failure(Exception("Password must be at least 6 characters"))
            }

            // Step 1: Create user with email and password
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Step 2: Update Firebase Auth profile with display name
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user.updateProfile(profileUpdates).await()

                // Step 3: Create user document in Firestore with all details
                firestoreManager.createOrUpdateUser(
                    user = user,
                    displayName = name,
                    countryCode = countryCode,
                    phone = phone
                )

                Log.d(TAG, "Registration successful: ${user.email} with display name: $name")
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed: No user returned"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            Result.failure(e)
        }
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        try {
            // Sign out from Firebase
            auth.signOut()

            // Sign out from Google
            getGoogleSignInClient().signOut()

            Log.d(TAG, "User signed out successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed", e)
        }
    }

    /**
     * Get current logged-in user
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            if (email.isBlank()) {
                return Result.failure(Exception("Email cannot be empty"))
            }

            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent to: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Password reset failed", e)
            Result.failure(e)
        }
    }

    /**
     * Update user profile (display name)
     */
    suspend fun updateUserProfile(displayName: String): Result<Unit> {
        return try {
            val user = getCurrentUser() ?: return Result.failure(Exception("No user logged in"))

            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            user.updateProfile(profileUpdates).await()

            // Update in Firestore as well
            firestoreManager.updateUserDisplayName(user.uid, displayName)

            Log.d(TAG, "User profile updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Profile update failed", e)
            Result.failure(e)
        }
    }

    /**
     * Delete user account
     */
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = getCurrentUser() ?: return Result.failure(Exception("No user logged in"))

            // Delete user data from Firestore first
            firestoreManager.deleteUserData(user.uid)

            // Delete Firebase Auth account
            user.delete().await()

            Log.d(TAG, "User account deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Account deletion failed", e)
            Result.failure(e)
        }
    }
}