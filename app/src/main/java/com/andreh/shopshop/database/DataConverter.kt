package com.andreh.shopshop.database

import androidx.room.TypeConverter
import com.andreh.shopshop.database.entities.ShoppingItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverter {
    @TypeConverter
    fun fromShoppingItemList(value: List<ShoppingItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ShoppingItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toShoppingItemList(value: String): List<ShoppingItem> {
        val gson = Gson()
        val type = object : TypeToken<List<ShoppingItem>>() {}.type
        return gson.fromJson(value, type)
    }
}