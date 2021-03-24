package com.androiddevs.mvvmnewsapp.ui

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.androiddevs.mvvmnewsapp.R

class WebViewActivity: AppCompatActivity() {

    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        webView = findViewById<WebView>(R.id.webView)
        val client = WebViewClient()
        webView.webViewClient = client
        webView.loadUrl("https://www.google.com")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
// ALTER article.db SET content = String?
// ALTER article.db CREATE COL author as TEXT