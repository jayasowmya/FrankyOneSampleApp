package com.example.frankyonesampleapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       val btnClick =  findViewById<Button>(R.id.btnClick)
        btnClick.setOnClickListener {
            val navigate = Intent(this, WebViewActivity::class.java)
            startActivity(navigate)
        }
    }
}