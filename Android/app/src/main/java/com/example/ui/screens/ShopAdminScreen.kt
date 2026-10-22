package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoxTier
import com.example.model.Product
import com.example.model.ShopCategory
import com.example.model.StorefrontCopy
import com.example.ui.components.SanMiguelImage
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
import com.example.ui.theme.OnSurfaceDark
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineGrey
import com.example.viewmodel.ShopUiState
import java.util.Locale

@Composable
fun ShopAdminScreen(
  uiState: ShopUiState,
  onBack: () -> Unit = {},
  onEnsureCatalog: () -> Unit,
  onEnsureStorefront: () -> Unit,
  onSave: (Product) -> Unit,
  onDelete: (String) -> Unit,
  onUploadImage: (productId: String, uri: Uri, onUploaded: (String) -> Unit) -> Unit,
  onSaveCopy: (StorefrontCopy) -> Unit,
  onSaveBoxTiers: (List<BoxTier>) -> Unit,
  onSaveCategory: (ShopCategory) -> Unit,
  onDeleteCategory: (String) -> Unit,
  onUploadStoreImage: (folder: String, uri: Uri, onUploaded: (String) -> Unit) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var editingProduct by remember { mutableStateOf<Product?>(null) }
  val currentEditingProduct = rememberUpdatedState(editingProduct)

  val productImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    val current = currentEditingProduct.value
    if (uri != null && current != null) {
      onUploadImage(current.id, uri) { url ->
        val latest = currentEditingProduct.value
        if (latest != null && latest.id == current.id) {
          editingProduct = latest.copy(imageUrl = url)
        }
      }
    }
  }

  LaunchedEffect(uiState.catalogSynced, uiState.storeProducts.size, uiState.categoriesSynced, uiState.isShopOwner) {
    onEnsureCatalog()
    onEnsureStorefront()
  }

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(CreamBackground)
  ) {
    val contentMaxWidth = if (maxWidth >= 720.dp) 740.dp else maxWidth

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(bottom = 120.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = contentMaxWidth)
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        // Barra Superior de Administración
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(CreamSurfaceContainerLowest)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Volver",
              tint = ChocolatePrimaryContainer
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "👑 Panel de Administración",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold
                ),
                color = ChocolatePrimaryContainer
              )
            }
            Text(
              text = "Modo Dueño · Don Antonio",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = GoldSecondary
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(GoldSecondaryContainer)
              .clickable(onClick = onBack)
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "Ver Tienda",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = OnGoldSecondaryContainer
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pestañas Segmentadas
        val tabs = listOf(
          AdminTabItem("Productos", Icons.Default.Inventory2, uiState.editableProducts.size),
          AdminTabItem("Categorías", Icons.Default.Category, uiState.visibleCategories.size),
          AdminTabItem("Cajas", Icons.Default.CardGiftcard, uiState.activeBoxTiers.size),
          AdminTabItem("Textos", Icons.Default.Palette, null)
        )

        val tabScrollState = rememberScrollState()
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CreamSurfaceContainer)
            .padding(4.dp)
            .horizontalScroll(tabScrollState),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          tabs.forEachIndexed { index, tab ->
            val isSelected = selectedTab == index
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) ChocolatePrimaryContainer else Color.Transparent)
                .clickable {
                  selectedTab = index
                  editingProduct = null
                }
                .padding(horizontal = 14.dp, vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = tab.icon,
                  contentDescription = null,
                  tint = if (isSelected) GoldSecondaryContainer else OnSurfaceVariant,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = tab.label,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  ),
                  color = if (isSelected) CreamSurface else OnSurfaceVariant
                )
                if (tab.count != null) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Box(
                    modifier = Modifier
                      .clip(CircleShape)
                      .background(if (isSelected) GoldSecondaryContainer else CreamSurfaceContainerHigh)
                      .padding(horizontal = 6.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = "${tab.count}",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                      color = if (isSelected) OnGoldSecondaryContainer else OnSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // CONTENIDO SEGÚN PESTAÑA ACTIVA
        when (selectedTab) {
          0 -> {
            // PESTAÑA: PRODUCTOS
            ProductManagementTab(
              products = uiState.editableProducts,
              categories = uiState.visibleCategories,
              editingProduct = editingProduct,
              onStartNew = {
                editingProduct = Product(
                  id = "p${System.currentTimeMillis()}",
                  name = "",
                  subtitle = "",
                  price = 0.0,
                  imageUrl = "",
                  description = "",
                  category = if (uiState.visibleCategories.isNotEmpty()) uiState.visibleCategories.first().name else "Bombones",
                  origin = "Perú",
                  badge = null
                )
              },
              onStartEdit = { editingProduct = it },
              onPickImage = { productImagePicker.launch("image/*") },
              onChangeProduct = { editingProduct = it },
              onSaveProduct = {
                onSave(it)
                editingProduct = null
              },
              onDeleteProduct = {
                onDelete(it)
                editingProduct = null
              },
              onCloseEditor = { editingProduct = null }
            )
          }

          1 -> {
            // PESTAÑA: CATEGORÍAS
            CategoryManager(
              categories = uiState.visibleCategories,
              onSaveCategory = onSaveCategory,
              onDeleteCategory = onDeleteCategory,
              onUploadImage = onUploadStoreImage
            )
          }

          2 -> {
            // PESTAÑA: CAJAS Y COFRES
            BoxTiersEditor(
              tiers = uiState.activeBoxTiers,
              onSaveTiers = onSaveBoxTiers
            )
          }

          3 -> {
            // PESTAÑA: TEXTOS Y PORTADA
            StorefrontTextEditor(
              copy = uiState.storefront,
              onSaveCopy = onSaveCopy,
              onUploadImage = onUploadStoreImage
            )
          }
        }
      }
    }
  }
}

private data class AdminTabItem(
  val label: String,
  val icon: ImageVector,
  val count: Int?
)

@Composable
private fun ProductManagementTab(
  products: List<Product>,
  categories: List<ShopCategory>,
  editingProduct: Product?,
  onStartNew: () -> Unit,
  onStartEdit: (Product) -> Unit,
  onPickImage: () -> Unit,
  onChangeProduct: (Product) -> Unit,
  onSaveProduct: (Product) -> Unit,
  onDeleteProduct: (String) -> Unit,
  onCloseEditor: () -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Encabezado de la pestaña
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Catálogo de Productos",
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = ChocolatePrimaryContainer
        )
        Text(
          text = "${products.size} creaciones registradas",
          style = MaterialTheme.typography.bodySmall,
          color = OnSurfaceVariant
        )
      }

      Button(
        onClick = onStartNew,
        colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
        shape = RoundedCornerShape(20.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = CreamSurface, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Nuevo Producto", color = CreamSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    // Formulario de edición desplegado
    editingProduct?.let { product ->
      ProductEditorCard(
        product = product,
        categories = categories,
        onChange = onChangeProduct,
        onPickImage = onPickImage,
        onSave = { onSaveProduct(product) },
        onDelete = { onDeleteProduct(product.id) },
        onClose = onCloseEditor
      )
    }

    if (products.isEmpty()) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Todavía no tienes productos creados.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))
          Button(onClick = onStartNew) {
            Text("Crear tu primer producto")
          }
        }
      }
    } else {
      // Lista de productos
      products.forEach { product ->
        AdminProductCard(
          product = product,
          onEdit = { onStartEdit(product) },
          onToggleVisibility = {
            onSaveProduct(product.copy(visible = !product.visible))
          }
        )
      }
    }
  }
}

@Composable
private fun AdminProductCard(
  product: Product,
  onEdit: () -> Unit,
  onToggleVisibility: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    border = if (!product.visible) BorderStroke(1.dp, OutlineGrey.copy(alpha = 0.4f)) else null
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (product.imageUrl.isNotBlank()) {
        SanMiguelImage(
          model = product.imageUrl,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(62.dp)
            .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = product.name.ifBlank { "Sin nombre" },
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = if (product.visible) OnSurfaceDark else OutlineGrey,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          Spacer(modifier = Modifier.width(6.dp))

          // Badge de estado
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(if (product.visible) GoldSecondaryContainer.copy(alpha = 0.4f) else CreamSurfaceContainerHigh)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (product.visible) "Visible" else "Oculto",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              ),
              color = if (product.visible) OnGoldSecondaryContainer else OutlineGrey
            )
          }
        }

        Text(
          text = "${product.category} · S/ ${String.format(Locale.US, "%.2f", product.price)}",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
          color = GoldSecondary
        )

        if (product.subtitle.isNotBlank()) {
          Text(
            text = product.subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = OnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Botón de visibilidad rápida
      IconButton(
        onClick = onToggleVisibility,
        modifier = Modifier.size(34.dp)
      ) {
        Icon(
          imageVector = if (product.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
          contentDescription = if (product.visible) "Ocultar" else "Mostrar",
          tint = if (product.visible) GoldSecondary else OutlineGrey,
          modifier = Modifier.size(18.dp)
        )
      }

      // Botón Editar
      Button(
        onClick = onEdit,
        colors = ButtonDefaults.buttonColors(containerColor = CreamSurfaceContainer),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Edit, contentDescription = null, tint = ChocolatePrimaryContainer, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Editar", color = ChocolatePrimaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun ProductEditorCard(
  product: Product,
  categories: List<ShopCategory>,
  onChange: (Product) -> Unit,
  onPickImage: () -> Unit,
  onSave: () -> Unit,
  onDelete: () -> Unit,
  onClose: () -> Unit
) {
  var priceText by remember(product.id) {
    mutableStateOf(String.format(Locale.US, "%.2f", product.price))
  }

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
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (product.name.isNotBlank()) "Editar: ${product.name}" else "Nuevo Producto",
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = ChocolatePrimaryContainer,
          modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onClose) {
          Text("Cerrar")
        }
      }

      // Foto del producto
      if (product.imageUrl.isNotBlank()) {
        SanMiguelImage(
          model = product.imageUrl,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
        )
      }

      OutlinedButton(
        onClick = onPickImage,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (product.imageUrl.isBlank()) "Elegir foto del celular" else "Cambiar foto")
      }

      OutlinedTextField(
        value = product.name,
        onValueChange = { onChange(product.copy(name = it)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Nombre del Producto") },
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = product.subtitle,
        onValueChange = { onChange(product.copy(subtitle = it)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Subtítulo (ej. Cacao 70% de Piura)") },
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = priceText,
        onValueChange = { value ->
          priceText = value
          val parsed = value.replace(',', '.').toDoubleOrNull()
          if (parsed != null) {
            onChange(product.copy(price = parsed))
          }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Precio en soles (S/)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(10.dp)
      )

      // Selector de Categoría
      Column {
        Text(
          text = "Categoría:",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        val categoryOptions = if (categories.isNotEmpty()) categories.map { it.name } else listOf("Bombones", "Trufas", "Cajas", "Tabletas")
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categoryOptions.forEach { catName ->
            val isSelected = product.category.equals(catName, ignoreCase = true)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) ChocolatePrimaryContainer else CreamSurfaceContainer)
                .clickable { onChange(product.copy(category = catName)) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = catName,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                color = if (isSelected) CreamSurface else OnSurfaceDark
              )
            }
          }
        }
      }

      OutlinedTextField(
        value = product.description,
        onValueChange = { onChange(product.copy(description = it)) },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 3,
        label = { Text("Descripción") },
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = product.origin,
        onValueChange = { onChange(product.copy(origin = it)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Origen del Cacao (ej. Piura y San Martín)") },
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = product.badge.orEmpty(),
        onValueChange = { onChange(product.copy(badge = it)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Etiqueta destacada (ej. Más vendido, Favorito)") },
        shape = RoundedCornerShape(10.dp)
      )

      // Interruptor de visibilidad
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CreamSurfaceContainerLow)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "Visible para los clientes",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer
          )
          Text(
            text = if (product.visible) "Los clientes pueden ver y comprar este producto" else "Producto pausado temporalmente",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
          )
        }

        Switch(
          checked = product.visible,
          onCheckedChange = { onChange(product.copy(visible = it)) },
          colors = SwitchDefaults.colors(
            checkedThumbColor = CreamSurface,
            checkedTrackColor = ChocolatePrimaryContainer
          )
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Button(
        onClick = onSave,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
        shape = RoundedCornerShape(24.dp)
      ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = CreamSurface, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Guardar Producto", fontWeight = FontWeight.Bold, color = CreamSurface)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        TextButton(onClick = onDelete) {
          Text("Eliminar producto de la tienda", color = Color(0xFFBA1A1A))
        }

        TextButton(onClick = onClose) {
          Text("Cancelar")
        }
      }
    }
  }
}
