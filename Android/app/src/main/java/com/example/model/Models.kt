package com.example.model

data class Product(
  val id: String,
  val name: String,
  val subtitle: String,
  val price: Double,
  val imageUrl: String,
  val rating: Double = 4.9,
  val reviewsCount: Int = 88,
  val badge: String? = null,
  val category: String = "Bombones",
  val origin: String = "Piura y San Martín",
  val description: String = "",
  val sensoryProfile: SensoryProfile = SensoryProfile(),
  val visible: Boolean = true
)

data class SensoryProfile(
  val cocoaIntensity: Int = 4, // out of 5
  val sweetnessLevel: Int = 2,  // out of 5
  val aromaNotes: List<String> = listOf("Tostado Intenso", "Vainilla de selva", "Toque Floral", "Sal de Maras")
)

data class BonbonFlavor(
  val id: String,
  val name: String,
  val description: String,
  val notes: String,
  val category: String, // "dark", "milk", "fruit", "liqueur"
  val imageUrl: String,
  val bonbonImageUrl: String,
  val tag: String? = null,
  val alcoholTag: String? = null
)

data class BoxTier(
  val size: Int,
  val name: String,
  val subtitle: String,
  val price: Double,
  val isFavorite: Boolean = false
)

data class CartItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val unitPrice: Double,
  val quantity: Int,
  val imageUrl: String,
  val customFlavors: List<Pair<String, Int>> = emptyList() // flavor name to count
) {
  val totalPrice: Double get() = unitPrice * quantity
}

enum class DeliveryMode {
  REFRIGERATED,
  PICKUP_SERRANO
}

enum class PaymentMethod {
  YAPE,
  CARD
}

data class CardCheckout(
  val number: String,
  val month: String,
  val year: String,
  val cvv: String
)

data class YapeCheckout(
  val phone: String,
  val otp: String
)

data class ShopCategory(
  val id: String,
  val name: String,
  val imageUrl: String = "",
  val sort: Int = 0
)

data class StorefrontCopy(
  val eyebrow: String = "MAESTROS CHOCOLATEROS",
  val headline: String = "El arte del cacao puro",
  val cornerBadge: String = "Gourmet Selection",
  val heroBadge: String = "EDICIÓN ESPECIAL",
  val heroSeason: String = "Colección de Autor",
  val heroTitle: String = "Trufas de castaña y cacao 72%",
  val heroBody: String = "Cacao peruano fino de aroma con notas tostadas de avellana silvestre y toque sutil de flor de sal.",
  val heroPriceCaption: String = "CAJA EXCLUSIVA",
  val heroPrice: String = "S/ 24.90",
  val heroButton: String = "Explorar Colección",
  val heroImageUrl: String = "",
  val categoriesHeading: String = "Categorías de Confitería",
  val productsHeading: String = "Creaciones Emblemáticas",
  val productsNote: String = "BOMBONES ARTESANALES",
  val boxKicker: String = "PERSONALIZACIÓN",
  val boxTitle: String = "¿Buscas un regalo especial?",
  val boxBody: String = "Arma tu caja personalizada: elige el estuche, selecciona tus bombones favoritos y añade un mensaje personalizado.",
  val boxStep1: String = "Tamaño",
  val boxStep2: String = "Sabores",
  val boxStep3: String = "Dedicatoria",
  val boxButton: String = "Diseñar mi Caja",
  val quote: String = "",
  val quoteName: String = "",
  val quoteRole: String = "",
  val sealTitle: String = "Cacao 100% Peruano",
  val sealBody: String = "Cacao fino de aroma de Piura y San Martín",
  val sealButton: String = "CONOCER MÁS",
  val footerLine: String = "FERNANDINA DE JAGUA CHOCOLATES · SAN MIGUEL, LIMA",
  val deliveryTitle: String = "Modalidad de Entrega",
  val deliveryDetail: String = "Envío a domicilio o recojo en tienda en San Miguel",
  val giftTitle: String = "Empaquetado para Regalo",
  val giftDetail: String = "Caja precintada con dedicatoria personalizada",
  val profileTagline: String = "Chocolatería Fina y Bombones Artesanales",
  val trustCraft: String = "Cacao Peruano",
  val trustCold: String = "Empaque Seguro",
  val trustPay: String = "Pago Seguro SSL",
  val tierPetitPrice: Double = 15.00,
  val tierClasicoPrice: Double = 28.00,
  val tierGranReservaPrice: Double = 52.00,
  val storeAddress: String = "Jr. Comandante Juan Guillermo Moore 296, San Miguel, Lima",
  val storeHours: String = "Lunes a Sábado: 10:00 am - 8:00 pm · Domingos: 11:00 am - 6:00 pm",
  val storePhone: String = "+51 987 654 321"
)

data class OrderSummary(
  val orderId: String,
  val date: String,
  val total: Double,
  val itemsCount: Int,
  val status: String
)
