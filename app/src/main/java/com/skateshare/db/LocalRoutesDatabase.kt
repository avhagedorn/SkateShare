package com.skateshare.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skateshare.models.Route

@Database(entities = [Route::class], version = 6, exportSchema = false)
@TypeConverters(Converter::class)
abstract class LocalRoutesDatabase : RoomDatabase() {

    abstract val localRoutesDao: LocalRoutesDao

}