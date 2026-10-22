package com.example.data

import android.net.Uri
import com.example.model.Product
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

object CatalogRepository {
  private const val DATABASE_ID = "tienda"

  private fun products() = FirebaseFirestore.getInstance(DATABASE_ID).collection("products")

  fun observe(includeHidden: Boolean): Flow<List<Product>> = callbackFlow {
    val query = if (includeHidden) {
      products()
    } else {
      products().whereEqualTo("visible", true)
    }
    val registration: ListenerRegistration = query.addSnapshotListener { snapshot, error ->
      if (error != null || snapshot == null) {
        return@addSnapshotListener
      }
      val items = snapshot.documents.mapNotNull { document -> document.toProduct() }
      trySend(items)
    }
    awaitClose { registration.remove() }
  }

  suspend fun save(product: Product) {
    products().document(product.id).set(product.toDocument()).await()
  }

  suspend fun delete(productId: String) {
    products().document(productId).delete().await()
  }

  suspend fun uploadImage(productId: String, uri: Uri): String {
    val fileName = "${UUID.randomUUID()}.jpg"
    val reference = FirebaseStorage.getInstance().reference.child("products/$productId/$fileName")
    reference.putFile(uri).await()
    return reference.downloadUrl.await().toString()
  }
}

private fun DocumentSnapshot.toProduct(): Product? {
  val name = getString("name") ?: return null
  val priceValue = get("price")
  val price = if (priceValue is Number) priceValue.toDouble() else 0.0
  val badge = getString("badge").orEmpty()
  return Product(
    id = id,
    name = name,
    subtitle = getString("subtitle").orEmpty(),
    price = price,
    imageUrl = getString("imageUrl").orEmpty(),
    badge = badge.ifBlank { null },
    category = getString("category").orEmpty().ifBlank { "Bombones" },
    origin = getString("origin").orEmpty(),
    description = getString("description").orEmpty(),
    visible = getBoolean("visible") ?: true
  )
}

private fun Product.toDocument(): Map<String, Any> {
  return mapOf(
    "name" to name,
    "subtitle" to subtitle,
    "price" to price,
    "imageUrl" to imageUrl,
    "description" to description,
    "category" to category,
    "origin" to origin,
    "badge" to badge.orEmpty(),
    "visible" to visible
  )
}
