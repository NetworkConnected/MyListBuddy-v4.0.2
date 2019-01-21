package com.nwcandroiddesign.listbuddy.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.nwcandroiddesign.listbuddy.Injection
import com.nwcandroiddesign.listbuddy.R
import com.nwcandroiddesign.listbuddy.db.ShoppingList
import com.nwcandroiddesign.listbuddy.db.ShoppingListItem
import com.nwcandroiddesign.listbuddy.dto.ShoppingListItemDTO
import com.nwcandroiddesign.listbuddy.ui.adapter.ShoppingListDetailsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
import kotlinx.android.synthetic.main.item_shopping_list.*
import kotlinx.android.synthetic.main.item_shopping_list_element.*
import java.util.*
import kotlin.collections.ArrayList

class ShoppingListEditItemActivity : AppCompatActivity() {


    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ShoppingListViewModel
    private var shoppingListItems = ArrayList<ShoppingListItemDTO>()
    private val viewHolder: ShoppingListDetailsAdapter.ViewHolder? = null
    private var mAdapter: ShoppingListDetailsAdapter? = null
    private var isArchived: Boolean? = null


    companion object {
        const val EXTRA_NAME = "itemName"
        const val EXTRA_ID = "id"
        const val EXTRA_LIST_ITEM_ID ="itemId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val name = intent.getStringExtra("itemName")
        title = "Edit Items"
        name_edit_text.setText(name)
        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShoppingListViewModel::class.java)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save_todo -> {
                saveItem()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveItem() {
        if (name_edit_text.text.toString().isBlank())
            Toast.makeText(this, "Can not insert empty Item!", Toast.LENGTH_SHORT).show()
        else {
            val position  = intent.getIntExtra(EXTRA_LIST_ITEM_ID,0)
            val listId = intent.getIntExtra(EXTRA_ID, 0)
            val name = name_edit_text.text.toString()
            val itemName = ShoppingListItem(name,false, Date())
                viewModel.updateItemName(itemName, listId, position)
            }
            finish()
        }
    }


