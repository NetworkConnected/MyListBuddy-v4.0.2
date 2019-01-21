package com.nwcandroiddesign.listbuddy.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ShoppingListDatabaseConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun stringToShoppingListItems(json: String): ArrayList<ShoppingListItem> {

        val gson = Gson()

        return gson.fromJson(json, object : TypeToken<ArrayList<ShoppingListItem>>() {}.type)
    }

    @TypeConverter
    fun shoppingListItemsToString(list: ArrayList<ShoppingListItem>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ShoppingListItem>>() {

        }.type
        return gson.toJson(list, type)
    }
}