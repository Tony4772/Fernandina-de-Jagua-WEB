package com.example.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AccountRepository {
  private const val DATABASE_ID = "tienda"

  private fun users() = FirebaseFirestore.getInstance(DATABASE_ID).collection("users")

  suspend fun register(name: String, email: String, password: String) {
    val auth = FirebaseAuth.getInstance()
    if (auth.currentUser?.isAnonymous == true) {
      auth.signOut()
    }
    val result = auth.createUserWithEmailAndPassword(email, password).await()
    val user = result.user ?: error("No se pudo crear la cuenta")
    val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
    user.updateProfile(profile).await()
    saveProfile(user.uid, name, user.email ?: email, emptySet())
  }

  suspend fun signInWithGoogle(idToken: String) {
    val auth = FirebaseAuth.getInstance()
    if (auth.currentUser?.isAnonymous == true) {
      auth.signOut()
    }
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential).await()
  }

  suspend fun signIn(email: String, password: String) {
    val auth = FirebaseAuth.getInstance()
    if (auth.currentUser?.isAnonymous == true) {
      auth.signOut()
    }
    auth.signInWithEmailAndPassword(email, password).await()
  }

  fun signOut() {
    FirebaseAuth.getInstance().signOut()
  }

  suspend fun load(uid: String): LoadedAccount? {
    val snapshot = users().document(uid).get().await()
    if (!snapshot.exists()) {
      return null
    }
    val name = snapshot.getString("displayName").orEmpty()
    val favorites = (snapshot.get("favoriteIds") as? List<*>)
      ?.mapNotNull { it as? String }
      ?.toSet()
      ?: emptySet()
    return LoadedAccount(name, favorites)
  }

  suspend fun saveProfile(uid: String, name: String, email: String, favorites: Set<String>) {
    users().document(uid).set(
      mapOf(
        "displayName" to name,
        "email" to email,
        "favoriteIds" to favorites.toList(),
        "createdAt" to FieldValue.serverTimestamp()
      )
    ).await()
  }

  suspend fun setFavorites(uid: String, favorites: Set<String>) {
    users().document(uid).update("favoriteIds", favorites.toList()).await()
  }

  suspend fun deleteAccount() {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser ?: return
    val uid = user.uid
    try {
      users().document(uid).delete().await()
    } catch (_: Exception) {}
    user.delete().await()
    auth.signOut()
  }
}

data class LoadedAccount(
  val displayName: String,
  val favorites: Set<String>
)
