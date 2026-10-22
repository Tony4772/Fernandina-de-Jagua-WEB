package com.example.data

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object CulqiPayments {
  private const val PUBLIC_KEY = "pk_live_U6Rxa1sMaLcZKpcA"
  private const val REGION = "us-central1"

  private val http = OkHttpClient.Builder()
    .callTimeout(40, TimeUnit.SECONDS)
    .build()

  private val jsonType = "application/json; charset=utf-8".toMediaType()

  suspend fun chargeCard(
    number: String,
    month: String,
    year: String,
    cvv: String,
    email: String,
    amountCents: Int
  ): String {
    val token = postToken(
      url = "https://secure.culqi.com/v2/tokens",
      payload = JSONObject()
        .put("card_number", number)
        .put("cvv", cvv)
        .put("expiration_month", month)
        .put("expiration_year", year)
        .put("email", email)
    )
    return charge(token, email, amountCents)
  }

  suspend fun chargeYape(
    phone: String,
    otp: String,
    email: String,
    amountCents: Int
  ): String {
    val token = postToken(
      url = "https://secure.culqi.com/v2/tokens/yape",
      payload = JSONObject()
        .put("number_phone", phone)
        .put("otp", otp)
        .put("amount", amountCents.toString())
    )
    return charge(token, email, amountCents)
  }

  private suspend fun postToken(url: String, payload: JSONObject): String = withContext(Dispatchers.IO) {
    val request = Request.Builder()
      .url(url)
      .addHeader("Authorization", "Bearer $PUBLIC_KEY")
      .addHeader("Content-Type", "application/json")
      .post(payload.toString().toRequestBody(jsonType))
      .build()
    http.newCall(request).execute().use { response ->
      val body = response.body?.string().orEmpty()
      val json = JSONObject(body.ifBlank { "{}" })
      val id = json.optString("id")
      if (!response.isSuccessful || id.isBlank()) {
        val message = json.optString("user_message").ifBlank {
          json.optString("merchant_message")
        }.ifBlank { "No se pudo preparar el pago." }
        error(message)
      }
      id
    }
  }

  private suspend fun charge(sourceId: String, email: String, amountCents: Int): String {
    return try {
      val result = FirebaseFunctions.getInstance(REGION)
        .getHttpsCallable("createCulqiCharge")
        .call(
          mapOf(
            "sourceId" to sourceId,
            "email" to email,
            "amount" to amountCents,
            "description" to "Pedido Fernandina de Jagua"
          )
        )
        .await()
      val data = result.getData() as? Map<*, *>
      val chargeId = data?.get("chargeId") as? String
      val charged = (data?.get("amount") as? Number)?.toInt()
      if (chargeId.isNullOrBlank() || charged != amountCents) {
        error("El cobro no coincide con el total del pedido.")
      }
      chargeId
    } catch (error: FirebaseFunctionsException) {
      error(error.message ?: "Culqi no aprobó el pago.")
    }
  }
}
