package com.example.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

object GoogleSignIn {
  suspend fun requestIdToken(context: Context): String {
    val credentialManager = CredentialManager.create(context)
    val googleIdOption = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(false)
      .setServerClientId(context.getString(R.string.default_web_client_id))
      .setAutoSelectEnabled(false)
      .build()
    val request = GetCredentialRequest.Builder()
      .addCredentialOption(googleIdOption)
      .build()
    val result = credentialManager.getCredential(context, request)
    val credential = result.credential
    if (credential is CustomCredential &&
      credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
      return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }
    error("No se pudo entrar con Google")
  }

  fun messageFor(error: Exception): String? {
    return when (error) {
      is GetCredentialCancellationException -> null
      is NoCredentialException -> "No hay una cuenta de Google en este dispositivo"
      else -> "No se pudo entrar con Google"
    }
  }
}
