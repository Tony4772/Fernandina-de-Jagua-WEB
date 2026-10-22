package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.SanMiguelData
import com.example.model.Product
import com.example.model.StorefrontCopy
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
import com.example.ui.theme.SecondaryFixedDim
import com.example.viewmodel.ShopUiState
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
  uiState: ShopUiState,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit,
  onToggleFavorite: (String) -> Unit,
  onFilterSelect: (String) -> Unit,
  onOpenSustainability: () -> Unit,
  onNavigateToBuilder: () -> Unit
) {
  val scrollState = rememberScrollState()
  var productsY by remember { mutableIntStateOf(0) }
  val scope = rememberCoroutineScope()
  val showProducts: () -> Unit = {
    onFilterSelect("Todos")
    scope.launch { scrollState.animateScrollTo(productsY) }
  }
  val openCategory: (String) -> Unit = { name ->
    onFilterSelect(name)
    scope.launch { scrollState.animateScrollTo(productsY) }
  }

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(CreamBackground)
  ) {
    val isTablet = maxWidth >= 600.dp
    val isDesktopOrUltraWide = maxWidth >= 900.dp
    val productColumns = if (isDesktopOrUltraWide) 4 else if (isTablet) 3 else 2
    val contentMaxWidth = if (isDesktopOrUltraWide) 920.dp else if (isTablet) 720.dp else maxWidth

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
        .padding(bottom = 96.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = contentMaxWidth)
      ) {
        // 1. Subtle Ambient Salutation
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            if (uiState.storefront.eyebrow.isNotBlank()) {
              Text(
                text = uiState.storefront.eyebrow,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.2.sp
                ),
                color = GoldSecondary
              )
            }
            if (uiState.storefront.headline.isNotBlank()) {
              Text(
                text = uiState.storefront.headline,
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontFamily = FontFamily.Serif,
                  fontStyle = FontStyle.Italic,
                  fontWeight = FontWeight.Medium
                ),
                color = ChocolatePrimaryContainer
              )
            }
          }

          if (uiState.storefront.cornerBadge.isNotBlank()) {
            Row(
              modifier = Modifier
                .clip(CircleShape)
                .background(CreamSurfaceContainerHigh)
                .padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = GoldSecondary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = uiState.storefront.cornerBadge,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = ChocolatePrimaryContainer
              )
            }
          }
        }

        if (uiState.storefront.heroTitle.isNotBlank()) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("hero_seasonal_banner"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = ChocolatePrimaryContainer),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(if (isTablet) 300.dp else 260.dp)
          ) {
            // Background Image (Lazy Loaded)
            SanMiguelImage(
              model = uiState.storefront.heroImageUrl.ifBlank { SanMiguelData.HERO_IMAGE_URL },
              contentDescription = uiState.storefront.heroTitle,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )

            // Gradient Scrim
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      Color.Transparent,
                      ChocolatePrimaryContainer.copy(alpha = 0.75f),
                      ChocolatePrimaryContainer
                    ),
                    startY = 60f
                  )
                )
            )

            // Content Overlay
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(if (isTablet) 24.dp else 16.dp),
              verticalArrangement = Arrangement.Bottom
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
              ) {
                if (uiState.storefront.heroBadge.isNotBlank()) {
                  Row(
                    modifier = Modifier
                      .clip(CircleShape)
                      .background(GoldSecondaryContainer)
                      .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = null,
                      tint = OnGoldSecondaryContainer,
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                      text = uiState.storefront.heroBadge,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = OnGoldSecondaryContainer
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                }
                if (uiState.storefront.heroSeason.isNotBlank()) {
                  Text(
                    text = uiState.storefront.heroSeason,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryFixed.copy(alpha = 0.9f)
                  )
                }
              }

              Text(
                text = uiState.storefront.heroTitle,
                style = MaterialTheme.typography.headlineMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = if (isTablet) 26.sp else 22.sp
                ),
                color = CreamSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )

              if (uiState.storefront.heroBody.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = uiState.storefront.heroBody,
                  style = MaterialTheme.typography.bodySmall,
                  color = CreamSurfaceContainerHigh,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  if (uiState.storefront.heroPriceCaption.isNotBlank()) {
                    Text(
                      text = uiState.storefront.heroPriceCaption,
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                      color = SecondaryFixedDim
                    )
                  }
                  if (uiState.storefront.heroPrice.isNotBlank()) {
                    Text(
                      text = uiState.storefront.heroPrice,
                      style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                      ),
                      color = SecondaryFixed
                    )
                  }
                }

                if (uiState.storefront.heroButton.isNotBlank()) {
                  Button(
                  onClick = showProducts,
                  colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
                  shape = RoundedCornerShape(20.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 18.dp,
                    vertical = 10.dp
                  ),
                  modifier = Modifier.testTag("explore_collection_button")
                ) {
                  Text(
                    text = uiState.storefront.heroButton,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = CreamSurface
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = CreamSurface,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
        }
      }
        Spacer(modifier = Modifier.height(14.dp))

        // Banner Destacado: Arma tu Caja de Bombones
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onNavigateToBuilder() }
            .testTag("banner_armar_caja"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = SecondaryFixed.copy(alpha = 0.35f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondaryContainer.copy(alpha = 0.7f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(ChocolatePrimaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = GoldSecondaryContainer,
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Arma tu Caja de Bombones",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold
                ),
                color = ChocolatePrimaryContainer
              )
              Text(
                text = "Elige tus sabores favoritos y personaliza tu estuche a tu medida.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = onNavigateToBuilder,
              colors = ButtonDefaults.buttonColors(containerColor = ChocolatePrimaryContainer),
              shape = RoundedCornerShape(20.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
              Text("Armar", color = CreamSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Horizontal Category Carousel
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .width(5.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(GoldSecondary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = uiState.storefront.categoriesHeading.ifBlank { "Categorías" },
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = OnSurfaceDark
            )
          }

          Text(
            text = "Ver todas",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = GoldSecondary,
            modifier = Modifier.clickable(onClick = showProducts)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
          items(uiState.visibleCategories, key = { it.id }) { category ->
            val selected = uiState.activeFilter == category.name
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .width(84.dp)
                .clickable { openCategory(category.name) }
            ) {
              Box(
                modifier = Modifier
                  .size(68.dp)
                  .clip(CircleShape)
                  .border(
                    width = if (selected) 3.dp else 0.dp,
                    color = GoldSecondary,
                    shape = CircleShape
                  )
                  .background(CreamSurfaceContainerHigh)
                  .padding(2.dp)
              ) {
                SanMiguelImage(
                  model = category.imageUrl,
                  contentDescription = category.name,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = category.name,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 11.sp
                ),
                color = OnSurfaceDark,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
              productsY = coordinates.positionInParent().y.roundToInt()
            }
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Text(
            text = uiState.storefront.productsHeading.ifBlank { "Productos" },
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = ChocolatePrimaryContainer
          )
          if (uiState.storefront.productsNote.isNotBlank()) {
            Text(
              text = uiState.storefront.productsNote,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              ),
              color = GoldSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val displayProducts = if (uiState.activeFilter == "Todos") {
          uiState.catalog
        } else {
          uiState.catalog.filter { it.category == uiState.activeFilter }
        }

        if (displayProducts.isEmpty()) {
          Text(
            text = "No hay productos en esta categoría.",
            color = OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
          )
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          val chunked = displayProducts.chunked(productColumns)
          chunked.forEach { rowItems ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              rowItems.forEach { product ->
                Box(modifier = Modifier.weight(1f)) {
                  ProductCard(
                    product = product,
                    isFavorite = uiState.favorites.contains(product.id),
                    onCardClick = { onProductClick(product) },
                    onAddToCart = { onAddToCart(product) },
                    onToggleFavorite = { onToggleFavorite(product.id) }
                  )
                }
              }
              // Fill remaining spaces in row
              repeat(productColumns - rowItems.size) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.storefront.sealTitle.isNotBlank()) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              // Seal bar
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(CreamSurfaceContainerLow)
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  Image(
                    painter = painterResource(R.drawable.logo_round),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = uiState.storefront.sealTitle,
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = ChocolatePrimaryContainer
                    )
                    Text(
                      text = uiState.storefront.sealBody,
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                      color = OnSurfaceVariant
                    )
                  }
                }

                TextButton(
                  onClick = onOpenSustainability,
                  modifier = Modifier.testTag("learn_more_sustainability_button")
                ) {
                  Text(
                    text = uiState.storefront.sealButton,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldSecondary
                  )
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Horario, Dirección y Contacto de la Tienda
        StoreLocationCard(storefront = uiState.storefront)

        Spacer(modifier = Modifier.height(24.dp))

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Image(
            painter = painterResource(R.drawable.logo_wordmark),
            contentDescription = "Fernandina de Jagua Chocolates",
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .fillMaxWidth(0.75f)
              .heightIn(min = 180.dp, max = 240.dp)
              .padding(horizontal = 8.dp)
          )
          if (uiState.storefront.footerLine.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
              modifier = Modifier
                .width(36.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(GoldSecondary.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = uiState.storefront.footerLine,
              style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 0.4.sp,
                fontSize = 10.sp
              ),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              modifier = Modifier.padding(horizontal = 24.dp),
              color = OutlineGrey
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProductCard(
  product: Product,
  isFavorite: Boolean,
  onCardClick: () -> Unit,
  onAddToCart: () -> Unit,
  onToggleFavorite: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onCardClick)
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // 4/5 Aspect Ratio Image with Badge and Favorite Icon (Lazy Loaded)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(4f / 5f)
          .clip(RoundedCornerShape(10.dp))
          .background(CreamSurfaceContainer)
      ) {
        SanMiguelImage(
          model = product.imageUrl,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Badge
        product.badge?.let { badgeText ->
          val isFavBadge = badgeText == "Favorito"
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(6.dp)
              .clip(CircleShape)
              .background(
                if (isFavBadge) GoldSecondary else CreamSurfaceContainerHigh.copy(alpha = 0.9f)
              )
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = badgeText,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              ),
              color = if (isFavBadge) CreamSurface else ChocolatePrimaryContainer
            )
          }
        }

        // Heart Icon
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(4.dp)
            .size(30.dp)
            .clip(CircleShape)
            .background(CreamSurfaceContainerLowest.copy(alpha = 0.85f))
            .testTag("fav_btn_${product.id}")
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorito",
            tint = if (isFavorite) ErrorRed else OnSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Origen / Categoría artesanal
      Text(
        text = product.origin.ifBlank { product.category },
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Medium,
          fontSize = 10.5.sp
        ),
        color = GoldSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Title
      Text(
        text = product.name,
        style = MaterialTheme.typography.titleSmall.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold,
          fontSize = 13.5.sp
        ),
        color = ChocolatePrimaryContainer,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 17.sp
      )

      Text(
        text = product.subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = OutlineGrey,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Price and Add Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "S/ ${String.format(java.util.Locale.US, "%.2f", product.price)}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer
          )
          Text(
            text = if (product.price > 10.0) "Estuche de lujo" else "Por unidad",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = OutlineGrey
          )
        }

        IconButton(
          onClick = onAddToCart,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(ChocolatePrimaryContainer)
            .testTag("add_product_${product.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Añadir al carrito",
            tint = CreamSurfaceContainerLowest,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun StoreLocationCard(
  storefront: StorefrontCopy
) {
  val context = LocalContext.current

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurfaceContainerLowest),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
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
              .background(GoldSecondaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Storefront,
              contentDescription = null,
              tint = OnGoldSecondaryContainer,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Nuestra Chocolatería",
              style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
              ),
              color = ChocolatePrimaryContainer
            )
            Text(
              text = "Chocolatería Artesanal & Tienda en San Miguel",
              style = MaterialTheme.typography.labelSmall,
              color = OnSurfaceVariant
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GoldSecondaryContainer.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "San Miguel, Lima",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = OnGoldSecondaryContainer
          )
        }
      }

      // Dirección
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CreamSurfaceContainerLow)
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Place,
          contentDescription = null,
          tint = GoldSecondary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Dirección",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer
          )
          Text(
            text = storefront.storeAddress,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceDark
          )
        }
        TextButton(
          onClick = {
            try {
              val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(storefront.storeAddress))
              val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
              context.startActivity(mapIntent)
            } catch (_: Exception) {
              val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=" + Uri.encode(storefront.storeAddress)))
              context.startActivity(browserIntent)
            }
          }
        ) {
          Text("Cómo llegar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldSecondary)
        }
      }

      // Horario de Atención
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CreamSurfaceContainerLow)
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Schedule,
          contentDescription = null,
          tint = GoldSecondary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Horario de Atención",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer
          )
          Text(
            text = storefront.storeHours,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceDark
          )
        }
      }

      // Teléfono / WhatsApp
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CreamSurfaceContainerLow)
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Phone,
          contentDescription = null,
          tint = GoldSecondary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Teléfono y Pedidos",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = ChocolatePrimaryContainer
          )
          Text(
            text = storefront.storePhone,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceDark
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
          TextButton(
            onClick = {
              try {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${storefront.storePhone.replace(" ", "")}"))
                context.startActivity(callIntent)
              } catch (_: Exception) {}
            }
          ) {
            Text("Llamar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChocolatePrimaryContainer)
          }

          val digitsOnly = storefront.storePhone.filter { it.isDigit() }
          if (digitsOnly.isNotBlank()) {
            TextButton(
              onClick = {
                try {
                  val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$digitsOnly"))
                  context.startActivity(waIntent)
                } catch (_: Exception) {}
              }
            ) {
              Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldSecondary)
            }
          }
        }
      }
    }
  }
}
