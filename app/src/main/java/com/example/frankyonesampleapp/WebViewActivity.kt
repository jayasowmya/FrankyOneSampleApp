package com.example.frankyonesampleapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.helper.widget.MotionEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WebViewActivity : AppCompatActivity() {
    private var uri: Uri? = null
    private var takeDocumentPicture: ActivityResultLauncher<Uri>? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SS",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        return image
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        //WebView Object
        val myWebView: WebView = findViewById(R.id.webView)

        //Enable Javascript

        myWebView.settings.javaScriptEnabled = true

        myWebView.settings.domStorageEnabled = true
        myWebView.settings.databaseEnabled = true


        myWebView.settings.mediaPlaybackRequiresUserGesture = false;

        myWebView.webViewClient = MyWebViewClient()

        myWebView.webChromeClient = MyWebChromeClient()

        myWebView.loadUrl("https://staging.boqgroup.idkit.co/4f6fd7f7-fdee-4f2e-bc6a-1c272899774f")

        takeDocumentPicture =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
                if (success) {
                    // The image was saved into the given Uri -> do something with it
                    val msg = "Image captured successfully at : $uri"
                    Log.v(MotionEffect.TAG, msg)
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    filePathCallback?.onReceiveValue(arrayOf(uri!!))
                }
            }
    }

    private inner class MyWebChromeClient : WebChromeClient() {

        override fun onPermissionRequest(request: PermissionRequest?) {
            //super.onPermissionRequest(request)
            Log.i(MotionEffect.TAG, "onPermissionRequest ${request?.resources}");
            val requestedResources = request!!.resources
            for (r in requestedResources) {
                if (r == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                    request.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                    break
                }
            }
        }

        override fun onPermissionRequestCanceled(request: PermissionRequest?) {
            super.onPermissionRequestCanceled(request)
            Log.i(MotionEffect.TAG, "onPermissionRequestCanceled ${request?.resources}");
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallbackParam: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            Log.v(MotionEffect.TAG, "Show a file chooser")
            filePathCallback = filePathCallbackParam

            uri = createImageFile()?.let {
                FileProvider.getUriForFile(
                    this@WebViewActivity,
                    "com.example.frankyonesampleapp.fileprovider",
                    it
                )
            }
            takeDocumentPicture?.launch(uri);

            return true
        }
    }
}