package com.example.chatapp

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient

class ChatApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
        OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }
}