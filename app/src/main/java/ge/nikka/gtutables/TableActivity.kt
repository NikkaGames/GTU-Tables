package ge.nikka.gtutables

import android.app.ActionBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ge.nikka.gtutables.ui.theme.GTUTablesTheme
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.graphics.drawable.toDrawable

class TableActivity : ComponentActivity() {

    internal var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.let { ab ->
            val textView = TextView(this).apply {
                text = "Schedule table for " + Singleton.instance.table
                textSize = 20f
                setTextColor(android.graphics.Color.WHITE)
                gravity = Gravity.CENTER

                typeface = ResourcesCompat.getFont(this@TableActivity, R.font.google)
            }

            val layout = ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }

            ab.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            ab.setCustomView(textView, layout)

            ab.setBackgroundDrawable(android.graphics.Color.DKGRAY.toDrawable())
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        setContent {
            GTUTablesTheme {
                TableScreen(Singleton.instance.data!!, this)
            }
        }
    }

    override fun onBackPressed() {
        webView?.let {
            if (it.canGoBack()) {
                it.goBack()
            } else {
                super.onBackPressed()
            }
        } ?: super.onBackPressed()
    }
}

@Composable
fun TableScreen(
    htmlData: String,
    activity: TableActivity
) {
    var loading by remember { mutableStateOf(true) }

    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.DarkGray)
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.apply {
                        builtInZoomControls = true
                        displayZoomControls = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }

                    setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            if (url == null) return false
                            Toast.makeText(context, url, Toast.LENGTH_SHORT).show()

                            when {
                                url.startsWith("tg:") || url.startsWith("fb:") -> {
                                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                    context.startActivity(intent)
                                    return true
                                }
                                url.startsWith("intent:") -> {
                                    while (view?.canGoBack() == true) view.goBack()
                                    return true
                                }
                                url.startsWith("sfbfi:") -> {
                                    return true
                                }
                            }
                            return false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            loading = false
                        }
                    }

                    loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)

                    activity.webView = this
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
                webView.scaleX = animatedScale
                webView.scaleY = animatedScale
            },
            modifier = Modifier.fillMaxSize()
        )

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(60.dp))
            }
        }
    }
}