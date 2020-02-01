package com.app.supermarket

import android.app.Application
import com.app.supermarket.di.AppComponent
import com.app.supermarket.di.DaggerAppComponent
import com.app.supermarket.di.modules.LoginModule

class MarketApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .loginModule(LoginModule())
            .build()
    }
}