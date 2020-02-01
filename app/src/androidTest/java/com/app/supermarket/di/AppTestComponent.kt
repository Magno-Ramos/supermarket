package com.app.supermarket.di

import com.app.supermarket.di.modules.MockLoginModule
import com.app.supermarket.di.modules.MockNetworkModule
import com.app.supermarket.login.LoginActivityTest
import com.app.supermarket.main.MainActivityTest
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        MockLoginModule::class,
        MockNetworkModule::class
    ]
)
@Singleton
interface AppTestComponent : AppComponent {

    fun inject(loginActivityTest: LoginActivityTest)

    fun inject(mainActivityTest: MainActivityTest)
}