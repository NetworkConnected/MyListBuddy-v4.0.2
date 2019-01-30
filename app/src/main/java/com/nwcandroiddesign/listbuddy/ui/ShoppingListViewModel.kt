package com.nwcandroiddesign.listbuddy.ui

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.nwcandroiddesign.listbuddy.db.ShoppingList
import com.nwcandroiddesign.listbuddy.db.ShoppingListDao
import com.nwcandroiddesign.listbuddy.db.ShoppingListItem
import com.nwcandroiddesign.listbuddy.dto.ShoppingListDTO
import com.nwcandroiddesign.listbuddy.dto.ShoppingListItemDTO
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class ShoppingListViewModel(private val dataSource: ShoppingListDao) : ViewModel() {

    fun createShoppingList(listName: String) {
        val arrayList = ArrayList<ShoppingListItem>()
        val shoppingList = ShoppingList(name = listName, isArchived = false, items = arrayList, timestamp = Date())
        Completable.fromCallable { dataSource.insertShoppingList(shoppingList) }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    @SuppressLint("CheckResult")
    fun createShoppingListItem(itemName: String, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
            .firstElement()
            .subscribe { shoppingList: ShoppingList ->
                val items = shoppingList.items
                items.add(ShoppingListItem(itemName, false, Date()))
                dataSource.updateShoppingList(shoppingList = shoppingList)
            }
    }


    @SuppressLint("CheckResult")
    fun updateItemName(renameItemName: ShoppingListItem, shoppingListId: Int, position: Int) {
        dataSource.getShoppingList(shoppingListId)
            .firstElement()
            .subscribe { shoppingList: ShoppingList ->
                val items: ArrayList<ShoppingListItem> = shoppingList.items
                items.set(position, renameItemName).itemName
                dataSource.updateShoppingList(
                    shoppingList = ShoppingList(
                        id = shoppingList.id,
                        name = shoppingList.name,
                        isArchived = shoppingList.isArchived,
                        timestamp = shoppingList.timestamp,
                        items = items
                    )
                )
            }
    }
    fun getShoppingLists(): Flowable<List<ShoppingList>> {
        return dataSource.getActiveShoppingLists()
            .map { t ->
                t.sortedBy { it.timestamp }
            }
    }

    fun getArchivedLists(): Flowable<List<ShoppingList>> {
        return dataSource.getArchivedShoppingLists()
            .map { t ->
                t.sortedByDescending { it.timestamp }
            }
    }

    fun getShoppingList(id: Int): Flowable<ShoppingList> {
        return dataSource.getShoppingList(id)
    }

    fun archiveItem(deletedShoppingListItem: ShoppingListDTO) {
        Completable.fromCallable { dataSource.archiveShoppingList(deletedShoppingListItem.id) }
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun reArchiveItem(deletedShoppingListItem: ShoppingListDTO) {
        Completable.fromCallable { dataSource.reArchiveShoppingList(deletedShoppingListItem.id) }
            .subscribeOn(Schedulers.io()).subscribe()
    }

    @SuppressLint("CheckResult")
    fun removeShoppingListItem(deletedItem: ShoppingListItemDTO, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
            .firstElement()
            .subscribe { shoppingList: ShoppingList ->
                val items: ArrayList<ShoppingListItem> = shoppingList.items
                val filter = items.filter {
                    it.timestamp != deletedItem.timestamp
                }
                dataSource.updateShoppingList(
                    shoppingList = ShoppingList(
                        id = shoppingList.id,
                        name = shoppingList.name,
                        isArchived = shoppingList.isArchived,
                        timestamp = shoppingList.timestamp,
                        items = filter as ArrayList<ShoppingListItem>
                    )
                )
            }
    }

    @SuppressLint("CheckResult")
    fun restoreShoppingListItem(deletedItem: ShoppingListItemDTO, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
            .firstElement()
            .subscribe { shoppingList: ShoppingList ->
                val items = shoppingList.items
                items.add(ShoppingListItem(deletedItem.itemName, deletedItem.isCompleted, deletedItem.timestamp))
                dataSource.updateShoppingList(
                    shoppingList = ShoppingList(
                        id = shoppingList.id,
                        name = shoppingList.name,
                        isArchived = shoppingList.isArchived,
                        timestamp = shoppingList.timestamp,
                        items = items
                    )
                )
            }
    }

    @SuppressLint("CheckResult")
    fun updateShoppingList(shoppingList: ArrayList<ShoppingListItemDTO>, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
            .firstElement()
            .subscribe { t: ShoppingList ->
                val dbShoppingList = ArrayList<ShoppingListItem>()
                shoppingList.forEach { it ->
                    dbShoppingList.add(ShoppingListItem(it.itemName, it.isCompleted, it.timestamp))
                }

                dataSource.updateShoppingList(
                    shoppingList = ShoppingList(
                        id = t.id,
                        name = t.name,
                        isArchived = t.isArchived,
                        timestamp = t.timestamp,
                        items = dbShoppingList
                    )
                )
            }
    }
}


