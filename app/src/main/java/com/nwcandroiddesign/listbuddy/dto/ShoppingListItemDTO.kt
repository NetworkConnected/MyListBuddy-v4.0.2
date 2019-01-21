package com.nwcandroiddesign.listbuddy.dto

import java.util.*

data class ShoppingListItemDTO(
    var itemName: String,
    var isCompleted: Boolean,
    val timestamp: Date
)
