package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CartItem
import com.example.model.CardCheckout
import com.example.model.DeliveryMode
import com.example.model.PaymentMethod
import com.example.model.YapeCheckout
import com.example.ui.components.SanMiguelImage
import com.example.ui.theme.YapePurple
import com.example.ui.theme.ChocolatePrimaryContainer
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.CreamSurfaceContainer
import com.example.ui.theme.CreamSurfaceContainerHigh
import com.example.ui.theme.CreamSurfaceContainerLow
import com.example.ui.theme.CreamSurfaceContainerLowest
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryContainer
import com.example.ui.theme.OnGoldSecondaryContainer
import com.example.ui.theme.OnSecondaryFixed
import com.example.ui.theme.OnSurfaceDark
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineGrey
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.SecondaryFixedDim
import com.example.viewmodel.ShopUiState

@Composable
fun CartScreen(
  uiState: ShopUiState,
  onUpdateQuantity: (String, Int) -> Unit,
  onRemoveItem: (String) -> Unit,
  onSelectDeliveryMode: (DeliveryMode) -> Unit,
  onSelectTimeSlot: (String) -> Unit,
  onToggleGiftWrap: (Boolean) -> Unit,
  onUpdateGiftMessage: (String) -> Unit,
  onSelectPayment: (PaymentMethod) -> Unit,
  onCheckout: (CardCheckout?, YapeCheckout?) -> Unit,
  onNavigateToBuilder: () -> Unit
) {
  val scrollState = rememberScrollState()
  var cardNumber by remember { mutableStateOf("") }
  var cardMonth by remember { mutableStateOf("") }
  var cardYear by remember { mutableStateOf("") }
  var cardCvv by remember { mutableStateOf("") }
  var yapePhone by remember { mutableStateOf("") }
  var yapeOtp by remember { mutableStateOf("") }

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
    // 1. Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Bottom
    ) {
      Text(
        text = "Tu carrito",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold,
          fontSize = 26.sp
        ),
        color = ChocolatePrimaryContainer
      )
      Text(
        text = "${uiState.cartItems.sumOf { it.quantity }} productos",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = OnSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Aquí está lo que elegiste en Inicio. Revísalo y, si quieres, paga más abajo.",
      style = MaterialTheme.typography.bodyMedium,
      color = OnSurfaceVariant
    )

    Spacer(modifier = Modifier.height(14.dp))

    if (uiState.cartItems.isNotEmpty()) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = SecondaryFixed.copy(alpha = 0.5f))
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.LocalShipping,
            contentDescription = null,
            tint = GoldSecondary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          if (uiState.remainingForFreeShipping > 0.0) {
            Text(
              text = "¡Añade S/ ${String.format(java.util.Locale.US, "%.2f", uiState.remainingForFreeShipping)} más para Envío Gratis a Domicilio!",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = OnSecondaryFixed
            )
          } else {
            Text(
              text = "¡Has alcanzado el Envío Gratis a Domicilio!",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = OnSecondaryFixed
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
            .background(CreamSurfaceContainerHigh)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(uiState.freeShippingProgress)
              .height(8.dp)
              .clip(CircleShape)
              .background(GoldSecondary)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "S/ ${String.format(java.util.Locale.US, "%.2f", uiState.cartSubtotal)} actual",
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
          )
          Text(
            text = "Meta: S/ ${String.format(java.util.Locale.US, "%.2f", uiState.freeShippingGoal)}",
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
          )
        }
      }
    }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 3. Items Cart List
    if (uiState.cartItems.isEmpty()) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Tu carrito está vacío",
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
            color = ChocolatePrimaryContainer
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Ve a Inicio, elige un producto y agrégalo.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = onNavigateToBuilder,
            colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
            shape = RoundedCornerShape(20.dp)
          ) {
            Text("Ir a Inicio", color = CreamSurface)
          }
        }
      }
    } else {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        uiState.cartItems.forEach { item ->
          CartItemCard(
            item = item,
            onMinus = { onUpdateQuantity(item.id, -1) },
            onPlus = { onUpdateQuantity(item.id, 1) },
            onDelete = { onRemoveItem(item.id) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 4. Modalidad de Entrega Fina
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Modalidad de Entrega",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        ),
        color = ChocolatePrimaryContainer
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = null,
          tint = GoldSecondary,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = "EMPAQUE SEGURO",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = GoldSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Option A: Home delivery
      DeliveryOptionCard(
        title = "Envío a Domicilio",
        subtitle = "Empaque cuidadoso",
        highlight = "Lima Metropolitana",
        icon = Icons.Default.LocalShipping,
        isSelected = uiState.deliveryMode == DeliveryMode.REFRIGERATED,
        modifier = Modifier.weight(1f),
        onClick = { onSelectDeliveryMode(DeliveryMode.REFRIGERATED) }
      )

      // Option B: Store pickup
      DeliveryOptionCard(
        title = "Recojo en Tienda",
        subtitle = uiState.storefront.storeAddress,
        highlight = "Gratis · San Miguel",
        icon = Icons.Default.Storefront,
        isSelected = uiState.deliveryMode == DeliveryMode.PICKUP_SERRANO,
        modifier = Modifier.weight(1f),
        onClick = { onSelectDeliveryMode(DeliveryMode.PICKUP_SERRANO) }
      )
    }

    if (uiState.deliveryMode == DeliveryMode.PICKUP_SERRANO) {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CreamSurfaceContainerLow)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Schedule,
          contentDescription = null,
          tint = GoldSecondary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Horario de retiro: ${uiState.storefront.storeHours}",
          style = MaterialTheme.typography.labelSmall,
          color = ChocolatePrimaryContainer
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Schedule Date + Time picker
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLow)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = GoldSecondary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Programar Fecha y Hora de Entrega",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        val context = LocalContext.current
        Button(
          onClick = {
            val now = Calendar.getInstance()
            DatePickerDialog(
              context,
              { _, year, month, day ->
                TimePickerDialog(
                  context,
                  { _, hour, minute ->
                    val amPm = if (hour < 12) "a.m." else "p.m."
                    val h12 = when {
                      hour == 0 -> 12
                      hour > 12 -> hour - 12
                      else -> hour
                    }
                    val min = minute.toString().padStart(2, '0')
                    val monthNames = listOf("ene","feb","mar","abr","may","jun","jul","ago","sep","oct","nov","dic")
                    val label = "$day ${monthNames[month]} $year · $h12:$min $amPm"
                    onSelectTimeSlot(label)
                  },
                  now.get(Calendar.HOUR_OF_DAY),
                  now.get(Calendar.MINUTE),
                  false
                ).show()
              },
              now.get(Calendar.YEAR),
              now.get(Calendar.MONTH),
              now.get(Calendar.DAY_OF_MONTH)
            ).apply {
              datePicker.minDate = now.timeInMillis
            }.show()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (uiState.selectedTimeSlot.isNotBlank())
              ChocolatePrimaryContainer else CreamSurfaceContainerHigh,
            contentColor = if (uiState.selectedTimeSlot.isNotBlank())
              CreamSurfaceContainerLowest else ChocolatePrimaryContainer
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (uiState.selectedTimeSlot.isNotBlank())
              uiState.selectedTimeSlot else "Seleccionar fecha y hora",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
          )
        }

        if (uiState.selectedTimeSlot.isNotBlank()) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "✓ Entrega programada",
            style = MaterialTheme.typography.labelSmall,
            color = GoldSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // 5. Gift Experience Packaging Option
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top
        ) {
          Checkbox(
            checked = uiState.giftWrapEnabled,
            onCheckedChange = onToggleGiftWrap,
            colors = CheckboxDefaults.colors(
              checkedColor = GoldSecondary,
              uncheckedColor = OutlineGrey
            ),
            modifier = Modifier.testTag("gift_toggle_checkbox")
          )

          Spacer(modifier = Modifier.width(4.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Preparar para Regalo de Lujo",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = ChocolatePrimaryContainer
              )
              Text(
                text = "+ S/ 3.00",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = GoldSecondary
              )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = "Lazo de satén dorado hecho a mano, caja precintada con lacre de Fernandina de Jagua y dedicatoria caligráfica.",
              style = MaterialTheme.typography.bodySmall,
              color = OnSurfaceVariant,
              lineHeight = 16.sp
            )
          }
        }

        AnimatedVisibility(visible = uiState.giftWrapEnabled) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 12.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(CreamSurfaceContainerLow)
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.EditNote,
                  contentDescription = null,
                  tint = GoldSecondary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Mensaje para la tarjeta personalizada",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = ChocolatePrimaryContainer
                )
              }
              Text(
                text = "${uiState.giftCardMessage.length}/200",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
              value = uiState.giftCardMessage,
              onValueChange = { if (it.length <= 200) onUpdateGiftMessage(it) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("gift_card_message_input"),
              maxLines = 3,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CreamSurfaceContainerLowest,
                unfocusedContainerColor = CreamSurfaceContainerLowest,
                focusedBorderColor = GoldSecondary,
                unfocusedBorderColor = CreamSurfaceContainerHigh
              ),
              shape = RoundedCornerShape(8.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "MÉTODO DE PAGO",
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      ),
      color = OnSurfaceVariant,
      modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = when (uiState.paymentMethod) {
        PaymentMethod.YAPE -> "El código de aprobación sale en Yape, en el menú lateral. El cobro se hace ahora."
        PaymentMethod.CARD -> "El cobro con tarjeta se hace ahora, a través de Culqi."
      },
      style = MaterialTheme.typography.bodySmall,
      color = OnSurfaceVariant,
      modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      PaymentChoice(
        label = "Yape",
        selected = uiState.paymentMethod == PaymentMethod.YAPE,
        container = YapePurple,
        onClick = { onSelectPayment(PaymentMethod.YAPE) },
        modifier = Modifier.weight(1f),
        testTag = "yape_button"
      )
      PaymentChoice(
        label = "Tarjeta",
        selected = uiState.paymentMethod == PaymentMethod.CARD,
        container = ChocolatePrimaryContainer,
        onClick = { onSelectPayment(PaymentMethod.CARD) },
        modifier = Modifier.weight(1f),
        testTag = "card_button"
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (uiState.paymentMethod == PaymentMethod.CARD) {
      Text(
        text = "Ingresa los datos de tu tarjeta de crédito o débito:",
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = ChocolatePrimaryContainer
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = cardNumber,
        onValueChange = { cardNumber = it.filter(Char::isDigit).take(19) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Número de tarjeta (16 dígitos)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(64.dp)) {
        OutlinedTextField(
          value = cardMonth,
          onValueChange = { cardMonth = it.filter(Char::isDigit).take(2) },
          modifier = Modifier.weight(1f).fillMaxHeight(),
          singleLine = true,
          label = { Text("Mes (MM)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
          value = cardYear,
          onValueChange = { cardYear = it.filter(Char::isDigit).take(4) },
          modifier = Modifier.weight(1f).fillMaxHeight(),
          singleLine = true,
          label = { Text("Año") },
          placeholder = { Text("AAAA") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
          value = cardCvv,
          onValueChange = { cardCvv = it.filter(Char::isDigit).take(4) },
          modifier = Modifier.weight(1f).fillMaxHeight(),
          singleLine = true,
          label = { Text("CVV") },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
      }
      Text(
        text = "🔒 Pago 100% seguro y encriptado a través de Culqi.",
        style = MaterialTheme.typography.labelSmall,
        color = OnSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
      )
      Spacer(modifier = Modifier.height(14.dp))
    }

    if (uiState.paymentMethod == PaymentMethod.YAPE) {
      Text(
        text = "Paga con Yape de forma rápida y sencilla:",
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = ChocolatePrimaryContainer
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = yapePhone,
        onValueChange = { yapePhone = it.filter(Char::isDigit).take(9) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Celular Yape (9 dígitos)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
      )
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = yapeOtp,
        onValueChange = { yapeOtp = it.filter(Char::isDigit).take(6) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Código de aprobación (6 dígitos)") },
        placeholder = { Text("Ej. 123456") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
      )
      Text(
        text = "💡 Abre tu app Yape, pulsa en el menú lateral y copia tu 'Código de aprobación'.",
        style = MaterialTheme.typography.labelSmall,
        color = OnSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
      )
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 7. Price Summary Breakdown
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Subtotal confitería", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
          Text(
            text = "S/ ${String.format(java.util.Locale.US, "%.2f", uiState.cartSubtotal)}",
            fontWeight = FontWeight.SemiBold,
            color = OnSurfaceDark
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (uiState.deliveryMode == DeliveryMode.PICKUP_SERRANO) "Recojo en Tienda (San Miguel)" else "Envío a Domicilio",
              color = OnSurfaceVariant,
              style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Info, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(14.dp))
          }
          Text(
            text = if (uiState.shippingPrice == 0.0) "Gratis" else "S/ ${String.format(java.util.Locale.US, "%.2f", uiState.shippingPrice)}",
            fontWeight = FontWeight.Bold,
            color = GoldSecondary
          )
        }

        if (uiState.giftWrapEnabled) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Empaque para Regalo con Dedicatoria", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            Text(text = "S/ 3.00", fontWeight = FontWeight.SemiBold, color = OnSurfaceDark)
          }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(CreamSurfaceContainer)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Importe Total",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = ChocolatePrimaryContainer
            )
            Text(
              text = "IGV incluido",
              style = MaterialTheme.typography.labelSmall,
              color = OnSurfaceVariant
            )
            Text(
              text = "Emitimos boleta y factura electrónica.",
              style = MaterialTheme.typography.labelSmall,
              color = OnSurfaceVariant
            )
          }

          Text(
            text = "S/ ${String.format(java.util.Locale.US, "%.2f", uiState.cartTotal)}",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Assurance Badges
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (uiState.storefront.trustCraft.isNotBlank()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Verified, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = uiState.storefront.trustCraft, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
      }

      if (uiState.storefront.trustCold.isNotBlank()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Shield, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = uiState.storefront.trustCold, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
      }

      if (uiState.storefront.trustPay.isNotBlank()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = uiState.storefront.trustPay, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (uiState.accountEmail == null && uiState.cartItems.isNotEmpty()) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryFixed.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Info, contentDescription = null, tint = ChocolatePrimaryContainer, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Recuerda: Si aún no has iniciado sesión, al pulsar Pagar se abrirá tu perfil para identificarte.",
            style = MaterialTheme.typography.bodySmall,
            color = OnSecondaryFixed
          )
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Primary CTA Button
    Button(
      onClick = {
        val card = if (uiState.paymentMethod == PaymentMethod.CARD) {
          CardCheckout(cardNumber, cardMonth, cardYear, cardCvv)
        } else {
          null
        }
        val yape = if (uiState.paymentMethod == PaymentMethod.YAPE) {
          YapeCheckout(yapePhone, yapeOtp)
        } else {
          null
        }
        onCheckout(card, yape)
      },
      enabled = !uiState.orderInProgress && uiState.cartItems.isNotEmpty(),
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("checkout_order_cta"),
      colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
      shape = RoundedCornerShape(28.dp),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (uiState.orderInProgress) "Cobrando" else "Pagar ahora",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = CreamSurface
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "S/ ${String.format(java.util.Locale.US, "%.2f", uiState.cartTotal)}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = GoldSecondary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = CreamSurface, modifier = Modifier.size(18.dp))
        }
      }
    }
    }
  }
}
}

@Composable
private fun PaymentChoice(
  label: String,
  selected: Boolean,
  container: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String
) {
  Button(
    onClick = onClick,
    modifier = modifier
      .height(48.dp)
      .testTag(testTag)
      .border(
        width = if (selected) 0.dp else 1.dp,
        color = if (selected) Color.Transparent else OutlineGrey,
        shape = RoundedCornerShape(24.dp)
      ),
    colors = ButtonDefaults.buttonColors(
      containerColor = if (selected) container else CreamSurfaceContainerLowest
    ),
    shape = RoundedCornerShape(24.dp),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (selected) 2.dp else 0.dp)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
      color = if (selected) Color.White else OnSurfaceDark,
      maxLines = 1
    )
  }
}

@Composable
private fun CartItemCard(
  item: CartItem,
  onMinus: () -> Unit,
  onPlus: () -> Unit,
  onDelete: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("cart_item_${item.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        SanMiguelImage(
          model = item.imageUrl,
          contentDescription = item.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Text(
              text = item.title,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = ChocolatePrimaryContainer,
              modifier = Modifier.weight(1f)
            )
            IconButton(
              onClick = onDelete,
              modifier = Modifier
                .size(28.dp)
                .testTag("delete_${item.id}")
            ) {
              Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Eliminar",
                tint = OutlineGrey,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Stepper
            Row(
              modifier = Modifier
                .clip(CircleShape)
                .background(CreamSurfaceContainerLow)
                .padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "−",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
                modifier = Modifier
                  .clickable { onMinus() }
                  .padding(horizontal = 6.dp)
                  .testTag("minus_${item.id}")
              )
              Text(
                text = "${item.quantity}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = ChocolatePrimaryContainer,
                modifier = Modifier.padding(horizontal = 4.dp)
              )
              Text(
                text = "+",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
                modifier = Modifier
                  .clickable { onPlus() }
                  .padding(horizontal = 6.dp)
                  .testTag("plus_${item.id}")
              )
            }

            Text(
              text = "S/ ${String.format(java.util.Locale.US, "%.2f", item.totalPrice)}",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = ChocolatePrimaryContainer
            )
          }
        }
      }

      // Expandable flavors accordion if custom box
      if (item.customFlavors.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CreamSurfaceContainerLow)
            .clickable { isExpanded = !isExpanded }
            .padding(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = GoldSecondary,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Ver sabores elegidos (${item.customFlavors.sumOf { it.second }} uds)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = GoldSecondary
              )
            }

            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = GoldSecondary,
              modifier = Modifier.size(18.dp)
            )
          }

          AnimatedVisibility(visible = isExpanded) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              item.customFlavors.forEach { (flavorName, count) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(5.dp)
                      .background(GoldSecondary, CircleShape)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${count}x $flavorName",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = OnSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DeliveryOptionCard(
  title: String,
  subtitle: String,
  highlight: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .testTag("delivery_${title.take(5)}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) ChocolatePrimaryContainer else CreamSurfaceContainerLowest
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .background(
              if (isSelected) GoldSecondaryContainer.copy(alpha = 0.3f) else CreamSurfaceContainer,
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) GoldSecondaryContainer else OnSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }

        RadioButton(
          selected = isSelected,
          onClick = onClick,
          colors = RadioButtonDefaults.colors(
            selectedColor = GoldSecondaryContainer,
            unselectedColor = OutlineGrey
          )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = if (isSelected) CreamSurface else OnSurfaceDark
      )

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = if (isSelected) CreamSurfaceContainerHigh else OnSurfaceVariant
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = highlight,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = GoldSecondary
      )
    }
  }
}

@Composable
private fun TimeSlotPill(
  label: String,
  isSelected: Boolean,
  icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
  showCheck: Boolean = false,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .clip(CircleShape)
      .background(if (isSelected) ChocolatePrimaryContainer else CreamSurfaceContainerLowest)
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 7.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    icon?.let {
      Icon(it, contentDescription = null, tint = if (isSelected) CreamSurface else OnSurfaceVariant, modifier = Modifier.size(14.dp))
      Spacer(modifier = Modifier.width(4.dp))
    }
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      ),
      color = if (isSelected) CreamSurface else OnSurfaceDark
    )
    if (showCheck && isSelected) {
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = null,
        tint = GoldSecondary,
        modifier = Modifier.size(14.dp)
      )
    }
  }
}
