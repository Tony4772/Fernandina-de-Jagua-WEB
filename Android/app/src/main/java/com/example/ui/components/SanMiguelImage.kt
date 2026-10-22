package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Optimized lazy-loaded image component using Coil's memory & disk cache
 * with crossfade transition as items enter the screen viewport.
 */
@Composable
fun SanMiguelImage(
  model: Any?,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop
) {
  val context = LocalContext.current
  val request = remember(model) {
    ImageRequest.Builder(context)
      .data(model)
      .crossfade(300)
      .build()
  }

  AsyncImage(
    model = request,
    contentDescription = contentDescription,
    contentScale = contentScale,
    modifier = modifier
  )
}
