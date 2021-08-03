package com.skateshare.di

import android.content.Context
import androidx.room.Room
import com.skateshare.db.LocalRoutesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule  {

    @Singleton
    @Provides
    fun provideLocalRoutesDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        LocalRoutesDatabase::class.java,
        "local_routes_database"
    ).build()

    @Singleton
    @Provides
    fun provideLocalRoutesDao(db: LocalRoutesDatabase) = null //db.getDao()

}