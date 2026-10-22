package com.example.data

import android.net.Uri
import com.example.model.BoxTier
import com.example.model.ShopCategory
import com.example.model.StorefrontCopy
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

object StorefrontRepository {
  private const val DATABASE_ID = "tienda"

  private fun database() = FirebaseFirestore.getInstance(DATABASE_ID)

  fun observeCategories(): Flow<List<ShopCategory>> = callbackFlow {
    val registration = database().collection("categories").addSnapshotListener { snapshot, error ->
      if (error != null || snapshot == null) {
        return@addSnapshotListener
      }
      val items = snapshot.documents.mapNotNull { it.toCategory() }.sortedBy { it.sort }
      trySend(items)
    }
    awaitClose { registration.remove() }
  }

  fun observeCopy(): Flow<StorefrontCopy> = callbackFlow {
    val registration = database().collection("settings").document("storefront")
      .addSnapshotListener { snapshot, error ->
        if (error != null || snapshot == null || !snapshot.exists()) {
          return@addSnapshotListener
        }
        trySend(snapshot.toCopy())
      }
    awaitClose { registration.remove() }
  }

  suspend fun saveCategory(category: ShopCategory) {
    database().collection("categories").document(category.id).set(
      mapOf(
        "name" to category.name,
        "imageUrl" to category.imageUrl,
        "sort" to category.sort
      )
    ).await()
  }

  suspend fun deleteCategory(categoryId: String) {
    database().collection("categories").document(categoryId).delete().await()
  }

  fun observeBoxTiers(): Flow<List<BoxTier>> = callbackFlow {
    val registration = database().collection("settings").document("box_tiers")
      .addSnapshotListener { snapshot, error ->
        if (error != null || snapshot == null || !snapshot.exists()) {
          trySend(SanMiguelData.boxTiers)
          return@addSnapshotListener
        }
        val rawTiers = snapshot.get("tiers") as? List<*>
        val tiers = rawTiers?.mapNotNull { item ->
          val map = item as? Map<*, *> ?: return@mapNotNull null
          val size = (map["size"] as? Number)?.toInt() ?: return@mapNotNull null
          val name = map["name"] as? String ?: return@mapNotNull null
          val subtitle = map["subtitle"] as? String ?: "$size Piezas"
          val price = (map["price"] as? Number)?.toDouble() ?: 0.0
          val isFavorite = map["isFavorite"] as? Boolean ?: false
          BoxTier(size, name, subtitle, price, isFavorite)
        }?.sortedBy { it.size } ?: SanMiguelData.boxTiers

        trySend(if (tiers.isNotEmpty()) tiers else SanMiguelData.boxTiers)
      }
    awaitClose { registration.remove() }
  }

  suspend fun saveBoxTiers(tiers: List<BoxTier>) {
    database().collection("settings").document("box_tiers").set(
      mapOf(
        "tiers" to tiers.map { tier ->
          mapOf(
            "size" to tier.size,
            "name" to tier.name,
            "subtitle" to tier.subtitle,
            "price" to tier.price,
            "isFavorite" to tier.isFavorite
          )
        }
      )
    ).await()
  }

  suspend fun saveCopy(copy: StorefrontCopy) {
    database().collection("settings").document("storefront").set(copy.toDocument()).await()
  }

  suspend fun uploadImage(folder: String, uri: Uri): String {
    val fileName = "${UUID.randomUUID()}.jpg"
    val reference = FirebaseStorage.getInstance().reference.child("$folder/$fileName")
    reference.putFile(uri).await()
    return reference.downloadUrl.await().toString()
  }
}

private fun DocumentSnapshot.toCategory(): ShopCategory? {
  val name = getString("name")?.trim().orEmpty()
  if (name.isEmpty()) return null
  val sortValue = get("sort")
  return ShopCategory(
    id = id,
    name = name,
    imageUrl = getString("imageUrl").orEmpty(),
    sort = if (sortValue is Number) sortValue.toInt() else 0
  )
}

private fun DocumentSnapshot.toCopy(): StorefrontCopy {
  val defaults = StorefrontCopy()
  return defaults.copy(
    eyebrow = string("eyebrow", defaults.eyebrow),
    headline = string("headline", defaults.headline),
    cornerBadge = string("cornerBadge", defaults.cornerBadge),
    heroBadge = string("heroBadge", defaults.heroBadge),
    heroSeason = string("heroSeason", defaults.heroSeason),
    heroTitle = string("heroTitle", defaults.heroTitle),
    heroBody = string("heroBody", defaults.heroBody),
    heroPriceCaption = string("heroPriceCaption", defaults.heroPriceCaption),
    heroPrice = string("heroPrice", defaults.heroPrice),
    heroButton = string("heroButton", defaults.heroButton),
    heroImageUrl = string("heroImageUrl", defaults.heroImageUrl),
    categoriesHeading = string("categoriesHeading", defaults.categoriesHeading),
    productsHeading = string("productsHeading", defaults.productsHeading),
    productsNote = string("productsNote", defaults.productsNote),
    deliveryTitle = string("deliveryTitle", defaults.deliveryTitle),
    deliveryDetail = string("deliveryDetail", defaults.deliveryDetail),
    giftTitle = string("giftTitle", defaults.giftTitle),
    giftDetail = string("giftDetail", defaults.giftDetail),
    profileTagline = string("profileTagline", defaults.profileTagline),
    boxKicker = string("boxKicker", defaults.boxKicker),
    boxTitle = string("boxTitle", defaults.boxTitle),
    boxBody = string("boxBody", defaults.boxBody),
    boxStep1 = string("boxStep1", defaults.boxStep1),
    boxStep2 = string("boxStep2", defaults.boxStep2),
    boxStep3 = string("boxStep3", defaults.boxStep3),
    boxButton = string("boxButton", defaults.boxButton),
    quote = string("quote", defaults.quote),
    quoteName = string("quoteName", defaults.quoteName),
    quoteRole = string("quoteRole", defaults.quoteRole),
    sealTitle = string("sealTitle", defaults.sealTitle),
    sealBody = string("sealBody", defaults.sealBody),
    sealButton = string("sealButton", defaults.sealButton),
    footerLine = string("footerLine", defaults.footerLine),
    trustCraft = string("trustCraft", defaults.trustCraft),
    trustCold = string("trustCold", defaults.trustCold),
    trustPay = string("trustPay", defaults.trustPay),
    tierPetitPrice = double("tierPetitPrice", defaults.tierPetitPrice),
    tierClasicoPrice = double("tierClasicoPrice", defaults.tierClasicoPrice),
    tierGranReservaPrice = double("tierGranReservaPrice", defaults.tierGranReservaPrice),
    storeAddress = string("storeAddress", defaults.storeAddress),
    storeHours = string("storeHours", defaults.storeHours),
    storePhone = string("storePhone", defaults.storePhone)
  )
}

private fun DocumentSnapshot.string(key: String, fallback: String): String {
  return if (contains(key)) getString(key).orEmpty() else fallback
}

private fun DocumentSnapshot.double(key: String, fallback: Double): Double {
  val v = get(key)
  return if (v is Number) v.toDouble() else fallback
}

private fun StorefrontCopy.toDocument(): Map<String, Any> {
  return mapOf(
    "eyebrow" to eyebrow,
    "headline" to headline,
    "cornerBadge" to cornerBadge,
    "heroBadge" to heroBadge,
    "heroSeason" to heroSeason,
    "heroTitle" to heroTitle,
    "heroBody" to heroBody,
    "heroPriceCaption" to heroPriceCaption,
    "heroPrice" to heroPrice,
    "heroButton" to heroButton,
    "heroImageUrl" to heroImageUrl,
    "categoriesHeading" to categoriesHeading,
    "productsHeading" to productsHeading,
    "productsNote" to productsNote,
    "deliveryTitle" to deliveryTitle,
    "deliveryDetail" to deliveryDetail,
    "giftTitle" to giftTitle,
    "giftDetail" to giftDetail,
    "profileTagline" to profileTagline,
    "boxKicker" to boxKicker,
    "boxTitle" to boxTitle,
    "boxBody" to boxBody,
    "boxStep1" to boxStep1,
    "boxStep2" to boxStep2,
    "boxStep3" to boxStep3,
    "boxButton" to boxButton,
    "quote" to quote,
    "quoteName" to quoteName,
    "quoteRole" to quoteRole,
    "sealTitle" to sealTitle,
    "sealBody" to sealBody,
    "sealButton" to sealButton,
    "footerLine" to footerLine,
    "trustCraft" to trustCraft,
    "trustCold" to trustCold,
    "trustPay" to trustPay,
    "tierPetitPrice" to tierPetitPrice,
    "tierClasicoPrice" to tierClasicoPrice,
    "tierGranReservaPrice" to tierGranReservaPrice,
    "storeAddress" to storeAddress,
    "storeHours" to storeHours,
    "storePhone" to storePhone
  )
}
