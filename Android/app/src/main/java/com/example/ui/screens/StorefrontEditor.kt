package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoxTier
import com.example.model.ShopCategory
import com.example.model.StorefrontCopy
import com.example.ui.components.SanMiguelImage
import com.example.ui.theme.ChocolatePrimaryContainer
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.CreamSurfaceContainer
import com.example.ui.theme.CreamSurfaceContainerHigh
import com.example.ui.theme.CreamSurfaceContainerLow
import com.example.ui.theme.CreamSurfaceContainerLowest
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryContainer
import com.example.ui.theme.OnGoldSecondaryContainer
import com.example.ui.theme.OnSurfaceDark
import com.example.ui.theme.OnSurfaceVariant
import java.util.Locale

@Composable
fun StorefrontTextEditor(
  copy: StorefrontCopy,
  onSaveCopy: (StorefrontCopy) -> Unit,
  onUploadImage: (folder: String, uri: Uri, onUploaded: (String) -> Unit) -> Unit
) {
  var draft by remember(copy) { mutableStateOf(copy) }

  var contactExpanded by remember { mutableStateOf(true) }
  var bannerExpanded by remember { mutableStateOf(false) }
  var deliveryExpanded by remember { mutableStateOf(false) }
  var brandExpanded by remember { mutableStateOf(false) }

  val heroPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    if (uri != null) {
      onUploadImage("storefront", uri) { url ->
        draft = draft.copy(heroImageUrl = url)
      }
    }
  }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Text(
      text = "Textos y Portada de la Tienda",
      style = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
      ),
      color = ChocolatePrimaryContainer
    )
    Text(
      text = "Organizado por secciones claras. Cualquier cambio que guardes se reflejará de inmediato en la app de tus clientes.",
      style = MaterialTheme.typography.bodySmall,
      color = OnSurfaceVariant
    )

    // SECCIÓN 1: DATOS DEL NEGOCIO, HORARIO Y CONTACTO
    SectionAccordionCard(
      title = "📍 Dirección, Horario y Teléfono",
      subtitle = "Ubicación del taller, horarios de atención y contacto para pedidos",
      isExpanded = contactExpanded,
      onToggle = { contactExpanded = !contactExpanded }
    ) {
      AdminTextField(
        label = "Dirección de la Chocolatería (San Miguel)",
        value = draft.storeAddress
      ) { draft = draft.copy(storeAddress = it) }

      AdminTextField(
        label = "Horario de trabajo y atención",
        value = draft.storeHours
      ) { draft = draft.copy(storeHours = it) }

      AdminTextField(
        label = "Teléfono / WhatsApp de atención y pedidos",
        value = draft.storePhone
      ) { draft = draft.copy(storePhone = it) }
    }

    // SECCIÓN 2: BANNER PRINCIPAL DE LA PORTADA
    SectionAccordionCard(
      title = "🖼️ Banner Principal de Portada",
      subtitle = "Foto, título y mensaje promocional destacado",
      isExpanded = bannerExpanded,
      onToggle = { bannerExpanded = !bannerExpanded }
    ) {
      if (draft.heroImageUrl.isNotBlank()) {
        SanMiguelImage(
          model = draft.heroImageUrl,
          contentDescription = "Banner",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
      }

      OutlinedButton(
        onClick = { heroPicker.launch("image/*") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (draft.heroImageUrl.isBlank()) "Elegir foto del banner" else "Cambiar foto del banner")
      }

      Spacer(modifier = Modifier.height(8.dp))
      AdminTextField(label = "Título del banner", value = draft.heroTitle) { draft = draft.copy(heroTitle = it) }
      AdminTextField(label = "Temporada / Colección", value = draft.heroSeason) { draft = draft.copy(heroSeason = it) }
      AdminTextField(label = "Etiqueta destacada", value = draft.heroBadge) { draft = draft.copy(heroBadge = it) }
      AdminTextField(label = "Descripción del banner", value = draft.heroBody) { draft = draft.copy(heroBody = it) }
      AdminTextField(label = "Precio mostrado", value = draft.heroPrice) { draft = draft.copy(heroPrice = it) }
      AdminTextField(label = "Texto del botón", value = draft.heroButton) { draft = draft.copy(heroButton = it) }
    }

    // SECCIÓN 3: ENTREGA Y EMPAQUE
    SectionAccordionCard(
      title = "🚚 Modalidades de Entrega y Empaque",
      subtitle = "Detalles de envío a domicilio, empaque y garantías",
      isExpanded = deliveryExpanded,
      onToggle = { deliveryExpanded = !deliveryExpanded }
    ) {
      AdminTextField(label = "Título de entrega", value = draft.deliveryTitle) { draft = draft.copy(deliveryTitle = it) }
      AdminTextField(label = "Detalle de entrega", value = draft.deliveryDetail) { draft = draft.copy(deliveryDetail = it) }
      Spacer(modifier = Modifier.height(4.dp))
      AdminTextField(label = "Título de empaque para regalo", value = draft.giftTitle) { draft = draft.copy(giftTitle = it) }
      AdminTextField(label = "Detalle del empaque", value = draft.giftDetail) { draft = draft.copy(giftDetail = it) }
      Spacer(modifier = Modifier.height(4.dp))
      AdminTextField(label = "Garantía 1 (Bolsa)", value = draft.trustCraft) { draft = draft.copy(trustCraft = it) }
      AdminTextField(label = "Garantía 2 (Bolsa)", value = draft.trustCold) { draft = draft.copy(trustCold = it) }
      AdminTextField(label = "Garantía 3 (Bolsa)", value = draft.trustPay) { draft = draft.copy(trustPay = it) }
    }

    // SECCIÓN 4: IDENTIDAD DE MARCA Y PIE DE PÁGINA
    SectionAccordionCard(
      title = "✨ Identidad de Marca y Pie de Página",
      subtitle = "Lema de la chocolatería, pie de página y descripción",
      isExpanded = brandExpanded,
      onToggle = { brandExpanded = !brandExpanded }
    ) {
      AdminTextField(label = "Frase superior de cabecera", value = draft.eyebrow) { draft = draft.copy(eyebrow = it) }
      AdminTextField(label = "Lema principal", value = draft.headline) { draft = draft.copy(headline = it) }
      Spacer(modifier = Modifier.height(4.dp))
      AdminTextField(label = "Texto de pie de página", value = draft.footerLine) { draft = draft.copy(footerLine = it) }
      AdminTextField(label = "Lema en perfil", value = draft.profileTagline) { draft = draft.copy(profileTagline = it) }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
      onClick = { onSaveCopy(draft) },
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
      colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
      shape = RoundedCornerShape(25.dp)
    ) {
      Text("Guardar Textos de la Tienda", fontWeight = FontWeight.Bold, color = CreamSurface)
    }
  }
}

@Composable
fun BoxTiersEditor(
  tiers: List<BoxTier>,
  onSaveTiers: (List<BoxTier>) -> Unit
) {
  var list by remember(tiers) { mutableStateOf(tiers) }
  var editingTier by remember { mutableStateOf<BoxTier?>(null) }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "🎁 Cofres para \"Arma tu Caja\"",
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = ChocolatePrimaryContainer
        )
        Text(
          text = "${list.size} tamaños configurados",
          style = MaterialTheme.typography.bodySmall,
          color = OnSurfaceVariant
        )
      }

      Button(
        onClick = {
          editingTier = BoxTier(
            size = 18,
            name = "Cofre Real",
            subtitle = "18 Piezas",
            price = 42.00,
            isFavorite = false
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
        shape = RoundedCornerShape(20.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = CreamSurface, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Nuevo Cofre", color = CreamSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Text(
      text = "Aquí puedes añadir nuevos tamaños de cofres, cambiar la cantidad de bombones, modificar los precios o eliminar los tamaños que no desees vender. Los clientes verán los cambios inmediatamente.",
      style = MaterialTheme.typography.bodySmall,
      color = OnSurfaceVariant
    )

    // Formulario de edición de cofre
    editingTier?.let { tier ->
      TierEditCard(
        tier = tier,
        onSave = { updated ->
          val existingIndex = list.indexOfFirst { it.size == tier.size }
          list = if (existingIndex >= 0) {
            list.toMutableList().also { it[existingIndex] = updated }
          } else {
            (list + updated).sortedBy { it.size }
          }
          editingTier = null
        },
        onDelete = {
          list = list.filterNot { it.size == tier.size }
          editingTier = null
        },
        onCancel = { editingTier = null }
      )
    }

    // Lista de cofres configurados
    list.sortedBy { it.size }.forEach { tier ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (tier.isFavorite) BorderStroke(1.5.dp, GoldSecondaryContainer) else null
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(if (tier.isFavorite) GoldSecondaryContainer else CreamSurfaceContainerLow),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CardGiftcard,
              contentDescription = null,
              tint = if (tier.isFavorite) OnGoldSecondaryContainer else GoldSecondary,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            if (tier.isFavorite) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "MÁS ELEGIDO",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                  color = GoldSecondary
                )
              }
            }
            Text(
              text = "${tier.name} (${tier.size} bombones)",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = ChocolatePrimaryContainer
            )
            Text(
              text = "S/ ${String.format(Locale.US, "%.2f", tier.price)} · ${tier.subtitle}",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = GoldSecondary
            )
          }

          Button(
            onClick = { editingTier = tier },
            colors = ButtonDefaults.buttonColors(containerColor = CreamSurfaceContainer),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Editar", color = ChocolatePrimaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(
      onClick = { onSaveTiers(list) },
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
      colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
      shape = RoundedCornerShape(25.dp)
    ) {
      Text("Guardar Configuración de Cofres", fontWeight = FontWeight.Bold, color = CreamSurface)
    }
  }
}

@Composable
private fun TierEditCard(
  tier: BoxTier,
  onSave: (BoxTier) -> Unit,
  onDelete: () -> Unit,
  onCancel: () -> Unit
) {
  var name by remember(tier) { mutableStateOf(tier.name) }
  var sizeText by remember(tier) { mutableStateOf(tier.size.toString()) }
  var subtitle by remember(tier) { mutableStateOf(tier.subtitle) }
  var priceText by remember(tier) { mutableStateOf(String.format(Locale.US, "%.2f", tier.price)) }
  var isFavorite by remember(tier) { mutableStateOf(tier.isFavorite) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    border = BorderStroke(1.5.dp, GoldSecondaryContainer)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(
        text = "Configurar Cofre",
        style = MaterialTheme.typography.titleMedium.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold
        ),
        color = ChocolatePrimaryContainer
      )

      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Nombre del Cofre (ej. Cofre Clásico)") },
        shape = RoundedCornerShape(10.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = sizeText,
          onValueChange = { sizeText = it.filter(Char::isDigit).take(3) },
          modifier = Modifier.weight(1f),
          singleLine = true,
          label = { Text("Bombones (uds)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
          value = priceText,
          onValueChange = { priceText = it },
          modifier = Modifier.weight(1f),
          singleLine = true,
          label = { Text("Precio (S/)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          shape = RoundedCornerShape(10.dp)
        )
      }

      OutlinedTextField(
        value = subtitle,
        onValueChange = { subtitle = it },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Subtítulo (ej. 12 Piezas de Autor)") },
        shape = RoundedCornerShape(10.dp)
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CreamSurfaceContainerLow)
          .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Destacar como \"MÁS ELEGIDO\"", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceDark)
        Switch(checked = isFavorite, onCheckedChange = { isFavorite = it })
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            val size = sizeText.toIntOrNull() ?: tier.size
            val price = priceText.replace(',', '.').toDoubleOrNull() ?: tier.price
            onSave(BoxTier(size, name.ifBlank { "Cofre" }, subtitle.ifBlank { "$size Piezas" }, price, isFavorite))
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Aplicar", color = CreamSurface, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onDelete,
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Eliminar", color = Color(0xFFBA1A1A))
        }

        TextButton(onClick = onCancel) {
          Text("Cancelar")
        }
      }
    }
  }
}

@Composable
fun CategoryManager(
  categories: List<ShopCategory>,
  onSaveCategory: (ShopCategory) -> Unit,
  onDeleteCategory: (String) -> Unit,
  onUploadImage: (folder: String, uri: Uri, onUploaded: (String) -> Unit) -> Unit
) {
  var editing by remember { mutableStateOf<ShopCategory?>(null) }
  val currentEditing = rememberUpdatedState(editing)

  val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    val current = currentEditing.value
    if (uri != null && current != null) {
      onUploadImage("categories/${current.id}", uri) { url ->
        val latest = currentEditing.value
        if (latest != null && latest.id == current.id) {
          editing = latest.copy(imageUrl = url)
        }
      }
    }
  }

  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Categorías de la Vitrina",
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = ChocolatePrimaryContainer
        )
        Text(
          text = "${categories.size} secciones activas",
          style = MaterialTheme.typography.bodySmall,
          color = OnSurfaceVariant
        )
      }

      Button(
        onClick = {
          editing = ShopCategory(
            id = "c${System.currentTimeMillis()}",
            name = "",
            sort = categories.size
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
        shape = RoundedCornerShape(20.dp)
      ) {
        Text("+ Nueva Categoría", color = CreamSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    editing?.let { category ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.5.dp, GoldSecondaryContainer)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = if (categories.any { it.id == category.id }) "Editar Categoría" else "Nueva Categoría",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer
          )

          OutlinedTextField(
            value = category.name,
            onValueChange = { editing = category.copy(name = it.take(80)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Nombre de la Categoría") },
            shape = RoundedCornerShape(10.dp)
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            if (category.imageUrl.isNotBlank()) {
              SanMiguelImage(
                model = category.imageUrl,
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(56.dp)
                  .clip(CircleShape)
              )
              Spacer(modifier = Modifier.width(12.dp))
            }
            OutlinedButton(
              onClick = { imagePicker.launch("image/*") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text(if (category.imageUrl.isBlank()) "Elegir foto circular" else "Cambiar foto")
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                onSaveCategory(category)
                editing = null
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Guardar", color = CreamSurface)
            }

            if (categories.any { it.id == category.id }) {
              OutlinedButton(
                onClick = {
                  onDeleteCategory(category.id)
                  editing = null
                },
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Eliminar")
              }
            }

            TextButton(
              onClick = { editing = null }
            ) {
              Text("Cancelar")
            }
          }
        }
      }
    }

    categories.filter { !it.id.startsWith("local_") }.forEach { category ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (category.imageUrl.isNotBlank()) {
            SanMiguelImage(
              model = category.imageUrl,
              contentDescription = category.name,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
          }

          Text(
            text = category.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = OnSurfaceDark
          )

          Button(
            onClick = { editing = category },
            colors = ButtonDefaults.buttonColors(containerColor = CreamSurfaceContainer),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text("Editar", color = ChocolatePrimaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun SectionAccordionCard(
  title: String,
  subtitle: String,
  isExpanded: Boolean,
  onToggle: () -> Unit,
  content: @Composable () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggle() }
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
          )
        }
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = GoldSecondary,
          modifier = Modifier.size(24.dp)
        )
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          content()
        }
      }
    }
  }
}

@Composable
private fun AdminTextField(label: String, value: String, onChange: (String) -> Unit) {
  OutlinedTextField(
    value = value,
    onValueChange = onChange,
    modifier = Modifier.fillMaxWidth(),
    label = { Text(label) },
    shape = RoundedCornerShape(10.dp)
  )
}
