package com.boni.stemflow.core.data.di

import com.boni.stemflow.core.data.repository.DefaultAlbumRepository
import com.boni.stemflow.core.data.repository.DefaultSearchRepository
import com.boni.stemflow.core.data.repository.DefaultTrackRepository
import com.boni.stemflow.core.domain.repository.AlbumRepository
import com.boni.stemflow.core.domain.repository.SearchRepository
import com.boni.stemflow.core.domain.repository.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: DefaultSearchRepository): SearchRepository

    @Binds
    @Singleton
    abstract fun bindTrackRepository(impl: DefaultTrackRepository): TrackRepository

    @Binds
    @Singleton
    abstract fun bindAlbumRepository(impl: DefaultAlbumRepository): AlbumRepository
}
