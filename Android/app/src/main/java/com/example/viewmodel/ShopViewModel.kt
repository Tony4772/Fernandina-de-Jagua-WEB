package com.example.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AccountRepository
import com.example.data.CatalogRepository
import com.example.data.CulqiPayments
import com.example.data.OrderRepository
import com.example.data.SanMiguelData
import com.example.data.ShopOwner
import com.example.data.StorefrontRepository
import com.example.data.TaxReceipts
import com.example.model.BoxTier
import com.example.model.CardCheckout
import com.example.model.CartItem
import com.example.model.DeliveryMode
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.ShopCategory
import com.example.model.StorefrontCopy
import com.example.model.YapeCheckout
import kotlin.math.roundToInt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CurrentScreen {
  data object Home : CurrentScreen
  data object BoxBuilder : CurrentScreen
  data object Cart : CurrentScreen
  data class ProductDetail(val product: Product) : CurrentScreen
  data object Profile : CurrentScreen
  data object ShopAdmin : CurrentScreen
}

data class ShopUiState(
  val currentScreen: CurrentScreen = CurrentScreen.Home,
  val navigationHistory: List<CurrentScreen> = listOf(CurrentScreen.Home),
  val activeFilter: String = "Todos",
  val searchQuery: String = "",
  val isSearchOpen: Boolean = false,
  val isSustainabilityOpen: Boolean = false,
  val isOrderSuccessOpen: Boolean = false,
  val orderPlacedId: String = "",
  val paidChargeId: String = "",
  val receiptBusy: Boolean = false,
  val receiptLabel: String? = null,
  val receiptError: String? = null,
  val favorites: Set<String> = emptySet(),
  val accountName: String? = null,
  val accountEmail: String? = null,
  val accountBusy: Boolean = false,
  val storeProducts: List<Product> = emptyList(),
  val catalogSynced: Boolean = false,
  val hadStoreProducts: Boolean = false,
  val storeCategories: List<ShopCategory> = emptyList(),
  val categoriesSynced: Boolean = false,
  val hadCategories: Boolean = false,
  val storefront: StorefrontCopy = StorefrontCopy(),

  val storeBoxTiers: List<BoxTier> = emptyList(),
  val bonbonCounts: Map<String, Int> = emptyMap(),
  val selectedTierSize: Int = 12,

  // Cart state
  val cartItems: List<CartItem> = emptyList(),
  val deliveryMode: DeliveryMode = DeliveryMode.REFRIGERATED,
  val paymentMethod: PaymentMethod = PaymentMethod.YAPE,
  val orderInProgress: Boolean = false,
  val selectedTimeSlot: String = "Mañana (10:00 - 14:00)",
  val giftWrapEnabled: Boolean = true,
  val giftCardMessage: String = "Para celebrar tus logros con la dulzura que mereces. Con todo mi cariño y admiración siempre.",

  // Product detail state
  val detailQuantity: Int = 1,
  val detailGalleryIndex: Int = 0,
  val detailRibbonSelected: Boolean = true,
  val detailCardSelected: Boolean = false,
  val detailCardMessage: String = "",
  val detailIngredientsExpanded: Boolean = false,
  val detailShippingExpanded: Boolean = false,

  // Toast / feedback message
  val toastMessage: String? = null
) {
  val boxCurrentTotal: Int get() = bonbonCounts.values.sum()
  val activeBoxTiers: List<BoxTier>
    get() = if (storeBoxTiers.isNotEmpty()) storeBoxTiers else SanMiguelData.boxTiers

  val selectedBoxTier: BoxTier
    get() = activeBoxTiers.find { it.size == selectedTierSize } ?: activeBoxTiers.firstOrNull() ?: SanMiguelData.boxTiers[1]

  val cartSubtotal: Double
    get() = cartItems.sumOf { it.totalPrice }

  val giftWrapPrice: Double
    get() = if (giftWrapEnabled) 3.00 else 0.00

  val shippingPrice: Double
    get() = 0.0 // Always free climate-controlled shipping in this campaign

  val cartTotal: Double
    get() = cartSubtotal + giftWrapPrice + shippingPrice

  val freeShippingGoal: Double = 65.00
  val remainingForFreeShipping: Double
    get() = (freeShippingGoal - cartSubtotal).coerceAtLeast(0.0)

  val freeShippingProgress: Float
    get() = (cartSubtotal / freeShippingGoal).coerceIn(0.0, 1.0).toFloat()

  val isShopOwner: Boolean
    get() = accountEmail.equals(ShopOwner.EMAIL, ignoreCase = true)

  val catalog: List<Product>
    get() = when {
      storeProducts.isNotEmpty() -> storeProducts.filter { it.visible }
      hadStoreProducts -> emptyList()
      else -> SanMiguelData.products
    }

  val editableProducts: List<Product>
    get() = if (storeProducts.isNotEmpty() || hadStoreProducts) storeProducts else SanMiguelData.products

  val visibleCategories: List<ShopCategory>
    get() = when {
      storeCategories.isNotEmpty() -> storeCategories
      hadCategories -> emptyList()
      else -> SanMiguelData.categories.mapIndexed { index, pair ->
        ShopCategory("local_$index", pair.first, pair.second, index)
      }
    }
}

class ShopViewModel : ViewModel() {
  private var receiptPdf: ByteArray? = null
  private val auth = FirebaseAuth.getInstance()
  private val _uiState = MutableStateFlow(ShopUiState())
  val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()
  private var catalogJob: Job? = null
  private var storefrontJob: Job? = null

  private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
    val user = firebaseAuth.currentUser
    val owner = user != null && !user.isAnonymous &&
      user.email.equals(ShopOwner.EMAIL, ignoreCase = true)
    watchCatalog(owner)
    if (user == null || user.isAnonymous) {
      _uiState.update {
        it.copy(
          accountName = null,
          accountEmail = null,
          favorites = emptySet(),
          accountBusy = false
        )
      }
    } else {
      viewModelScope.launch {
        loadAccount(user.uid, user.email.orEmpty(), user.displayName.orEmpty())
      }
    }
  }

  init {
    auth.addAuthStateListener(authListener)
    watchStorefront()
  }

  private fun watchCatalog(includeHidden: Boolean) {
    catalogJob?.cancel()
    catalogJob = viewModelScope.launch {
      CatalogRepository.observe(includeHidden).collect { products ->
        _uiState.update {
          it.copy(
            storeProducts = products,
            catalogSynced = true,
            hadStoreProducts = it.hadStoreProducts || products.isNotEmpty()
          )
        }
      }
    }
  }

  private fun watchStorefront() {
    storefrontJob?.cancel()
    storefrontJob = viewModelScope.launch {
      launch {
        StorefrontRepository.observeCategories().collect { categories ->
          _uiState.update {
            val filterStillExists = it.activeFilter == "Todos" ||
              categories.isEmpty() ||
              categories.any { category -> category.name == it.activeFilter }
            it.copy(
              storeCategories = categories,
              categoriesSynced = true,
              hadCategories = it.hadCategories || categories.isNotEmpty(),
              activeFilter = if (filterStillExists) it.activeFilter else "Todos"
            )
          }
        }
      }
      launch {
        StorefrontRepository.observeCopy().collect { copy ->
          _uiState.update { it.copy(storefront = copy) }
        }
      }
      launch {
        StorefrontRepository.observeBoxTiers().collect { tiers ->
          _uiState.update { it.copy(storeBoxTiers = tiers) }
        }
      }
    }
  }

  override fun onCleared() {
    catalogJob?.cancel()
    storefrontJob?.cancel()
    auth.removeAuthStateListener(authListener)
    super.onCleared()
  }

  fun navigateTo(screen: CurrentScreen) {
    _uiState.update { state ->
      val newHistory = if (state.currentScreen != screen) {
        state.navigationHistory + screen
      } else {
        state.navigationHistory
      }
      state.copy(
        currentScreen = screen,
        navigationHistory = newHistory
      )
    }
  }

  fun navigateBack(): Boolean {
    val state = _uiState.value
    if (state.navigationHistory.size > 1) {
      val newHistory = state.navigationHistory.dropLast(1)
      _uiState.update {
        it.copy(
          currentScreen = newHistory.last(),
          navigationHistory = newHistory
        )
      }
      return true
    }
    return false
  }

  fun setFilter(filter: String) {
    _uiState.update { it.copy(activeFilter = filter) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun toggleSearch(isOpen: Boolean) {
    _uiState.update { it.copy(isSearchOpen = isOpen) }
  }

  fun toggleSustainability(isOpen: Boolean) {
    _uiState.update { it.copy(isSustainabilityOpen = isOpen) }
  }

  fun toggleFavorite(productId: String) {
    val current = _uiState.value
    if (current.accountEmail == null) {
      _uiState.update { it.copy(toastMessage = "Entra a tu cuenta para guardar favoritos") }
      navigateTo(CurrentScreen.Profile)
      return
    }
    val newFavorites = if (current.favorites.contains(productId)) {
      current.favorites - productId
    } else {
      current.favorites + productId
    }
    val isAdded = newFavorites.contains(productId)
    _uiState.update {
      it.copy(
        favorites = newFavorites,
        toastMessage = if (isAdded) "Añadido a tus favoritos" else "Eliminado de favoritos"
      )
    }
    val uid = auth.currentUser?.uid ?: return
    viewModelScope.launch {
      try {
        AccountRepository.setFavorites(uid, newFavorites)
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudieron guardar los favoritos") }
      }
    }
  }

  fun register(name: String, email: String, password: String) {
    val trimmedName = name.trim()
    val trimmedEmail = email.trim()
    if (trimmedName.length < 2) {
      _uiState.update { it.copy(toastMessage = "Escribe tu nombre") }
      return
    }
    if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
      _uiState.update { it.copy(toastMessage = "El correo no es válido") }
      return
    }
    if (password.length < 6) {
      _uiState.update { it.copy(toastMessage = "La contraseña debe tener al menos 6 caracteres") }
      return
    }
    _uiState.update { it.copy(accountBusy = true) }
    viewModelScope.launch {
      try {
        AccountRepository.register(trimmedName, trimmedEmail, password)
      } catch (error: Exception) {
        _uiState.update { it.copy(accountBusy = false, toastMessage = authMessage(error)) }
      }
    }
  }

  fun signInWithGoogle(idToken: String) {
    _uiState.update { it.copy(accountBusy = true) }
    viewModelScope.launch {
      try {
        AccountRepository.signInWithGoogle(idToken)
      } catch (error: Exception) {
        _uiState.update { it.copy(accountBusy = false, toastMessage = authMessage(error)) }
      }
    }
  }

  fun showToast(message: String) {
    _uiState.update { it.copy(toastMessage = message) }
  }

  fun signIn(email: String, password: String) {
    val trimmedEmail = email.trim()
    if (trimmedEmail.isEmpty() || password.isEmpty()) {
      _uiState.update { it.copy(toastMessage = "Escribe tu correo y tu contraseña") }
      return
    }
    _uiState.update { it.copy(accountBusy = true) }
    viewModelScope.launch {
      try {
        AccountRepository.signIn(trimmedEmail, password)
      } catch (error: Exception) {
        _uiState.update { it.copy(accountBusy = false, toastMessage = authMessage(error)) }
      }
    }
  }

  fun signOut() {
    AccountRepository.signOut()
  }

  fun deleteAccount(onComplete: () -> Unit) {
    viewModelScope.launch {
      _uiState.update { it.copy(accountBusy = true) }
      try {
        AccountRepository.deleteAccount()
        _uiState.update {
          it.copy(
            accountBusy = false,
            accountName = null,
            accountEmail = null,
            favorites = emptySet(),
            toastMessage = "Cuenta y datos eliminados correctamente"
          )
        }
        onComplete()
      } catch (error: Exception) {
        _uiState.update {
          it.copy(
            accountBusy = false,
            toastMessage = "No se pudo eliminar la cuenta: ${error.localizedMessage ?: "Error"}"
          )
        }
      }
    }
  }

  fun ensureStorefront() {
    val state = _uiState.value
    if (!state.isShopOwner || !state.categoriesSynced || state.storeCategories.isNotEmpty() || state.hadCategories) {
      return
    }
    viewModelScope.launch {
      try {
        SanMiguelData.categories.forEachIndexed { index, pair ->
          StorefrontRepository.saveCategory(
            ShopCategory(
              id = "c${index + 1}",
              name = pair.first,
              imageUrl = pair.second,
              sort = index
            )
          )
        }
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudieron preparar las categorías") }
      }
    }
  }

  fun saveCategory(category: ShopCategory) {
    if (!_uiState.value.isShopOwner) return
    val name = category.name.trim()
    if (name.isEmpty()) {
      _uiState.update { it.copy(toastMessage = "Escribe el nombre de la categoría") }
      return
    }
    viewModelScope.launch {
      try {
        StorefrontRepository.saveCategory(category.copy(name = name))
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudo guardar la categoría") }
      }
    }
  }

  fun deleteCategory(categoryId: String) {
    if (!_uiState.value.isShopOwner || categoryId.startsWith("local_")) return
    viewModelScope.launch {
      try {
        StorefrontRepository.deleteCategory(categoryId)
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudo eliminar la categoría") }
      }
    }
  }

  fun saveStorefront(copy: StorefrontCopy) {
    if (!_uiState.value.isShopOwner) return
    viewModelScope.launch {
      try {
        StorefrontRepository.saveCopy(copy)
        _uiState.update { it.copy(toastMessage = "Textos guardados") }
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudieron guardar los textos") }
      }
    }
  }

  fun saveBoxTiers(tiers: List<BoxTier>) {
    if (!_uiState.value.isShopOwner) return
    viewModelScope.launch {
      try {
        StorefrontRepository.saveBoxTiers(tiers)
        _uiState.update { it.copy(toastMessage = "Cofres guardados exitosamente") }
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudieron guardar los cofres") }
      }
    }
  }

  fun uploadStoreImage(folder: String, uri: Uri, onUploaded: (String) -> Unit) {
    if (!_uiState.value.isShopOwner) return
    viewModelScope.launch {
      try {
        onUploaded(StorefrontRepository.uploadImage(folder, uri))
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudo subir la imagen") }
      }
    }
  }

  fun ensureCatalog() {
    val state = _uiState.value
    if (!state.isShopOwner || !state.catalogSynced || state.storeProducts.isNotEmpty() || state.hadStoreProducts) {
      return
    }
    viewModelScope.launch {
      try {
        SanMiguelData.products.forEach { CatalogRepository.save(it) }
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudo preparar el catálogo") }
      }
    }
  }

  fun saveProduct(product: Product) {
    if (!_uiState.value.isShopOwner) {
      return
    }
    val name = product.name.trim()
    if (name.length < 2) {
      _uiState.update { it.copy(toastMessage = "Escribe el nombre del producto") }
      return
    }
    if (product.price < 0) {
      _uiState.update { it.copy(toastMessage = "El precio no puede ser negativo") }
      return
    }
    viewModelScope.launch {
      try {
        CatalogRepository.save(
          product.copy(
            name = name,
            subtitle = product.subtitle.trim(),
            description = product.description.trim(),
            category = product.category.trim().ifBlank { "Bombones" },
            origin = product.origin.trim(),
            badge = product.badge?.trim()?.ifBlank { null }
          )
        )
        _uiState.update { it.copy(toastMessage = "Producto guardado") }
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudo guardar el producto") }
      }
    }
  }

  fun deleteProduct(productId: String) {
    if (!_uiState.value.isShopOwner) {
      return
    }
    viewModelScope.launch {
      try {
        CatalogRepository.delete(productId)
        _uiState.update { it.copy(toastMessage = "Producto eliminado") }
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudo eliminar el producto") }
      }
    }
  }

  fun uploadProductImage(productId: String, uri: Uri, onUploaded: (String) -> Unit) {
    if (!_uiState.value.isShopOwner) {
      return
    }
    viewModelScope.launch {
      try {
        val url = CatalogRepository.uploadImage(productId, uri)
        onUploaded(url)
      } catch (_: Exception) {
        _uiState.update { it.copy(toastMessage = "No se pudo subir la foto") }
      }
    }
  }

  private suspend fun loadAccount(uid: String, email: String, fallbackName: String) {
    try {
      val loaded = AccountRepository.load(uid)
      if (loaded == null) {
        val name = fallbackName.ifBlank { email.substringBefore("@") }
        AccountRepository.saveProfile(uid, name, email, emptySet())
        _uiState.update {
          it.copy(accountName = name, accountEmail = email, favorites = emptySet(), accountBusy = false)
        }
      } else {
        val name = loaded.displayName.ifBlank { fallbackName.ifBlank { email.substringBefore("@") } }
        _uiState.update {
          it.copy(
            accountName = name,
            accountEmail = email,
            favorites = loaded.favorites,
            accountBusy = false
          )
        }
      }
    } catch (_: Exception) {
      _uiState.update {
        it.copy(
          accountName = fallbackName.ifBlank { email.substringBefore("@") },
          accountEmail = email,
          accountBusy = false,
          toastMessage = "No se pudo cargar tu perfil"
        )
      }
    }
  }

  private fun authMessage(error: Exception): String {
    val code = (error as? FirebaseAuthException)?.errorCode
    return when (code) {
      "ERROR_EMAIL_ALREADY_IN_USE", "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
        "Ese correo ya tiene una cuenta. Entra con tu contraseña."
      "ERROR_INVALID_CREDENTIAL", "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND" ->
        "Correo o contraseña incorrectos"
      "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres"
      "ERROR_INVALID_EMAIL" -> "El correo no es válido"
      else -> "No se pudo entrar. Inténtalo de nuevo."
    }
  }

  fun clearToast() {
    _uiState.update { it.copy(toastMessage = null) }
  }

  // --- Box Builder ---
  fun selectBoxTier(size: Int) {
    _uiState.update { state ->
      state.copy(selectedTierSize = size)
    }
  }

  fun clearBox() {
    _uiState.update { it.copy(bonbonCounts = emptyMap()) }
  }

  fun modifyBonbonCount(flavorId: String, delta: Int) {
    _uiState.update { state ->
      val currentTotal = state.bonbonCounts.values.sum()
      val currentCount = state.bonbonCounts[flavorId] ?: 0
      val targetSize = state.selectedTierSize

      if (delta > 0 && currentTotal >= targetSize) {
        return@update state.copy(
          toastMessage = "Tu cofre de $targetSize bombones ya está lleno. Para cambiar un sabor, reduce otro primero."
        )
      }

      val next = (currentCount + delta).coerceIn(0, targetSize)
      val newCounts = state.bonbonCounts.toMutableMap()
      if (next == 0) newCounts.remove(flavorId) else newCounts[flavorId] = next
      state.copy(bonbonCounts = newCounts)
    }
  }

  fun confirmBoxToCart() {
    val state = _uiState.value
    val totalBonbons = state.bonbonCounts.values.sum()
    val tier = state.selectedBoxTier

    if (totalBonbons == 0) {
      _uiState.update { it.copy(toastMessage = "Elige al menos un bombón para tu cofre") }
      return
    }

    if (totalBonbons < tier.size) {
      _uiState.update {
        it.copy(
          toastMessage = "Faltan ${tier.size - totalBonbons} bombones para completar tu ${tier.name} (${tier.size} uds)"
        )
      }
      return
    }

    val flavorMap = SanMiguelData.bonbonFlavors.associateBy { it.id }
    val customFlavors = state.bonbonCounts.entries.mapNotNull { (id, qty) ->
      if (qty <= 0) null
      else {
        val flavorName = flavorMap[id]?.name ?: id
        Pair(flavorName, qty)
      }
    }

    val boxItem = CartItem(
      id = "box_${System.currentTimeMillis()}",
      title = "${tier.name} Personalizado",
      subtitle = "${tier.size} bombones de autor a tu elección",
      unitPrice = tier.price,
      quantity = 1,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDDGmSBlyotzYVmAQTfWQoGRi4gH2FVQDLjynJ9v0Kohbyjyoi-LkbYxsEF3XbWuBszcDW3DvyItjAWEegJKCSLd15fQHD91NttEpXqwQxwEE6wK8ZQXrhVr4RmEJbCdn7DvRDyyRR1B16F6-0QX3tErXTUrYtEI9pBh04BEPnZjeRGFbO6NjJyE7v5UnSWnL5pAD8qRZIEe2nJ1dUy_rQfMGMckPa9e4nJv1gszuCKL5S-B93nvtbuLQ",
      customFlavors = customFlavors
    )

    _uiState.update {
      it.copy(
        cartItems = it.cartItems + boxItem,
        bonbonCounts = emptyMap(),
        toastMessage = "¡${tier.name} añadido a tu bolsa de compra!",
        currentScreen = CurrentScreen.Cart
      )
    }
  }

  // --- Cart actions ---
  fun updateCartQuantity(itemId: String, delta: Int) {
    _uiState.update { state ->
      val updatedList = state.cartItems.mapNotNull { item ->
        if (item.id == itemId) {
          val newQty = item.quantity + delta
          if (newQty > 0) item.copy(quantity = newQty) else null
        } else item
      }
      state.copy(cartItems = updatedList)
    }
  }

  fun removeCartItem(itemId: String) {
    _uiState.update { state ->
      state.copy(
        cartItems = state.cartItems.filterNot { it.id == itemId },
        toastMessage = "Producto eliminado del carrito"
      )
    }
  }

  fun setDeliveryMode(mode: DeliveryMode) {
    _uiState.update { it.copy(deliveryMode = mode) }
  }

  fun setPaymentMethod(method: PaymentMethod) {
    _uiState.update { it.copy(paymentMethod = method) }
  }

  fun setTimeSlot(slot: String) {
    _uiState.update { it.copy(selectedTimeSlot = slot) }
  }

  fun setGiftWrap(enabled: Boolean) {
    _uiState.update { it.copy(giftWrapEnabled = enabled) }
  }

  fun setGiftCardMessage(message: String) {
    _uiState.update { it.copy(giftCardMessage = message) }
  }

  fun addProductToCart(product: Product) {
    _uiState.update { state ->
      val existing = state.cartItems.find { it.title.startsWith(product.name.take(15)) }
      val newItems = if (existing != null) {
        state.cartItems.map {
          if (it.id == existing.id) it.copy(quantity = it.quantity + 1) else it
        }
      } else {
        state.cartItems + CartItem(
          id = "prod_${System.currentTimeMillis()}",
          title = product.name,
          subtitle = product.subtitle,
          unitPrice = product.price,
          quantity = 1,
          imageUrl = product.imageUrl
        )
      }
      state.copy(
        cartItems = newItems,
        toastMessage = "¡${product.name} añadido al carrito!"
      )
    }
  }

  fun processOrder(card: CardCheckout? = null, yape: YapeCheckout? = null) {
    val snapshot = _uiState.value
    if (snapshot.orderInProgress || snapshot.cartItems.isEmpty()) {
      return
    }
    if (snapshot.accountEmail == null) {
      _uiState.update { it.copy(toastMessage = "Entra a tu cuenta para hacer el pedido") }
      navigateTo(CurrentScreen.Profile)
      return
    }
    val email = snapshot.accountEmail
    val amountCents = (snapshot.cartTotal * 100.0).roundToInt()
    if (amountCents < 100) {
      _uiState.update { it.copy(toastMessage = "El cobro mínimo es S/ 1.00") }
      return
    }
    if (snapshot.paymentMethod == PaymentMethod.YAPE && amountCents > 200_000) {
      _uiState.update { it.copy(toastMessage = "Yape acepta hasta S/ 2000 por pago.") }
      return
    }
    _uiState.update { it.copy(orderInProgress = true) }
    viewModelScope.launch {
      try {
        val chargeId = when (snapshot.paymentMethod) {
          PaymentMethod.CARD -> {
            val payment = card ?: error("Escribe los datos de la tarjeta")
            CulqiPayments.chargeCard(
              number = payment.number.filter { it.isDigit() },
              month = payment.month.padStart(2, '0'),
              year = when (payment.year.length) {
                2 -> "20${payment.year}"
                4 -> payment.year
                else -> error("Escribe el año de la tarjeta con 4 dígitos")
              },
              cvv = payment.cvv.filter { it.isDigit() },
              email = email,
              amountCents = amountCents
            )
          }
          PaymentMethod.YAPE -> {
            val payment = yape ?: error("Escribe el celular y el código de Yape")
            CulqiPayments.chargeYape(
              phone = payment.phone.filter { it.isDigit() },
              otp = payment.otp.filter { it.isDigit() },
              email = email,
              amountCents = amountCents
            )
          }
        }
        val orderId = OrderRepository.place(snapshot, chargeId)
        receiptPdf = null
        _uiState.update {
          it.copy(
            orderInProgress = false,
            isOrderSuccessOpen = true,
            orderPlacedId = orderId,
            paidChargeId = chargeId,
            receiptBusy = false,
            receiptLabel = null,
            receiptError = null
          )
        }
      } catch (error: Exception) {
        _uiState.update {
          it.copy(
            orderInProgress = false,
            toastMessage = error.message?.takeIf { it.isNotBlank() }
              ?: "No se pudo completar el pago."
          )
        }
      }
    }
  }

  fun dismissOrderSuccess() {
    receiptPdf = null
    _uiState.update {
      it.copy(
        isOrderSuccessOpen = false,
        cartItems = emptyList(),
        currentScreen = CurrentScreen.Home,
        receiptBusy = false,
        receiptLabel = null,
        receiptError = null,
        paidChargeId = ""
      )
    }
  }

  fun requestReceipt(kind: String, customerName: String, document: String) {
    val snapshot = _uiState.value
    if (snapshot.receiptBusy || snapshot.paidChargeId.isBlank()) return
    val items = snapshot.cartItems.map { item ->
      mapOf(
        "description" to item.title,
        "quantity" to item.quantity,
        "unitPrice" to item.unitPrice
      )
    }.toMutableList()
    if (snapshot.giftWrapEnabled) {
      items.add(
        mapOf(
          "description" to "Empaque de lujo",
          "quantity" to 1,
          "unitPrice" to snapshot.giftWrapPrice
        )
      )
    }
    _uiState.update { it.copy(receiptBusy = true, receiptError = null) }
    viewModelScope.launch {
      try {
        val issued = TaxReceipts.issue(
          chargeId = snapshot.paidChargeId,
          kind = kind,
          customerName = customerName,
          document = document,
          items = items
        )
        receiptPdf = issued.pdf
        OrderRepository.attachInvoice(
          snapshot.orderPlacedId,
          issued.kind,
          issued.series,
          issued.number
        )
        val title = if (issued.kind == "factura") "Factura" else "Boleta"
        _uiState.update {
          it.copy(
            receiptBusy = false,
            receiptLabel = "$title ${issued.series}-${issued.number}",
            receiptError = null
          )
        }
      } catch (error: Exception) {
        _uiState.update {
          it.copy(
            receiptBusy = false,
            receiptError = error.message?.takeIf { message -> message.isNotBlank() }
              ?: "No se pudo emitir el comprobante."
          )
        }
      }
    }
  }

  fun currentReceiptPdf(): ByteArray? = receiptPdf

  // --- Product Detail ---
  fun openProductDetail(product: Product) {
    _uiState.update {
      it.copy(
        detailQuantity = 1,
        detailGalleryIndex = 0,
        detailIngredientsExpanded = false,
        detailShippingExpanded = false
      )
    }
    navigateTo(CurrentScreen.ProductDetail(product))
  }

  fun changeDetailQuantity(delta: Int) {
    _uiState.update {
      val next = (it.detailQuantity + delta).coerceIn(1, 20)
      it.copy(detailQuantity = next)
    }
  }

  fun setDetailGalleryIndex(index: Int) {
    _uiState.update { it.copy(detailGalleryIndex = index) }
  }

  fun toggleDetailRibbon(selected: Boolean) {
    _uiState.update { it.copy(detailRibbonSelected = selected) }
  }

  fun toggleDetailCard(selected: Boolean) {
    _uiState.update { it.copy(detailCardSelected = selected) }
  }

  fun setDetailCardMessage(msg: String) {
    _uiState.update { it.copy(detailCardMessage = msg) }
  }

  fun toggleDetailIngredients() {
    _uiState.update { it.copy(detailIngredientsExpanded = !it.detailIngredientsExpanded) }
  }

  fun toggleDetailShipping() {
    _uiState.update { it.copy(detailShippingExpanded = !it.detailShippingExpanded) }
  }

  fun addDetailToCart(product: Product) {
    val qty = _uiState.value.detailQuantity.coerceAtLeast(1)
    val item = CartItem(
      id = "detail_${product.id}_${System.currentTimeMillis()}",
      title = product.name,
      subtitle = product.subtitle,
      unitPrice = product.price,
      quantity = qty,
      imageUrl = product.imageUrl
    )
    _uiState.update {
      it.copy(
        cartItems = it.cartItems + item,
        toastMessage = "¡${product.name} añadido al carrito!",
        currentScreen = CurrentScreen.Cart
      )
    }
  }
}
