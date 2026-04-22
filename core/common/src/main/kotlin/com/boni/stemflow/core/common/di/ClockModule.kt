package com.boni.stemflow.core.common.di

import com.boni.stemflow.core.common.time.Clock
import com.boni.stemflow.core.common.time.SystemClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClockModule {
    @Provides
    @Singleton
    fun provideClock(): Clock = SystemClock
}
