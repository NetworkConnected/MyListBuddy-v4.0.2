package com.nwcandroiddesign.listbuddy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nwcandroiddesign.listbuddy.db.ShoppingListDao

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(private val dataSource: ShoppingListDao) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
                return ShoppingListViewModel(dataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
