package com.example.frankyonesampleapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WebViewActivity : AppCompatActivity() {
    private var uri: Uri? = null
    private var pathCallback: ValueCallback<Array<Uri>>? = null
    private val REQUEST_IMAGE =101


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
        // Save a file: path for use with ACTION_VIEW intents
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

        myWebView.loadUrl("https://staging.boqgroup.idkit.co/6a570a0e-4405-44f4-a62b-7ba38d6741df")
    }

    private inner class MyWebChromeClient : WebChromeClient() {

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallbackParam: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            Log.v(MotionEffect.TAG, "Show a file chooser")

            if (pathCallback != null) {
                pathCallback?.onReceiveValue(null)
                pathCallback = null
            }
            pathCallback = filePathCallbackParam
            if (fileChooserParams?.isCaptureEnabled == true) {
                var permissions = ArrayList<String>()
                if(existCameraPermission()) {
                    permissions.add(android.Manifest.permission.CAMERA);
                }
                if(existMemoryWritePermission()) {
                    permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if(permissions.isNotEmpty()) {
                    requestPermissions(permissions.toTypedArray(), 1);
                } else {
                    takePhoto()
                }
            } else {

                val intent: Intent? = fileChooserParams?.createIntent()
                try {
                    intent?.let { startActivityForResult(it, REQUEST_IMAGE) }
                } catch (e: ActivityNotFoundException) {
                    pathCallback = null
                    return false
                }
            }

            return true
        }
    }

    private fun existMemoryWritePermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED)
    }

    private fun existCameraPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
    }

    fun takePhoto() {
        var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent?.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                Log.d("error ================> ", ex.toString())
            }
            if (photoFile != null) {
                var photoFileTmp = photoFile
                uri = FileProvider.getUriForFile(this, "com.example.frankyonesampleapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            }
            startActivityForResult(takePictureIntent, REQUEST_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && (requestCode == REQUEST_IMAGE)) {
            if (pathCallback == null) {
                return
            }
            pathCallback?.onReceiveValue(arrayOf(uri!!))
        }
    }
}