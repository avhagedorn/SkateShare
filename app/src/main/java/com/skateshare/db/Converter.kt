package com.skateshare.db

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

    @TypeConverter
    fun floatlistToJson(raw: List<Float>) : String {
        return Gson().toJson(raw)
    }

    @TypeConverter
    fun jsonToFloatList(raw: String) : List<Float> {
        val targetType = object: TypeToken<List<Float>>(){}.type
        return Gson().fromJson<List<Float>>(raw, targetType).toList()
    }
}