package com.example.data

import com.example.viewmodel.ShopUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object OrderRepository {
  private const val DATABASE_ID = "tienda"

  suspend fun place(state: ShopUiState, chargeId: String): String {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null || user.isAnonymous) {
      error("Entra a tu cuenta para hacer el pedido")
    }
    val uid = user.uid
    val items = state.cartItems.map { item ->
      mapOf(
        "title" to item.title,
        "subtitle" to item.subtitle,
        "unitPrice" to item.unitPrice,
        "quantity" to item.quantity,
        "customFlavors" to item.customFlavors.map { flavor ->
          mapOf("name" to flavor.first, "count" to flavor.second)
        }
      )
    }
    val order = hashMapOf(
      "uid" to uid,
      "createdAt" to FieldValue.serverTimestamp(),
      "status" to "recibido",
      "paymentMethod" to state.paymentMethod.name,
      "deliveryMode" to state.deliveryMode.name,
      "timeSlot" to state.selectedTimeSlot,
      "giftWrapEnabled" to state.giftWrapEnabled,
      "giftCardMessage" to state.giftCardMessage.take(500),
      "currency" to "PEN",
      "items" to items,
      "subtotal" to state.cartSubtotal,
      "giftWrapPrice" to state.giftWrapPrice,
      "total" to state.cartTotal,
      "itemCount" to state.cartItems.sumOf { it.quantity },
      "culqiChargeId" to chargeId,
      "invoiceKind" to "",
      "invoiceSeries" to "",
      "invoiceNumber" to ""
    )
    val document = FirebaseFirestore.getInstance(DATABASE_ID).collection("orders").document()
    document.set(order).await()
    return document.id
  }

  suspend fun attachInvoice(orderId: String, kind: String, series: String, number: String) {
    FirebaseFirestore.getInstance(DATABASE_ID).collection("orders").document(orderId)
      .update(
        mapOf(
          "invoiceKind" to kind,
          "invoiceSeries" to series,
          "invoiceNumber" to number
        )
      )
      .await()
  }
}
