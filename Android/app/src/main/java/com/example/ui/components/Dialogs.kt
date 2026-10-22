package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Product
import com.example.ui.components.SanMiguelImage
import com.example.ui.theme.ChocolatePrimaryContainer
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurfaceContainer
import com.example.ui.theme.CreamSurfaceContainerHigh
import com.example.ui.theme.CreamSurfaceContainerLow
import com.example.ui.theme.CreamSurfaceContainerLowest
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryContainer
import com.example.ui.theme.OnGoldSecondaryContainer
import com.example.ui.theme.OnSurfaceDark
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.SecondaryFixed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SustainabilityBottomSheet(
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CreamBackground,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(SecondaryFixed, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = null,
              tint = ChocolatePrimaryContainer,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Cacao 100% Sostenible",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
              ),
              color = ChocolatePrimaryContainer
            )
            Text(
              text = "Chocolatería San Miguel · Comercio Justo",
              style = MaterialTheme.typography.bodySmall,
              color = OnSurfaceVariant
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = OnSurfaceVariant)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "Nuestro Compromiso Ético & Regenerativo",
        style = MaterialTheme.typography.titleLarge.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold
        ),
        color = ChocolatePrimaryContainer
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "En Fernandina de Jagua Chocolates adquirimos el 100% de nuestras habas de cacao directamente a pequeñas cooperativas de Piura, Cusco y San Martín.",
        style = MaterialTheme.typography.bodyMedium,
        color = OnSurfaceDark,
        lineHeight = 22.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      Card(
        colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLow),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Eco, contentDescription = null, tint = GoldSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Agricultura Regenerativa & Trazabilidad",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = ChocolatePrimaryContainer
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Pagamos una prima del 40% sobre el precio de mercado internacional, financiando escuelas rurales y garantizando cultivos libres de deforestación.",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = onDismiss,
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("close_sustainability_button"),
        colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
        shape = RoundedCornerShape(25.dp)
      ) {
        Text(
          text = "Entendido",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = CreamSurfaceContainerLowest
        )
      }
    }
  }
}

@Composable
fun SearchDialog(
  searchQuery: String,
  products: List<Product>,
  onQueryChange: (String) -> Unit,
  onProductClick: (Product) -> Unit,
  onDismiss: () -> Unit
) {
  val filteredProducts = if (searchQuery.isBlank()) {
    products
  } else {
    products.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
          it.subtitle.contains(searchQuery, ignoreCase = true) ||
          it.description.contains(searchQuery, ignoreCase = true)
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {},
    containerColor = CreamBackground,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Buscar en la Tienda",
          style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
          color = ChocolatePrimaryContainer
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = OnSurfaceVariant)
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onQueryChange,
          placeholder = { Text("Ej. Praliné, Avellana, Trufa...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldSecondary) },
          singleLine = true,
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GoldSecondary,
            unfocusedBorderColor = CreamSurfaceContainerHigh,
            focusedContainerColor = CreamSurfaceContainerLowest,
            unfocusedContainerColor = CreamSurfaceContainerLowest
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_text_field")
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
        ) {
          items(filteredProducts) { product ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onProductClick(product)
                  onDismiss()
                }
                .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              SanMiguelImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(8.dp))
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = product.name,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = ChocolatePrimaryContainer,
                  maxLines = 1
                )
                Text(
                  text = product.subtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = OnSurfaceVariant,
                  maxLines = 1
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
  )
}

@Composable
fun OrderSuccessDialog(
  orderId: String,
  customerName: String,
  receiptBusy: Boolean,
  receiptLabel: String?,
  receiptError: String?,
  onRequestReceipt: (kind: String, name: String, document: String) -> Unit,
  onDownload: () -> Unit,
  onPrint: () -> Unit,
  onDismiss: () -> Unit
) {
  var kind by remember { mutableStateOf("boleta") }
  var name by remember(customerName) { mutableStateOf(customerName) }
  var document by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("order_success_cta")
      ) {
        Text("Volver a la Tienda", color = CreamSurfaceContainerLowest, fontWeight = FontWeight.Bold)
      }
    },
    containerColor = CreamBackground,
    title = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(56.dp)
            .background(GoldSecondaryContainer, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = ChocolatePrimaryContainer,
            modifier = Modifier.size(32.dp)
          )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "¡Pedido Tramitado con Éxito!",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = ChocolatePrimaryContainer,
          textAlign = TextAlign.Center
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 360.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Código de Seguimiento: $orderId",
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = GoldSecondary
          ),
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Emitimos boleta y factura electrónica. Puedes descargarla o imprimirla ahora.",
          style = MaterialTheme.typography.bodyMedium,
          color = OnSurfaceDark,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (receiptLabel == null) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { kind = "boleta" }) {
              Text(if (kind == "boleta") "Boleta" else "boleta", color = ChocolatePrimaryContainer)
            }
            TextButton(onClick = { kind = "factura" }) {
              Text(if (kind == "factura") "Factura" else "factura", color = ChocolatePrimaryContainer)
            }
          }
          OutlinedTextField(
            value = name,
            onValueChange = { name = it.take(150) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(if (kind == "factura") "Razón social" else "Nombre") }
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = document,
            onValueChange = { document = it.filter(Char::isDigit).take(if (kind == "factura") 11 else 8) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(if (kind == "factura") "RUC" else "DNI, si lo tienes") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
          )
          receiptError?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = ChocolatePrimaryContainer, textAlign = TextAlign.Center)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = { onRequestReceipt(kind, name, document) },
            enabled = !receiptBusy,
            colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(if (receiptBusy) "Emitiendo" else "Emitir comprobante", color = OnGoldSecondaryContainer)
          }
        } else {
          Text(
            text = receiptLabel,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = onDownload,
            colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Descargar PDF", color = OnGoldSecondaryContainer)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = onPrint,
            colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Imprimir", color = CreamSurfaceContainerLowest)
          }
        }
      }
    }
  )
}
