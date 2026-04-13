package com.bafanam.logistics

import android.app.Application
import com.bafanam.logistics.di.AppContainer
import com.bafanam.logistics.di.DefaultAppContainer

class LogisticsApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}
