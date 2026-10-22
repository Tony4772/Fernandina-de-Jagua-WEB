package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GoogleSignIn
import com.example.data.SanMiguelData
import kotlinx.coroutines.launch
import com.example.model.Product
import com.example.ui.components.SanMiguelImage
import com.example.ui.theme.ChocolatePrimaryContainer
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.CreamSurfaceContainer
import com.example.ui.theme.CreamSurfaceContainerHigh
import com.example.ui.theme.CreamSurfaceContainerLow
import com.example.ui.theme.CreamSurfaceContainerLowest
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryContainer
import com.example.ui.theme.OnGoldSecondaryContainer
import com.example.ui.theme.OnSurfaceDark
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineGrey
import com.example.ui.theme.SecondaryFixed
import com.example.viewmodel.ShopUiState

@Composable
fun ProfileScreen(
  uiState: ShopUiState,
  onProductClick: (Product) -> Unit,
  onNavigateToBuilder: () -> Unit,
  onRegister: (name: String, email: String, password: String) -> Unit,
  onSignIn: (email: String, password: String) -> Unit,
  onGoogleSignIn: (idToken: String) -> Unit,
  onAuthMessage: (String) -> Unit,
  onSignOut: () -> Unit,
  onOpenShop: () -> Unit,
  onDeleteAccount: () -> Unit
) {
  val scrollState = rememberScrollState()
  var showDeleteDialog by remember { mutableStateOf(false) }

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(CreamBackground)
  ) {
    val contentMaxWidth = if (maxWidth >= 720.dp) 740.dp else maxWidth

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
        .padding(bottom = 120.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = contentMaxWidth)
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
    // User Profile Header Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("profile_card"),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = ChocolatePrimaryContainer),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(GoldSecondaryContainer)
            .border(2.dp, GoldSecondaryContainer, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = (uiState.accountName ?: "F").take(1).uppercase(),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = OnGoldSecondaryContainer
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.WorkspacePremium,
              contentDescription = null,
              tint = GoldSecondaryContainer,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "FERNANDINA DE JAGUA",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              ),
              color = GoldSecondaryContainer
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = uiState.accountName ?: "Tu cuenta",
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = CreamSurface
          )

          Text(
            text = uiState.accountEmail ?: "Entra para guardar favoritos y pedidos",
            style = MaterialTheme.typography.bodySmall,
            color = CreamSurfaceContainerHigh
          )
        }
      }
    }

    if (uiState.accountEmail == null) {
      Spacer(modifier = Modifier.height(14.dp))
      AccountForm(
        busy = uiState.accountBusy,
        onRegister = onRegister,
        onSignIn = onSignIn,
        onGoogleSignIn = onGoogleSignIn,
        onAuthMessage = onAuthMessage
      )
    } else {
      Spacer(modifier = Modifier.height(14.dp))
      OutlinedButton(
        onClick = onSignOut,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("sign_out_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Cerrar sesión")
      }
      Spacer(modifier = Modifier.height(8.dp))
      TextButton(
        onClick = { showDeleteDialog = true },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Eliminar mi cuenta y datos", color = ErrorRed, fontWeight = FontWeight.Bold)
      }
    }

    if (showDeleteDialog) {
      androidx.compose.material3.AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        title = { Text("Eliminar cuenta y datos") },
        text = { Text("¿Estás seguro de que deseas eliminar tu cuenta de Fernandina de Jagua Chocolates y todos tus datos asociados? Esta acción es permanente.") },
        confirmButton = {
          Button(
            onClick = {
              showDeleteDialog = false
              onDeleteAccount()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
          ) {
            Text("Eliminar", color = CreamSurfaceContainerLowest)
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteDialog = false }) {
            Text("Cancelar")
          }
        }
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Favoritos Guardados
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = null,
          tint = ErrorRed,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Mis Joyas Favoritas",
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = ChocolatePrimaryContainer
        )
      }
      Text(
        text = "${uiState.favorites.size} guardados",
        style = MaterialTheme.typography.labelSmall,
        color = OnSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    val favoriteProducts = uiState.catalog.filter { uiState.favorites.contains(it.id) }
    if (favoriteProducts.isEmpty()) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = if (uiState.accountEmail == null) {
              "Entra a tu cuenta para guardar favoritos"
            } else {
              "No tienes favoritos guardados aún"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
          )
        }
      }
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        favoriteProducts.forEach { product ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onProductClick(product) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              SanMiguelImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(50.dp)
                  .clip(RoundedCornerShape(8.dp))
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = product.name,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = ChocolatePrimaryContainer
                )
                Text(
                  text = product.subtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = OnSurfaceVariant
                )
              }
              Text(
                text = "S/ ${String.format(java.util.Locale.US, "%.2f", product.price)}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = GoldSecondary
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    if (uiState.isShopOwner) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ChocolatePrimaryContainer),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldSecondaryContainer)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(GoldSecondaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "👑",
                fontSize = 20.sp
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Panel de Administración",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold
                ),
                color = CreamSurface
              )
              Text(
                text = "Modo Propietario · Don Antonio",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = GoldSecondaryContainer
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Tienes control total de Fernandina Chocolates. Desde tu panel puedes gestionar productos, subir fotos de alta calidad, cambiar precios, personalizar textos de entrega y verificar el catálogo para tus clientes.",
            style = MaterialTheme.typography.bodySmall,
            color = CreamSurfaceContainerHigh,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = onOpenShop,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GoldSecondaryContainer),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = "Abrir Mi Panel de Tienda",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = OnGoldSecondaryContainer
            )
          }
        }
      }
    }

    Text(
      text = "PREFERENCIAS DE DEGUSTACIÓN",
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      ),
      color = OnSurfaceVariant
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest)
    ) {
      Column {
        PreferenceRow(
          icon = Icons.Default.Place,
          title = "Chocolatería y Tienda",
          subtitle = uiState.storefront.storeAddress
        )
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CreamSurfaceContainerHigh))
        PreferenceRow(
          icon = Icons.Default.Schedule,
          title = "Horario de Atención",
          subtitle = uiState.storefront.storeHours
        )
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CreamSurfaceContainerHigh))
        PreferenceRow(
          icon = Icons.Default.Phone,
          title = "Teléfono y Pedidos",
          subtitle = uiState.storefront.storePhone
        )
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CreamSurfaceContainerHigh))
        PreferenceRow(
          icon = Icons.Default.LocalShipping,
          title = uiState.storefront.deliveryTitle,
          subtitle = uiState.storefront.deliveryDetail
        )
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CreamSurfaceContainerHigh))
        PreferenceRow(
          icon = Icons.Default.CardGiftcard,
          title = uiState.storefront.giftTitle,
          subtitle = uiState.storefront.giftDetail
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Atelier heritage seal
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Fernandina de Jagua Chocolates · ${uiState.storefront.storeAddress}",
        style = MaterialTheme.typography.labelSmall,
        color = OutlineGrey
      )
      if (uiState.storefront.profileTagline.isNotBlank()) {
        Text(
          text = uiState.storefront.profileTagline,
          style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
          color = GoldSecondary
        )
      }
    }
    }
  }
}
}

@Composable
private fun PreferenceRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .background(CreamSurfaceContainer, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(18.dp))
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = OnSurfaceDark
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = OnSurfaceVariant
      )
    }
    Icon(
      imageVector = Icons.Default.ChevronRight,
      contentDescription = null,
      tint = OutlineGrey,
      modifier = Modifier.size(18.dp)
    )
  }
}

@Composable
private fun AccountForm(
  busy: Boolean,
  onRegister: (name: String, email: String, password: String) -> Unit,
  onSignIn: (email: String, password: String) -> Unit,
  onGoogleSignIn: (idToken: String) -> Unit,
  onAuthMessage: (String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var creating by remember { mutableStateOf(false) } // Default to Sign In for easier owner/client access
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CreamSurfaceContainerHigh)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Selector de modo: Iniciar Sesión vs Crear Cuenta
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(CreamSurfaceContainer)
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(9.dp))
            .background(if (!creating) ChocolatePrimaryContainer else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(enabled = !busy) { creating = false }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = if (!creating) CreamSurface else OnSurfaceVariant
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(9.dp))
            .background(if (creating) ChocolatePrimaryContainer else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(enabled = !busy) { creating = true }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = if (creating) CreamSurface else OnSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = if (creating) "Crea tu cuenta de cliente" else "Bienvenido a Fernandina Chocolates",
        style = MaterialTheme.typography.titleMedium.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold
        ),
        color = ChocolatePrimaryContainer
      )
      Text(
        text = if (creating) {
          "Regístrate para guardar tus piezas favoritas y hacer seguimiento de tus pedidos."
        } else {
          "Ingresa con tu correo registrado para acceder a tu historial o panel de administración."
        },
        style = MaterialTheme.typography.bodySmall,
        color = OnSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
      )

      OutlinedButton(
        onClick = {
          scope.launch {
            try {
              onGoogleSignIn(GoogleSignIn.requestIdToken(context))
            } catch (error: Exception) {
              val message = GoogleSignIn.messageFor(error) ?: return@launch
              onAuthMessage(message)
            }
          }
        },
        enabled = !busy,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("Continuar con Google")
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (creating) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          label = { Text("Nombre y Apellido") },
          enabled = !busy,
          shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
      }

      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Correo electrónico") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        enabled = !busy,
        shape = RoundedCornerShape(10.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Contraseña (mínimo 6 caracteres)") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        enabled = !busy,
        shape = RoundedCornerShape(10.dp)
      )

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = {
          if (creating) onRegister(name, email, password) else onSignIn(email, password)
        },
        enabled = !busy,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
        shape = RoundedCornerShape(24.dp)
      ) {
        Text(
          text = if (busy) "Un momento..." else if (creating) "Crear mi Cuenta" else "Entrar a mi Cuenta",
          fontWeight = FontWeight.Bold,
          color = CreamSurface
        )
      }

      if (!creating) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "👑 ¿Eres el dueño de la tienda? Ingresa aquí con tu correo para acceder al panel.",
          style = MaterialTheme.typography.labelSmall,
          color = GoldSecondary,
          modifier = Modifier.align(Alignment.CenterHorizontally)
        )
      }
    }
  }
}
