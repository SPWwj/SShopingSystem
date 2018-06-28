package com.example.weiwenjie.sassistbot

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_goods_info.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast


class GoodsInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_goods_info)
//
//        wvWiki = WebView(this)

        setContentView(R.layout.activity_goods_info)

        wvWiki.setWebViewClient(WebViewClient())

        val url= arrayOf("https://simple.wikipedia.org/wiki/Apple","https://simple.wikipedia.org/wiki/Pear","https://simple.wikipedia.org/wiki/Orange_(fruit)",
               "https://simple.wikipedia.org/wiki/Grape","https://simple.wikipedia.org/wiki/Watermelon")
        val bundle: Bundle = intent.extras
        val name = bundle.getString("name")
        val des = bundle.getString("des")
        //val image = bundle.getInt("image")



        if (name=="Orange") wvWiki.loadUrl("https://simple.wikipedia.org/wiki/Orange_(fruit)")
        else if(name=="Apple2" ||"Apple3"==name) wvWiki.loadUrl(url[0])
        else
        wvWiki.loadUrl("https://en.wikipedia.org/wiki/"+name)


    }

    override fun onBackPressed() {
        if (wvWiki.canGoBack()) {
            // If web view have back history, then go to the web view back history
            wvWiki.goBack()
            Toast.makeText(this, "Go to back history", Toast.LENGTH_SHORT).show()
        } else {
            // Ask the user to exit the app or stay in here
            super.onBackPressed()
            finish()
        }
    }
}


