package com.app.supermarket.di.modules

import com.app.supermarket.login.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MockLoginModule constructor(private val mockRepository: Repository) {

    @Provides
    @Singleton
    fun providesRepository(): Repository {
        return mockRepository
    }
}