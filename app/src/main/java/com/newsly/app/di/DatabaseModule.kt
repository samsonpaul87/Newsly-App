package com.newsly.app.di

import android.content.Context
import androidx.room.Room
import com.newsly.app.data.local.database.BookmarkDao
import com.newsly.app.data.local.database.NewslyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNewslyDatabase(@ApplicationContext context: Context): NewslyDatabase {
        return Room.databaseBuilder(
            context,
            NewslyDatabase::class.java,
            NewslyDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(database: NewslyDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
}
