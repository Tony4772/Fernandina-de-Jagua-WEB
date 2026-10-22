package com.example.data

import com.example.model.BonbonFlavor
import com.example.model.BoxTier
import com.example.model.CartItem
import com.example.model.Product
import com.example.model.SensoryProfile

object SanMiguelData {
  const val WHATSAPP_DISPLAY = "906 591 037"
  const val WHATSAPP_E164 = "51906591037"

  const val LOGO_URL =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuCLscibk581MuDyRbZh1Qk3xanqEP1jNUzssfC6Aub7GeRWIBnAZSbqDwtQnTcujm1-mVJm-uUeIHiA9lG6yVrgLYG3kz62IvGYXfgahBUYZvrDdDdaEZIoITxZEsv8a8El07YJg91o_yTDvz84Pd6jmwZPcOGjaN9crTH8Bpw7NqISXZiJ_1fwFAi7Q81GBJf2ei41hNBARQojhMchhkZVmONj_nufAw1nd0m8QfiBpv603xkIiGpaqg"

  const val PROFILE_URL =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuDs9MWILCj1ZEoso1pG85Yx1Qpu6zOq4pYH4jSazJ8M3OYEl7J8yr8m0vUsqG_BOfoKaOPVC7tTJZfaHHgZklD2S01KSzNMRHiEc1tKWmtBh0EEbwRyYfAiAnbanYgQu4B3rdZix5UkenY_sda_lDm4yKQI4kYHBTnlfZtDxWA2L_LOhp957zxKlU8yg6JcdKNyM6Y-OMXn8BHYIBZvv-hIby-d4VAuZ-wr7n6tcXfo9mjd1Yh-mK4S1w"

  const val HERO_IMAGE_URL =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuBMUjhUVJL-8ZzfFOlnyCPgzCMM1MWT99cItx_l-pAtl6A0pYg5HMcO_iScg1BGJ8qbWL60b4b6tRfx_AQRUx3-o5YE_L1Xjo1_7Wws6ktSRB75StinrtRoM26bh04srTZ3AGbyXZ5sR_FKB8mpA2qA8Zbv_a4Byboq9ogdzlOxAEwnpOGEBWKGwXLikWeF3yxrUzJFbMEuD-tH-R2loe5SU9nuulA3zuYyeWIYtvcdhVOwdz9vcE3XOw"

  val categories = listOf(
    Pair("Bombones de Autor", "https://lh3.googleusercontent.com/aida-public/AB6AXuBPDxcllpNJYhPnMpxL0GKeJRl-peZ9kM3qoIAHx5TEccoottzv4fsLHbjKhHFqZpLwM9zA-4MR0yCSF0_3KPFTqgBLRLuX0PWfPBj-I78zm1oMDui8OE1IzuHpVGNS0b9CvvJ-vkcyYymjgmB_lEkXDSQYECzGngHnsQPGRnxwoQ4q5_E6WnUY7lL1qt3AN9BnLByOOnvLHQw32qDBliuqb3-chmTS7tlD-wHVtBWuqmc74kQjHV6jfA"),
    Pair("Trufas & Ganache", "https://lh3.googleusercontent.com/aida-public/AB6AXuBqa3MdUZwbAzz4WMUV8AP2QP4TSjVTERoSfFwF1wopDZytMEK7nq00eNUvvwAj89GIUJ857MUNC4aPIEyYpBwLXdABlcsQyAu525hLx59SzBBBtl3rwfToYwOiii6QwtbTVzWT0B0wLo-XQqncf3knQLCIEadanGvn2OkcC_bz25HwKDS8l2SX-XEBzYlY8rpfa0fLduk6xZhp9bJKkIehlO9P05hSICQ6clgwfCcez4uRlBAk5prkuw"),
    Pair("Macarons Artesanos", "https://lh3.googleusercontent.com/aida-public/AB6AXuCywsAF8d-a_FPW5B_Z5Q0wJb3hev5zjpo-xK8iIZYB-T-_ojdHCnaeA9lm4WVy8a-IFprimHnH9E91dz0NodGnqUrFhmKte7nXqbhcV4Nzh0LP_p8opsh9s8aOs48yRiPNhmrFQyvd-mWrNCLBt4Je-GhsuhH4GME9bO5OKWyI-fxwPYf2klWO3OmlnKNyQBSuD1__E7jnuiRT9i5eE7Psxjv-gyqq6tL72um5-GdMfKCthJaqbqhiBQ"),
    Pair("Cajas de Regalo", "https://lh3.googleusercontent.com/aida-public/AB6AXuBCaiyXM6cYr0o824SNHCiq7-Dt1J0ItSwT4IFWxHWNsqbyfxuoclmJgZ498oaY_1zhCj3N6LJE8bGZHHcU7MH5UxkCIDaJxr3kvtCX2zPSS0qX5saZs8OxJF-qJCuN8EdFsm3sTIT2bxgpHUmbATnlQajwq1yloSGF78eL1_-EE31hCf6jNmwZ5jDIP9gx0RRNAClrCmwP-FLRkXUG2xBHzNKV47ZKU2kXQAwYgZ39E1219HPBMAwO_w"),
    Pair("Bean-to-Bar", "https://lh3.googleusercontent.com/aida-public/AB6AXuDGtthTZSaDE4F9aBVaBlbdnwLYCHPU2ljsXEvItu7awOcXJICl0o2qFuQFgdbYBXhqn0kcdPFM0l0uS97ECKavG-tqxU2hd6serskwZcqWVGwQxrnK1nJIihK76-YtLlGqxMkm-BiXVjHh7yrnziDI14HN-hO7dssfa38mXqrAtVQ1-Sf0cspIulsSTw1DgT2uSkRjCD_NtppisN6xsmW56K8VIQP4lDe1A-RDYuKj6Nhdb76Zfv9Paw")
  )

  val products = listOf(
    Product(
      id = "cofre_real",
      name = "Cofre Real Fernandina de Jagua · 18 Bombones",
      subtitle = "Surtido de grandes cosechas",
      price = 28.50,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDDGmSBlyotzYVmAQTfWQoGRi4gH2FVQDLjynJ9v0Kohbyjyoi-LkbYxsEF3XbWuBszcDW3DvyItjAWEegJKCSLd15fQHD91NttEpXqwQxwEE6wK8ZQXrhVr4RmEJbCdn7DvRDyyRR1B16F6-0QX3tErXTUrYtEI9pBh04BEPnZjeRGFbO6NjJyE7v5UnSWnL5pAD8qRZIEe2nJ1dUy_rQfMGMckPa9e4nJv1gszuCKL5S-B93nvtbuLQ",
      rating = 5.0,
      reviewsCount = 142,
      badge = "Favorito",
      category = "Para Regalar",
      origin = "Selección de terruños del Perú",
      description = "Exquisito cofre de madera lacada forrado en terciopelo azul marino, con 18 joyas de cacao pintadas a mano con polvo de oro comestible y ganaches de alta expresión.",
      sensoryProfile = SensoryProfile(cocoaIntensity = 4, sweetnessLevel = 2)
    ),
    Product(
      id = "bombon_oro",
      name = "Bombón Oro Praliné & Sal Marina",
      subtitle = "Castaña de Madre de Dios",
      price = 2.20,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDYVFiesXLx3NGJpP4XldhO89oRfFrIVCfrSwf5NqmMiqXdE4Qz2vP2y3pKy62AQDNeJ_jyYVRTNf0-usJ427IZAyvYOi8adIf7jEu1IbCfVhandETrVhjOn-CWOhLmggCPLFo2oNvifUP29i_1tn1iRwARCm_muyJpBK60c8L9N_TqCNyWj8NL8jhJ6AeATiijRUF8FC2OO38_8Q1Nj-6tM_32VUzVMUFqP0T3rEmUbbTgyGViZOHIxw",
      rating = 4.9,
      reviewsCount = 88,
      badge = "Praliné",
      category = "Más vendidos",
      origin = "70% Cacao de Piura · Castaña de Madre de Dios",
      description = "Bombón esculpido a mano con lámina de oro de 24k, corazón fundente de praliné tostado y microcristales de sal de Maras.",
      sensoryProfile = SensoryProfile(
        cocoaIntensity = 4,
        sweetnessLevel = 2,
        aromaNotes = listOf("Tostado Intenso", "Vainilla de selva", "Toque Floral", "Sal de Maras")
      )
    ),
    Product(
      id = "trufa_noir",
      name = "Trufa Negra Pisco & Naranja",
      subtitle = "Ganache fundente añejo",
      price = 2.40,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAwAyRw0RsGOBDVAzz_gSSfCZo4ZaPc2837M1bwFrwcW47mcX_P7ilkTwoj9pMhpZNF49Okl_O4UefOP-JDVwX-Renfhejz1WYVtIPK_LB9wOrItzz6QBRctXA77ZTx6WyMbYKYZPQR1WG74ieUzZtCSoJAw5dkXMDmeMP-LlGkaa7kILLaosRbXakX1fT1U0udDLhD8ARJaKl_BxdUbqpIgE-5YV6IayFyvp5X7ggRlxiC1ECjb6GJhw",
      rating = 4.8,
      reviewsCount = 64,
      badge = "Cacao 70%",
      category = "Sin gluten",
      origin = "Cacao de San Martín 70% y naranja peruana",
      description = "Profunda trufa de chocolate negro bañada en cacao puro, aromatizada con pisco y virutas confitadas de piel de naranja.",
      sensoryProfile = SensoryProfile(cocoaIntensity = 5, sweetnessLevel = 2)
    ),
    Product(
      id = "degustacion_origenes",
      name = "Degustación Orígenes · 6 Variedades",
      subtitle = "Piura, Cusco y San Martín",
      price = 16.90,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC3K4w9dn9m1O0307rMpNerllEMMgGrofWjiean9ikycHdbqtUshS7L4jJMnW9HUDXD2yODylaTVHz-nSmZitmF9QEnPIiTPgnJW3wmM5AAhV4KiMed7-Qi52Ye8auomzHlGHzbbLrUO3pGcFOcSHdxGuXwTxlGufW5c9suSlpThDibXXOdqjOuHVZfon0bEpZqKX4ruMH3oSSImQNUwBiOCgyUvh4P-K8c8IWwSo7mFWIc7YyaaaJphw",
      rating = 4.9,
      reviewsCount = 112,
      badge = "Tasting Box",
      category = "Veganos",
      origin = "Cacao fino de aroma del Perú",
      description = "Set de cata guiado con mini tabletas de origen único numeradas, desde las notas frutales de Piura hasta el tueste de San Martín.",
      sensoryProfile = SensoryProfile(cocoaIntensity = 4, sweetnessLevel = 1)
    )
  )

  val boxTiers = listOf(
    BoxTier(size = 6, name = "Cofre Petit", subtitle = "6 Piezas", price = 15.00),
    BoxTier(size = 12, name = "Cofre Clásico", subtitle = "12 Piezas", price = 28.00, isFavorite = true),
    BoxTier(size = 24, name = "Gran Reserva", subtitle = "24 Piezas", price = 52.00)
  )

  val bonbonFlavors = listOf(
    BonbonFlavor(
      id = "pistachio",
      name = "Ganache de Sacha Inchi",
      description = "Sacha inchi tostado de San Martín con chocolate con leche 42%",
      notes = "Notas tostadas y salinidad sutil",
      category = "milk",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC3oK5amoCi7GA3JS0_Wjz2pCHt7tPtE1A_6bcAXI8qRMOGMhSO70Ck680341MYk2vtWGqLM88xiT8qMmMLfdITFt8MbGX7Hi5F9gsxY58qi3cKGqWkTwrviLUFs2y9P0Rf042eje6BB4KSMD0rAYC8m0XHPZd5xL8y8Dr0HInAZqOQ7FfkzBBeVHiK1jdi7a_ZbD2Bhq0k2rC3tURegRHmMnbfLGRLjZrETsEwuP6T-QSFdrzlLT7Eew",
      bonbonImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCYN6ySGbPaPP1DMAdjsSix62JkJ8NAoMYgKIDPfxbgUjXCIjLIk_RgsILmosaxCkpKpD9RzufjTLN-OObDVzcHhoNy_4pOZMIkLMMO3FAZ8TylgzttBBAsUD2poFmhuPru7Vn6nYwnqNmyQWMJDrmlsdlMCOftfJWxlDjqRx6P6iGfcia4JkXW8I0gyz2biFEhgQPzkji6h_Cmms3toi6weCed5e_Csq2yeM4z_Wt85jnoqvSLFgUMnQ",
      tag = "Estrella"
    ),
    BonbonFlavor(
      id = "ruby",
      name = "Frambuesa Silvestre Ruby",
      description = "Cacao Ruby natural y coulis de frambuesas de montaña",
      notes = "Acidez vivaz y toque floral",
      category = "fruit",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAXsvL5f0Sao5EQ7xfcpd9K_WBAKuCUls9EWbF8nlVW_Yq4IrUIpAy3u3G1-nZPmpfzuzRkFJp44WTQXty30fKHKunvw0AFfX1Ow0al_wYo99d8zUJAjSVKJtDU2cnEqK4jEZ1fRcZnPEQMUhra_aybmVHnD4aTx8UWV9ikhis1mWX_riP9WvCrKlEXYiGjXBVYjRFqfwOes9kbPBMADppN2PZsl-OLM0br7Ytsx0dhREaJ17rFkmmFUw",
      bonbonImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBMe7rkPEHAAtTpFc_VD8nvm2vPPiHoETfzdqEY0MTH0ly4CVvEfQfxdwvOzaB4l_-uktGTNsNT3fDq3zAVFKnPU0SZey8SvXhFo9s4dT5OKWBkmzaO8TGk1U2S3tq2mQVzV8-EW1KpJECNWmoOyBK6zruSFPdsVPdYm67fpzg6JgEoa9VYY_1TfOgpEaUNz-9fTtvvT2FzwTjsNlOCBXDM4N_eLWehOFs9luyTr03GBRkqZGFuFhHYuA"
    ),
    BonbonFlavor(
      id = "caramel",
      name = "Caramelo a la Flor de Sal",
      description = "Mantequilla de Cajamarca con cristales de sal de Maras",
      notes = "Textura melosa con contraste salino",
      category = "milk",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA_fgV4VMRDxuso-BrW7qCid7e28mOUHuUHlHdJ7f5y-qMteHPWPcnDu1Xkzhwb_xcExszFU988EGZ4arqVIuVlvtY2wqIO7NY4ZKKJstq_iXl9DrMtqi9cHYLjv-9untjFYOPI-lLCFW34G57apk3H9Lt4VX6nHh-oZzRh1ZidgtHQ8IusfVVqk4OmjboqoFZbKLioIkYnxQSCBTpoOIGqFun_BdAjJNfGTl-H4BXDlZXbg1CJ5Z_rAw",
      bonbonImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCZm2KLv13C38zDUKxgixv0HCasz97EnAcjO3mwP2ZoEWts4_3xZgvGzAsxmd-YflVJoO-ghwD4uOk5007igL-pGGuBPfG_lw79g8wE_38RRn_Hxt3fGw5vJKqkA7T8zJ1vW4KwJr0unMxazQU5dGnII0-O0XijcxW7iPj4t_hb1niKs4jtcwCW-huAzDcAfFMvp8NJSnpdNimIgFJY972ALjATVQVkc0lKywYiCAprWg_exh7jHVhswA"
    ),
    BonbonFlavor(
      id = "moka",
      name = "Moka de Chanchamayo",
      description = "Infusión en frío de café de Chanchamayo con cacao criollo 72%",
      notes = "Amargor elegante con notas de cardamomo",
      category = "dark",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCRxXJBO_Y2B0oB4-G-3USf47mi-F0rOr3VsWNzYEw1rcgA26EEgCmQ-0o0JZBd_cEt95wbaCcl_gna0XXOKOFqxw7cek-r9JMbeRVEOrLO_PA2mgqb9wc50tH8cbyKNbwZjr-UP3eNydHhPTzDgUMem10KKHuF6U1e0smvlLap5RU6UeiSTRQ7AqEEvyZWluyuyx8j8iB457oxVK9kDhNYBPkJ3OQ3c8EHZ2GlNqAHU-ukqCopxd-nJA",
      bonbonImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAvzIpgS7bb7wL3i6lDIaCj0GmiynfyUFOwe4NJc0kHZif4YB4-JsZg-EFw5sj41W01YJZ-Jrf_vkhOqNDK39xAblmiVNRA-be0rxTsDOAwEloGNXXVU3Lr9P2nHmuG4YUfx6u-taq0DYO21ZefB_VNv53tp4lXNmBh2YO_13eICttj3eaEJvXfXMiI0lqp29dAyjrzFU-oDiI8RhcDN3HnPeEL_txzoJmbbjChUqbrWWegL7O_UxrkwQ"
    ),
    BonbonFlavor(
      id = "chuao",
      name = "Piura Blanco 75%",
      description = "Cacao blanco de Piura, notas de ciruela y frutos del bosque",
      notes = "Cacao puro intenso sin aditivos",
      category = "dark",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCHP9txG8FsdlHwyecvwu9NoGaWc-CaYuosPQgDV6VNopEVNVcSXZ9UGdfUlekiTRERPRT_nig-I6aYOS-8DqJWNx83KvqjT1cz5WF5RWqAMxUVeZ-WbwCDUbY40yacnddRiaeMrJn7iQ8BhojKqwLfiz9BgvNaqeB9jPB2WsfytMSXvEOjXSE6mWCBjXjToPBgpd9cJo97UNdyIPELp9mJJWUaMTXuIGvMdlcdTsdvrFKNRuprki07vg",
      bonbonImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCb-iiQf-073G7QaMo7YnDX8EjLCAA7vYKqnaGkI3anqYN_cRNogJG6yZTY-N58RIh73l27PXxUBnEwVutTVdrXsD_62CUOtHvdMnnfz57xvmDHb5v-USi-CDi4BJmRjt71_YaTLQQ_AHJwyWo4HrOPoVQE4MCQvPvglrLCzA1Gy0-4BqFRLnFq_VNJ894cwBVzZTDMRUYz9I5FGffqc571dyV3DquK0zBW_dHUVOqGMgfIhM14nFygfA"
    ),
    BonbonFlavor(
      id = "sherry",
      name = "Pisco & Naranja",
      description = "Pisco acholado con piel de naranja peruana",
      notes = "Cálido y espirituoso",
      category = "liqueur",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCJHa4hiwT7T2W7TK-97pNJnauyj7YfavoMax0Lc6zjRi35hpJ2uh2myb4jFxmJswzRw6Kl7Xra2lSP0i_G2muoiyhZzqLRWZPEvby0wbMRabBPzV7RzTZoLnJMQn0hENpNdc7Yybgg639xKdgOQ3jLo05AW0CBv-1akVGISX8m4_MoHDwJj126IVoqmnGcH_S0xzpD7PZqSpyz3l3UciyAEi0LEdvN2XCjNcjKmy_V3L8KB4tYX7Ix4g",
      bonbonImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA_zYcXUsXeUClHqtw0QV6_g-F7FGx4jo0iZZW3HOa4Z_0vuPY1II8g_2KQdTVAYupb6ATd5mZJVB0E4ySKUw08NRbi4AkSr5RVGLFpz0v9-7LZMU3-Kl21vqCBAHhJmKKGU-dhkHTp9maW1YCVDB6VQhzBWyEmyoi-bpWmV_hdbOcvT2N_EEMNjYalZzS0OTWVfKB7yowk860x0vFMw-qcUBSKcK7EZyg87gr8oRU10xLpJlugjfghJA",
      alcoholTag = "Licor 2.5%"
    )
  )

  val initialCart = listOf(
    CartItem(
      id = "c1",
      title = "Cofre Clásico Personalizado",
      subtitle = "12 bombones de autor",
      unitPrice = 28.00,
      quantity = 1,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDe0nxgRYMACpNF-JW2Q_CL1Ozlcn0_LwsG0lbLN9VLd_6u2CZt5EXg-y5Jkgp4LrgF8gMfCVtSqyjnCU1toyvZTjMehd_dGzg44MjpjrlNW41QpyoOyAWg7Ix0Iigxn-t8CE-QfUB7QRqd9vRi69VsRvGGHFNsJOqiXs0R_j5kTymd9HQX_M9fQrQvBQUDExfqTaPv1quJiiU5IWZtwIxWESTpy_iOb0_pXanzpVhq3cMZfuesqU6tPw",
      customFlavors = listOf(
        Pair("Praliné Pistacho", 3),
        Pair("Trufa Frambuesa", 3),
        Pair("Ganache 72% Origen", 4),
        Pair("Caramelo Sal de Maras", 2)
      )
    ),
    CartItem(
      id = "c2",
      title = "Caja Degustación Orígenes",
      subtitle = "6 variedades de terruño",
      unitPrice = 16.90,
      quantity = 1,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD3vkmHX3ygVEAm0bRgs9LioDW0xsx7HKNmO_JnqjpfQ65JmUIT3f7vlbiiqLS2gPGWQwi6FT_3P1K5XYQow9g2M-la_3ZnPXimxiVOR7nsqIen3hP705-R7oqsRY4V_70Fkav0ytizLnB4qP-JKAO3T71ussDmVureMQ5ONC7rkewpt1BOZ53kqoHARfsBBf_w7g33ccGOLGyGtiS_hvOS8SHccBEnY85UZ-l41DAno8jlyuKGtY5TLA"
    ),
    CartItem(
      id = "c3",
      title = "Tableta Criollo 85%",
      subtitle = "Edición Bean-to-Bar (2 uds.)",
      unitPrice = 5.80,
      quantity = 2,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC6eRR3Cxy8fMsUsBdQ4Na_tj49ZS7YlwhahtSpWLlik5ugVBgZVQW3-ukb58DJMWAhkoSvm-DderfV7RlFn-4D8fYldsYnYTDQlRQ_jd5NqOUXXcCJuw03X0Mwa-rbir1oI9T_173uk06Z5W177TzRsfgB6lVLLEiEOLDHv_12nfw2iauksLkLi5I6ybuRulzdr4jX6MRsPlrrfA1Wv7_j0tCPp44L1WDWIBjeWQYb_fLY7db8tmQOaA"
    )
  )

  val detailGallery = listOf(
    "https://lh3.googleusercontent.com/aida-public/AB6AXuDENsV23ScNmjNiuTQqmYbgb8jSbHaAvtZz6h57p4PBBVYowsCYqttGn1ZRg91ELmxD3OhVlor5arhosVdyZMtb0k0ehGZRSoMIS-mkp60qpBskUJGg_R3kipU7frM-9-snJyPYyo7YN3IcqgAzlSUGd9eQ6e9u87YSndU0zhlQxX0pXi2Ggg4xpvEu99IrksVXh1t3zwSW8-jG_Btyi_pNkQ0E7deRrnddixa2NJpYWi5qkB4T3GKl4Q",
    "https://lh3.googleusercontent.com/aida-public/AB6AXuD6wb1ZHevAf4f8NlEqcqqg0oU95hGSKvfQmXPZ0HVrwe7eDgKS2szMKxF4BuXS_FpFHarEUU4wvqFZBm7ia8Zt-QCP8M_3UwAg3TfCbm28p2F1q9Gag2vXu3mcaPoNemdAVzwEKQVFwiBfSFfqOl_SmcNUM7DbwBs-6BfsdiobOXvoyAN5-bLNQbpZji91gxHCTyXAgTkxu-YQaQttIDvDp5T8DGEPJgaA4rO6xyX5YahzW-OzGydyNA",
    "https://lh3.googleusercontent.com/aida-public/AB6AXuBdsl2SBJPzT5lUvu0O55tKqmvdEUB1c1QgDMTY7rxMqaIGWm9zgwL2zHVvC_cYcg3e9S5IF-_ZTFEHd9-KKiqP_vuFwGp7mRgZ8lexFwDW-L0KqtFHfSxhqO8W0LBGJT6wCSQ_98_DCaj28rXMXFjutujP-AVN_Dxbk5p4WeLJu7rCaIEGQFwdSBrywEqADxMJS98Izvvwd1dFWlqLxUv4V5Xx8rRAim076_L3nb0T9S16qWATgkhUag",
    "https://lh3.googleusercontent.com/aida-public/AB6AXuCYLRFnr7knT2cvwmi-FBqztg90ZjFPnZ9U8i4pGDgm8AeKZRZpmp_TSihgEKV8Ro5271ESbg3RzoUJ7wNk3HcBr9s-q9IAo3nINEuLdlqKWPOUUYt9l_VM2EeTz_Zg2UINK7iEpMenJAmZk8wnk1gu_M0iHHOXcWEAqqXItVtOXbrs4MLSipr2Jg_iJvxZCds_YjN0AAzM_8JlVJvq0GYIZpbvizmzFG8UPmDYllLso9dL_nA77GW70w"
  )
}
