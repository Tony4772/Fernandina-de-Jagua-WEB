package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.TaxReceipts
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.components.OrderSuccessDialog
import com.example.ui.components.SearchDialog
import com.example.ui.components.SustainabilityBottomSheet
import com.example.ui.screens.BoxBuilderScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ShopAdminScreen
import com.example.ui.splash.EbyzomSplash
import com.example.ui.theme.ChocolatePrimaryContainer
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CurrentScreen
import com.example.viewmodel.ShopViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      var showSplash by remember { mutableStateOf(true) }
      if (showSplash) {
        EbyzomSplash(
          appName = "Fernandina de Jagua Chocolates",
          tagline = "El arte del cacao puro",
          logo = painterResource(R.drawable.logo_wordmark),
          paper = CreamBackground,
          ink = ChocolatePrimaryContainer,
          inkSoft = GoldSecondary,
          onFinished = { showSplash = false },
        )
      } else {
        val view = LocalView.current
        val darkTheme = isSystemInDarkTheme()
        if (!view.isInEditMode) {
          SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
              isAppearanceLightStatusBars = !darkTheme
              isAppearanceLightNavigationBars = !darkTheme
            }
          }
        }
        MyApplicationTheme {
          MainAppContent()
        }
      }
    }
  }
}

@Composable
fun MainAppContent(viewModel: ShopViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val savePdf = rememberLauncherForActivityResult(
    ActivityResultContracts.CreateDocument("application/pdf")
  ) { uri ->
    val pdf = viewModel.currentReceiptPdf()
    if (uri != null && pdf != null) {
      context.contentResolver.openOutputStream(uri)?.use { output -> output.write(pdf) }
    }
  }

  // Handle hardware back button
  val isNotHome = uiState.currentScreen !is CurrentScreen.Home
  BackHandler(enabled = isNotHome) {
    if (!viewModel.navigateBack()) {
      viewModel.navigateTo(CurrentScreen.Home)
    }
  }

  // Clear toast after delay
  LaunchedEffect(uiState.toastMessage) {
    if (uiState.toastMessage != null) {
      delay(2500)
      viewModel.clearToast()
    }
  }

  val isDetail = uiState.currentScreen is CurrentScreen.ProductDetail

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(CreamBackground),
    topBar = {
      AppTopBar(
        isDetailScreen = isDetail,
        title = if (isDetail) "Detalle De Bombón" else "Fernandina de Jagua Chocolates",
        onBackClick = {
          if (!viewModel.navigateBack()) {
            viewModel.navigateTo(CurrentScreen.Home)
          }
        },
        onSearchClick = { viewModel.toggleSearch(true) },
        onShareClick = {
          val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
              Intent.EXTRA_TEXT,
              "Descubre los bombones de autor de Fernandina de Jagua Chocolates. Recojo en ${uiState.storefront.storeAddress}."
            )
            type = "text/plain"
          }
          val shareIntent = Intent.createChooser(sendIntent, "Compartir con")
          context.startActivity(shareIntent)
        },
        onProfileClick = { viewModel.navigateTo(CurrentScreen.Profile) }
      )
    },
    bottomBar = {
      AppBottomNav(
        currentScreen = uiState.currentScreen,
        cartBadgeCount = uiState.cartItems.sumOf { it.quantity },
        onNavigate = { viewModel.navigateTo(it) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (val screen = uiState.currentScreen) {
        is CurrentScreen.Home -> {
          HomeScreen(
            uiState = uiState,
            onProductClick = { viewModel.openProductDetail(it) },
            onAddToCart = { viewModel.addProductToCart(it) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onFilterSelect = { viewModel.setFilter(it) },
            onOpenSustainability = { viewModel.toggleSustainability(true) },
            onNavigateToBuilder = { viewModel.navigateTo(CurrentScreen.BoxBuilder) }
          )
        }

        is CurrentScreen.BoxBuilder -> {
          BoxBuilderScreen(
            uiState = uiState,
            onSelectTier = { viewModel.selectBoxTier(it) },
            onModifyCount = { id, delta -> viewModel.modifyBonbonCount(id, delta) },
            onClearBox = { viewModel.clearBox() },
            onConfirm = { viewModel.confirmBoxToCart() }
          )
        }

        is CurrentScreen.Cart -> {
          CartScreen(
            uiState = uiState,
            onUpdateQuantity = { id, delta -> viewModel.updateCartQuantity(id, delta) },
            onRemoveItem = { viewModel.removeCartItem(it) },
            onSelectDeliveryMode = { viewModel.setDeliveryMode(it) },
            onSelectTimeSlot = { viewModel.setTimeSlot(it) },
            onToggleGiftWrap = { viewModel.setGiftWrap(it) },
            onUpdateGiftMessage = { viewModel.setGiftCardMessage(it) },
            onSelectPayment = { viewModel.setPaymentMethod(it) },
            onCheckout = { card, yape -> viewModel.processOrder(card, yape) },
            onNavigateToBuilder = { viewModel.navigateTo(CurrentScreen.Home) }
          )
        }

        is CurrentScreen.ProductDetail -> {
          ProductDetailScreen(
            product = screen.product,
            uiState = uiState,
            onGallerySelect = { viewModel.setDetailGalleryIndex(it) },
            onChangeQuantity = { viewModel.changeDetailQuantity(it) },
            onToggleRibbon = { viewModel.toggleDetailRibbon(it) },
            onToggleCard = { viewModel.toggleDetailCard(it) },
            onCardMessageChange = { viewModel.setDetailCardMessage(it) },
            onToggleIngredients = { viewModel.toggleDetailIngredients() },
            onToggleShipping = { viewModel.toggleDetailShipping() },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onAddToCart = { viewModel.addDetailToCart(screen.product) }
          )
        }

        is CurrentScreen.Profile -> {
          ProfileScreen(
            uiState = uiState,
            onProductClick = { viewModel.openProductDetail(it) },
            onNavigateToBuilder = { viewModel.navigateTo(CurrentScreen.Home) },
            onRegister = { name, email, password -> viewModel.register(name, email, password) },
            onSignIn = { email, password -> viewModel.signIn(email, password) },
            onGoogleSignIn = { viewModel.signInWithGoogle(it) },
            onAuthMessage = { viewModel.showToast(it) },
            onSignOut = { viewModel.signOut() },
            onOpenShop = { viewModel.navigateTo(CurrentScreen.ShopAdmin) },
            onDeleteAccount = { viewModel.deleteAccount {} }
          )
        }

        is CurrentScreen.ShopAdmin -> {
          ShopAdminScreen(
            uiState = uiState,
            onBack = { viewModel.navigateTo(CurrentScreen.Profile) },
            onEnsureCatalog = { viewModel.ensureCatalog() },
            onEnsureStorefront = { viewModel.ensureStorefront() },
            onSave = { viewModel.saveProduct(it) },
            onDelete = { viewModel.deleteProduct(it) },
            onUploadImage = { productId, uri, onUploaded ->
              viewModel.uploadProductImage(productId, uri, onUploaded)
            },
            onSaveCopy = { viewModel.saveStorefront(it) },
            onSaveBoxTiers = { viewModel.saveBoxTiers(it) },
            onSaveCategory = { viewModel.saveCategory(it) },
            onDeleteCategory = { viewModel.deleteCategory(it) },
            onUploadStoreImage = { folder, uri, onUploaded ->
              viewModel.uploadStoreImage(folder, uri, onUploaded)
            }
          )
        }
      }

      // Custom Amber Toast notification pill
      AnimatedVisibility(
        visible = uiState.toastMessage != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp)
      ) {
        uiState.toastMessage?.let { msg ->
          Surface(
            shape = CircleShape,
            color = ChocolatePrimaryContainer,
            shadowElevation = 8.dp
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = GoldSecondaryContainer,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = msg,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = Color.White
              )
            }
          }
        }
      }
    }
  }

  // Search Dialog
  if (uiState.isSearchOpen) {
    SearchDialog(
      searchQuery = uiState.searchQuery,
      products = uiState.catalog,
      onQueryChange = { viewModel.setSearchQuery(it) },
      onProductClick = { viewModel.openProductDetail(it) },
      onDismiss = {
        viewModel.toggleSearch(false)
        viewModel.setSearchQuery("")
      }
    )
  }

  // Sustainability Modal Bottom Sheet
  if (uiState.isSustainabilityOpen) {
    SustainabilityBottomSheet(
      onDismiss = { viewModel.toggleSustainability(false) }
    )
  }

  // Order Success Celebration Dialog
  if (uiState.isOrderSuccessOpen) {
    OrderSuccessDialog(
      orderId = uiState.orderPlacedId,
      customerName = uiState.accountName.orEmpty(),
      receiptBusy = uiState.receiptBusy,
      receiptLabel = uiState.receiptLabel,
      receiptError = uiState.receiptError,
      onRequestReceipt = { kind, name, document -> viewModel.requestReceipt(kind, name, document) },
      onDownload = {
        val label = uiState.receiptLabel ?: "comprobante"
        savePdf.launch("$label.pdf")
      },
      onPrint = {
        val pdf = viewModel.currentReceiptPdf()
        val label = uiState.receiptLabel ?: "Comprobante"
        if (pdf != null) {
          TaxReceipts.print(context, label, pdf)
        }
      },
      onDismiss = { viewModel.dismissOrderSuccess() }
    )
  }
}
