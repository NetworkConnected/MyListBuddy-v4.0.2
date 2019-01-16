package com.nwcandroiddesign.listbuddy

import android.content.Context
import com.nwcandroiddesign.listbuddy.db.ShoppingListDao
import com.nwcandroiddesign.listbuddy.db.ShoppingListDatabase
import com.nwcandroiddesign.listbuddy.ui.ViewModelFactory

object Injection {
    private fun provideUserDataSource(context: Context): ShoppingListDao {
        val database = ShoppingListDatabase.getInstance(context)
        return database.shoppingListDao()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataSource = provideUserDataSource(context)
        return ViewModelFactory(dataSource)
    }
}