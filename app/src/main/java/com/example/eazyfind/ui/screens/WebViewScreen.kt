package com.example.eazyfind.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.eazyfind.R

@Composable
fun WebViewScreen(
    url: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {

        // 🌐 WEBVIEW (FULL SCREEN)
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            }
        )

        // LOGO OVERLAY (FLOATING)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .padding(top = 12.dp)
                .background(Color.White)
                .zIndex(1f),
            contentAlignment = Alignment.BottomStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_horizontal_white_bg),
                contentDescription = "App Logo",
                modifier = Modifier.height(50.dp)
            )
        }
    }
}
