package com.example.frankyonesampleapp

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val urlStr = request.url.toString()
            Log.e("Jaya",urlStr)
            val isActionComplete = urlStr.contains("redirect=complete")
            return when {
                isActionComplete -> {
                    Log.e("Jaya","we are able to intercept")
                    true
                }
                else -> false
            }
        }

}
