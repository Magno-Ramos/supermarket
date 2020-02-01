package com.app.supermarket.di.modules

import com.app.supermarket.core.OpenForTesting
import com.app.supermarket.login.LoginRepository
import com.app.supermarket.login.Repository
import dagger.Module
import dagger.Provides

@OpenForTesting
@Module
class LoginModule {

    @Provides
    fun providesRepository(): Repository {
        return LoginRepository()
    }
}