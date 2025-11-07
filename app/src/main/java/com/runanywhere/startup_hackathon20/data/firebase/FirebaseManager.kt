package com.runanywhere.startup_hackathon20.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.runanywhere.startup_hackathon20.data.firebase.models.*
import com.runanywhere.startup_hackathon20.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Manages all Firestore database operations
 * Collections structure:
 * - users/{userId}
 * - users/{userId}/plans/{planId}
 * - users/{userId}/likedDestinations/{destinationId}
 * - destinations/{destinationId}
 */
class FirestoreManager {

    private val db: FirebaseFirestore = Firebase.firestore

    companion object {
        private const val TAG = "FirestoreManager"

        // Collection names
        private const val USERS_COLLECTION = "users"
        private const val PLANS_COLLECTION = "plans"
        private const val LIKED_DESTINATIONS_COLLECTION = "likedDestinations"
        private const val DESTINATIONS_COLLECTION = "destinations"
    }

    // ==================== User Management ====================

    /**
     * Create or update user document in Firestore
     */
    suspend fun createOrUpdateUser(
        user: FirebaseUser,
        displayName: String? = null,
        countryCode: String = "",
        phone: String = ""
    ): Result<Unit> {
        return try {
            val userData = hashMapOf(
                "uid" to user.uid,
                "email" to (user.email ?: ""),
                "displayName" to (displayName ?: user.displayName ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "countryCode" to countryCode,
                "phone" to phone,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            // Use set with merge to update existing or create new
            db.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(userData, com.google.firebase.firestore.SetOptions.merge())
                .await()

            Log.d(
                TAG,
                "User created/updated: ${user.uid} with displayName: ${displayName ?: user.displayName}"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create/update user: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get user data from Firestore
     */
    suspend fun getUser(userId: String): Result<com.runanywhere.startup_hackathon20.data.firebase.models.FirebaseUser?> {
        return try {
            val snapshot = db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val user =
                snapshot.toObject(com.runanywhere.startup_hackathon20.data.firebase.models.FirebaseUser::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user", e)
            Result.failure(e)
        }
    }

    /**
     * Update user display name
     */
    suspend fun updateUserDisplayName(userId: String, displayName: String): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .update(
                    mapOf(
                        "displayName" to displayName,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update display name", e)
            Result.failure(e)
        }
    }

    /**
     * Delete all user data from Firestore
     */
    suspend fun deleteUserData(userId: String): Result<Unit> {
        return try {
            // Delete user's plans
            val plansSnapshot = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLANS_COLLECTION)
                .get()
                .await()

            plansSnapshot.documents.forEach { it.reference.delete() }

            // Delete user's liked destinations
            val likedSnapshot = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(LIKED_DESTINATIONS_COLLECTION)
                .get()
                .await()

            likedSnapshot.documents.forEach { it.reference.delete() }

            // Delete user document
            db.collection(USERS_COLLECTION)
                .document(userId)
                .delete()
                .await()

            Log.d(TAG, "User data deleted: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete user data", e)
            Result.failure(e)
        }
    }

    // ==================== Travel Plans Management ====================

    /**
     * Save a travel plan to Firestore
     */
    suspend fun savePlan(userId: String, plan: Plan): Result<String> {
        return try {
            val planData = FirebasePlan(
                id = plan.id,
                userId = userId,
                title = plan.title,
                from = "", // You'll need to add these fields to Plan model
                to = "",
                destinationId = plan.destinationId,
                startDate = "",
                nights = 0,
                budget = 0,
                people = 1,
                markdownItinerary = plan.markdownItinerary,
                isLiked = false
            )

            val docRef = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLANS_COLLECTION)
                .document(plan.id)

            docRef.set(planData).await()

            Log.d(TAG, "Plan saved: ${plan.id}")
            Result.success(plan.id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save plan", e)
            Result.failure(e)
        }
    }

    /**
     * Get all plans for a user
     */
    suspend fun getUserPlans(userId: String): Result<List<Plan>> {
        return try {
            val snapshot = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLANS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val plans = snapshot.documents.mapNotNull { doc ->
                val fbPlan = doc.toObject(FirebasePlan::class.java)
                fbPlan?.let {
                    Plan(
                        id = it.id,
                        title = it.title,
                        markdownItinerary = it.markdownItinerary,
                        destinationId = it.destinationId
                    )
                }
            }

            Result.success(plans)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user plans", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific plan by ID
     */
    suspend fun getPlan(userId: String, planId: String): Result<Plan?> {
        return try {
            val snapshot = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLANS_COLLECTION)
                .document(planId)
                .get()
                .await()

            val fbPlan = snapshot.toObject(FirebasePlan::class.java)
            val plan = fbPlan?.let {
                Plan(
                    id = it.id,
                    title = it.title,
                    markdownItinerary = it.markdownItinerary,
                    destinationId = it.destinationId
                )
            }

            Result.success(plan)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get plan", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a plan
     */
    suspend fun deletePlan(userId: String, planId: String): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLANS_COLLECTION)
                .document(planId)
                .delete()
                .await()

            Log.d(TAG, "Plan deleted: $planId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete plan", e)
            Result.failure(e)
        }
    }

    /**
     * Toggle like status of a plan
     */
    suspend fun togglePlanLike(userId: String, planId: String, isLiked: Boolean): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLANS_COLLECTION)
                .document(planId)
                .update("isLiked", isLiked)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle plan like", e)
            Result.failure(e)
        }
    }

    /**
     * Get liked plans as a Flow (real-time updates)
     */
    fun getLikedPlansFlow(userId: String): Flow<List<Plan>> = callbackFlow {
        val listener = db.collection(USERS_COLLECTION)
            .document(userId)
            .collection(PLANS_COLLECTION)
            .whereEqualTo("isLiked", true)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Liked plans listener error", error)
                    return@addSnapshotListener
                }

                val plans = snapshot?.documents?.mapNotNull { doc ->
                    val fbPlan = doc.toObject(FirebasePlan::class.java)
                    fbPlan?.let {
                        Plan(
                            id = it.id,
                            title = it.title,
                            markdownItinerary = it.markdownItinerary,
                            destinationId = it.destinationId
                        )
                    }
                } ?: emptyList()

                trySend(plans)
            }

        awaitClose { listener.remove() }
    }

    // ==================== Liked Destinations Management ====================

    /**
     * Like a destination
     */
    suspend fun likeDestination(userId: String, destinationId: String): Result<Unit> {
        return try {
            val likedData = LikedDestination(destinationId = destinationId)

            db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(LIKED_DESTINATIONS_COLLECTION)
                .document(destinationId)
                .set(likedData)
                .await()

            Log.d(TAG, "Destination liked: $destinationId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to like destination", e)
            Result.failure(e)
        }
    }

    /**
     * Unlike a destination
     */
    suspend fun unlikeDestination(userId: String, destinationId: String): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(LIKED_DESTINATIONS_COLLECTION)
                .document(destinationId)
                .delete()
                .await()

            Log.d(TAG, "Destination unliked: $destinationId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unlike destination", e)
            Result.failure(e)
        }
    }

    /**
     * Check if destination is liked
     */
    suspend fun isDestinationLiked(userId: String, destinationId: String): Result<Boolean> {
        return try {
            val snapshot = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(LIKED_DESTINATIONS_COLLECTION)
                .document(destinationId)
                .get()
                .await()

            Result.success(snapshot.exists())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check destination like status", e)
            Result.failure(e)
        }
    }

    /**
     * Get liked destination IDs as Flow (real-time updates)
     */
    fun getLikedDestinationsFlow(userId: String): Flow<Set<String>> = callbackFlow {
        val listener = db.collection(USERS_COLLECTION)
            .document(userId)
            .collection(LIKED_DESTINATIONS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Liked destinations listener error", error)
                    return@addSnapshotListener
                }

                val likedIds = snapshot?.documents?.map { it.id }?.toSet() ?: emptySet()
                trySend(likedIds)
            }

        awaitClose { listener.remove() }
    }
}