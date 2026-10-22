package com.example.ui.splash

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun EbyzomSplash(
  appName: String,
  tagline: String,
  logo: Painter,
  paper: Color,
  ink: Color,
  inkSoft: Color,
  onFinished: () -> Unit,
) {
  var brand by remember { mutableStateOf(true) }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      WindowCompat.getInsetsController(window, view).apply {
        isAppearanceLightStatusBars = !brand
        isAppearanceLightNavigationBars = !brand
      }
    }
  }
  if (brand) {
    LaunchedEffect(Unit) {
      delay(1600)
      brand = false
    }
    Box(
      Modifier
        .fillMaxSize()
        .background(Color.Black)
        .clickable { brand = false },
      contentAlignment = Alignment.Center,
    ) {
      Image(
        painterResource(R.drawable.ebyzom_logo),
        contentDescription = "EBYZOM",
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 40.dp),
        contentScale = ContentScale.Fit,
      )
    }
  } else {
    LaunchedEffect(Unit) {
      delay(2200)
      onFinished()
    }
    Box(
      Modifier
        .fillMaxSize()
        .background(paper)
        .statusBarsPadding()
        .clickable(onClick = onFinished),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        Image(
          logo,
          contentDescription = appName,
          modifier = Modifier
            .fillMaxWidth(0.92f)
            .heightIn(min = 280.dp, max = 380.dp)
            .padding(horizontal = 8.dp),
          contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(16.dp))
        Text(
          tagline,
          color = inkSoft,
          fontSize = 17.sp,
          fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
          fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
          textAlign = TextAlign.Center,
          lineHeight = 24.sp,
          modifier = Modifier.padding(horizontal = 24.dp),
        )
      }
    }
  }
}
