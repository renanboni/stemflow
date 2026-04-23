package com.boni.stemflow.core.database.di

import android.content.Context
import androidx.room.Room
import com.boni.stemflow.core.database.StemflowDatabase
import com.boni.stemflow.core.database.dao.AlbumDao
import com.boni.stemflow.core.database.dao.RecentlyPlayedDao
import com.boni.stemflow.core.database.dao.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StemflowDatabase =
        Room.databaseBuilder(
            context,
            StemflowDatabase::class.java,
            "stemflow.db",
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideTrackDao(db: StemflowDatabase): TrackDao = db.trackDao()

    @Provides
    fun provideAlbumDao(db: StemflowDatabase): AlbumDao = db.albumDao()

    @Provides
    fun provideRecentlyPlayedDao(db: StemflowDatabase): RecentlyPlayedDao = db.recentlyPlayedDao()
}
