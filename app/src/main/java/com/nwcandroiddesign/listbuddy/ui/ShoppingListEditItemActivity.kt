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
import com.nwcandroiddesign.listbuddy.dto.ShoppingListItemDTO
import com.nwcandroiddesign.listbuddy.ui.adapter.ShoppingListDetailsAdapter
import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
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
        const val EXTRA_NAME = "name"
        const val EXTRA_ID = "id"
        //   const val EXTRA_TIMESTAMP = "com.nwcandroiddesign.listbuddy.ui.EXTRA_TIMESTAMP"
        //   const val EXTRA_ISCOMPLETED = "com.nwcandroiddesign.listbuddy.ui.EXTRA_ISCOMPLETED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val name = intent.getStringExtra("name")
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
            val listId = intent.getIntExtra(EXTRA_ID, 0)
            val itemName = name_edit_text.text.toString()
            val shoppingList = ShoppingList(listId, itemName, false, Date(), items = ArrayList())
            viewModel.updateItemName(itemName, shoppingList, listId)
        }
        setResult(Activity.RESULT_OK)
        finish()
    }
}

