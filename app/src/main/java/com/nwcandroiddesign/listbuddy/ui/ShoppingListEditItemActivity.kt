package com.nwcandroiddesign.listbuddy.ui


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.nwcandroiddesign.listbuddy.Injection
import com.nwcandroiddesign.listbuddy.R
import com.nwcandroiddesign.listbuddy.db.ShoppingListItem
import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
import java.util.*


class ShoppingListEditItemActivity : AppCompatActivity() {


    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ShoppingListViewModel

    companion object {
        const val EXTRA_NAME = "itemName"
        const val EXTRA_ID = "id"
        const val EXTRA_LIST_ITEM_ID = "itemId"
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
        title = "Edit Item"
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
            val position = intent.getIntExtra(EXTRA_LIST_ITEM_ID, 0)
            val listId = intent.getIntExtra(EXTRA_ID, 0)
            val name = name_edit_text.text.toString()
            val itemName = ShoppingListItem(name, false, Date())
            viewModel.updateItemName(itemName, listId, position)
        }
        finish()
    }
}


