package com.example.revolut

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // start Koin!
        startKoin {
            // Android context
            androidContext(this@MyApplication)
            // modules
            modules(myModule)
        }
    }
} 