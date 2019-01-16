package com.nwcandroiddesign.listbuddy.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "shopping_list")
data class ShoppingList(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int = 0,
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "is_archived")
        val isArchived: Boolean,
        @ColumnInfo(name = "timestamp")
        val timestamp: Date,
        @ColumnInfo(name = "items")
        val items: ArrayList<ShoppingListItem> )

data class ShoppingListItem(
        val name: String,
        val isCompleted: Boolean,
        val timestamp: Date
)
