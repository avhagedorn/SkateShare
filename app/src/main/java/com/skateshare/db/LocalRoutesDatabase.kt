package com.skateshare.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skateshare.models.Converter
import com.skateshare.models.Route

@Database(entities = [Route::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class LocalRoutesDatabase : RoomDatabase() {

    abstract val localRoutesDao: LocalRoutesDao

}