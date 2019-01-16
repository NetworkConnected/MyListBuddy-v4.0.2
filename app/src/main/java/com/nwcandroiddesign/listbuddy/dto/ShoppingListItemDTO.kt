package com.nwcandroiddesign.listbuddy.dto

import java.util.*

data class ShoppingListItemDTO(
        var name: String,
        var isCompleted: Boolean,
        val timestamp: Date
)
