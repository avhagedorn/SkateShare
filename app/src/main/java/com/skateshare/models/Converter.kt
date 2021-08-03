package com.skateshare.models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {

    @TypeConverter
    fun listToJson(raw: List<Double>) : String {
        return Gson().toJson(raw)
    }

    @TypeConverter
    fun jsonToList(raw: String) : List<Double> {
        val targetType = object: TypeToken<List<Double>>(){}.type
        return Gson().fromJson<List<Double>>(raw, targetType).toList()
    }
}