package com.app.supermarket.di

import com.app.supermarket.di.modules.LoginModule
import com.app.supermarket.di.modules.NetworkModule
import com.app.supermarket.login.LoginActivity
import com.app.supermarket.main.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        LoginModule::class,
        NetworkModule::class
    ]
)
@Singleton
interface AppComponent {

    fun inject(loginActivity: LoginActivity)

    fun inject(mainActivity: MainActivity)
}
