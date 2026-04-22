package com.boni.stemflow.core.common.di

import com.boni.stemflow.core.common.network.ConnectivityObserver
import com.boni.stemflow.core.common.network.DefaultConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(
        impl: DefaultConnectivityObserver,
    ): ConnectivityObserver
}
