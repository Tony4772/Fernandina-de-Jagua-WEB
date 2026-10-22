package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SanMiguelData
import com.example.model.BonbonFlavor
import com.example.model.BoxTier
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
import com.example.ui.theme.SecondaryFixed
import com.example.viewmodel.ShopUiState
import java.util.Locale

@Composable
fun BoxBuilderScreen(
  uiState: ShopUiState,
  onSelectTier: (Int) -> Unit,
  onModifyCount: (String, Int) -> Unit,
  onClearBox: () -> Unit,
  onConfirm: () -> Unit
) {
  val scrollState = rememberScrollState()
  var selectedFilter by remember { mutableStateOf("Todos") }

  val tiers = uiState.activeBoxTiers
  val currentTier = uiState.selectedBoxTier
  val totalBonbons = uiState.bonbonCounts.values.sum()
  val isFull = totalBonbons == currentTier.size
  val progress = (totalBonbons.toFloat() / currentTier.size.toFloat()).coerceIn(0f, 1f)

  val allFlavors = SanMiguelData.bonbonFlavors
  val filteredFlavors = when (selectedFilter) {
    "Con Leche" -> allFlavors.filter { it.category == "milk" }
    "Negro / Dark" -> allFlavors.filter { it.category == "dark" }
    "Frutales" -> allFlavors.filter { it.category == "fruit" }
    "Con Licor" -> allFlavors.filter { it.category == "liqueur" }
    else -> allFlavors
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
        // Encabezado
        Text(
          text = "Arma tu Cofre de Bombones",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = ChocolatePrimaryContainer
        )
        Text(
          text = "Selecciona el tamaño de tu cofre de lujo y elige tus joyas artesanales de cacao peruano.",
          style = MaterialTheme.typography.bodyMedium,
          color = OnSurfaceVariant,
          modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // PASO 1: Elegir tamaño
        Text(
          text = "PASO 1: ELIGE EL TAMAÑO DE TU COFRE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          tiers.forEach { tier ->
            BoxTierCard(
              tier = tier,
              isSelected = tier.size == currentTier.size,
              modifier = Modifier.weight(1f),
              onClick = { onSelectTier(tier.size) }
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PASO 2: Visualizador del cofre y progreso
        Text(
          text = "PASO 2: SELECCIONA TUS SABORES",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isFull) SecondaryFixed.copy(alpha = 0.35f) else CreamSurfaceContainerLowest
          ),
          border = BorderStroke(
            1.5.dp,
            if (isFull) GoldSecondary else CreamSurfaceContainerHigh
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isFull) GoldSecondaryContainer else CreamSurfaceContainerLow),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isFull) Icons.Default.Check else Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = if (isFull) OnGoldSecondaryContainer else GoldSecondary,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "${currentTier.name} (${currentTier.size} piezas)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ChocolatePrimaryContainer
                  )
                  Text(
                    text = "Llevas $totalBonbons de ${currentTier.size} bombones",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = if (isFull) GoldSecondary else OnSurfaceVariant
                  )
                }
              }

              if (totalBonbons > 0) {
                TextButton(
                  onClick = onClearBox,
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = OutlineGrey,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Vaciar",
                    style = MaterialTheme.typography.labelSmall,
                    color = OutlineGrey
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(CreamSurfaceContainerHigh)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(progress)
                  .height(8.dp)
                  .clip(CircleShape)
                  .background(if (isFull) GoldSecondary else ChocolatePrimaryContainer)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje de estado
            Text(
              text = when {
                totalBonbons == 0 -> "Toca el botón + en los sabores que deseas incluir en tu cofre."
                totalBonbons < currentTier.size -> "Faltan ${currentTier.size - totalBonbons} bombones para completar tu ${currentTier.name}."
                isFull -> "✨ ¡Cofre completo! Todos los espacios están llenos con tus bombones favoritos."
                else -> "Has seleccionado ${totalBonbons - currentTier.size} bombones de más para este cofre."
              },
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isFull) FontWeight.Bold else FontWeight.Normal
              ),
              color = if (isFull) ChocolatePrimaryContainer else OnSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtro de categorías
        val filterOptions = listOf("Todos", "Con Leche", "Negro / Dark", "Frutales", "Con Licor")
        val filterScrollState = rememberScrollState()
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(filterScrollState),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          filterOptions.forEach { filter ->
            val isSelected = selectedFilter == filter
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) ChocolatePrimaryContainer else CreamSurfaceContainerLowest)
                .clickable { selectedFilter = filter }
                .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
              Text(
                text = filter,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) CreamSurface else OnSurfaceDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Lista de Sabores
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          filteredFlavors.forEach { flavor ->
            val count = uiState.bonbonCounts[flavor.id] ?: 0
            FlavorItemCard(
              flavor = flavor,
              count = count,
              onMinus = { onModifyCount(flavor.id, -1) },
              onPlus = { onModifyCount(flavor.id, 1) }
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón inferior para añadir al carrito
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("confirm_box_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          border = BorderStroke(1.dp, GoldSecondaryContainer.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = currentTier.name,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold
                ),
                color = ChocolatePrimaryContainer
              )
              Text(
                text = "$totalBonbons de ${currentTier.size} bombones elegidos",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
              )
              Text(
                text = "S/ ${String.format(Locale.US, "%.2f", currentTier.price)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = GoldSecondary
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
              onClick = onConfirm,
              enabled = totalBonbons > 0,
              modifier = Modifier
                .height(52.dp)
                .testTag("confirm_box"),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isFull) ChocolatePrimaryContainer else GoldSecondary
              ),
              shape = RoundedCornerShape(26.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = CreamSurface,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isFull) "Añadir a la Bolsa" else "Completar (${totalBonbons}/${currentTier.size})",
                fontWeight = FontWeight.Bold,
                color = CreamSurface
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun BoxTierCard(
  tier: BoxTier,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val containerColor by animateColorAsState(
    targetValue = if (isSelected) ChocolatePrimaryContainer else CreamSurfaceContainerLowest,
    label = "tierContainer"
  )
  val textColor = if (isSelected) CreamSurface else OnSurfaceDark
  val subtextColor = if (isSelected) CreamSurfaceContainerHigh else OnSurfaceVariant
  val priceColor = if (isSelected) GoldSecondaryContainer else GoldSecondary

  Card(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag("tier_btn_${tier.size}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(
      1.5.dp,
      if (isSelected) GoldSecondaryContainer else CreamSurfaceContainerHigh
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (tier.isFavorite) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = GoldSecondaryContainer,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "FAVORITO",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            ),
            color = if (isSelected) GoldSecondaryContainer else GoldSecondary
          )
        }
      } else {
        Spacer(modifier = Modifier.height(14.dp))
      }

      Text(
        text = "${tier.size} Uds",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = textColor
      )

      Text(
        text = tier.name,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = subtextColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "S/ ${String.format(Locale.US, "%.2f", tier.price)}",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = priceColor
      )
    }
  }
}

@Composable
private fun FlavorItemCard(
  flavor: BonbonFlavor,
  count: Int,
  onMinus: () -> Unit,
  onPlus: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    border = if (count > 0) BorderStroke(1.5.dp, GoldSecondaryContainer) else null
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      SanMiguelImage(
        model = flavor.imageUrl,
        contentDescription = flavor.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(60.dp)
          .clip(RoundedCornerShape(10.dp))
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = flavor.name,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = OnSurfaceDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          flavor.tag?.let { tagText ->
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(GoldSecondaryContainer.copy(alpha = 0.5f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = tagText,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = OnGoldSecondaryContainer,
                fontWeight = FontWeight.Bold
              )
            }
          }

          flavor.alcoholTag?.let { tagText ->
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(CreamSurfaceContainerHigh)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = tagText,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = GoldSecondary,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = flavor.description,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
          color = OnSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = flavor.notes,
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium
          ),
          color = GoldSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Stepper
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(CircleShape)
          .background(CreamSurfaceContainerLow)
          .padding(3.dp)
      ) {
        IconButton(
          onClick = onMinus,
          enabled = count > 0,
          modifier = Modifier
            .size(32.dp)
            .testTag("minus_flavor_${flavor.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Remove,
            contentDescription = "Restar",
            tint = if (count > 0) ChocolatePrimaryContainer else OutlineGrey,
            modifier = Modifier.size(16.dp)
          )
        }

        Text(
          text = "$count",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = if (count > 0) ChocolatePrimaryContainer else OnSurfaceVariant,
          modifier = Modifier
            .width(26.dp)
            .testTag("count_flavor_${flavor.id}"),
          textAlign = TextAlign.Center
        )

        IconButton(
          onClick = onPlus,
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(ChocolatePrimaryContainer)
            .testTag("plus_flavor_${flavor.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Sumar",
            tint = CreamSurface,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
