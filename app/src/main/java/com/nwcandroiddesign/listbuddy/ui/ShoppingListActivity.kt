package com.nwcandroiddesign.listbuddy.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nwcandroiddesign.listbuddy.Injection
import com.nwcandroiddesign.listbuddy.R
import com.nwcandroiddesign.listbuddy.db.ShoppingListItem
import com.nwcandroiddesign.listbuddy.dto.ShoppingListDTO
import com.nwcandroiddesign.listbuddy.ui.adapter.ShoppingListAdapter
import com.nwcandroiddesign.listbuddy.ui.listeners.RecyclerItemTouchHelper
import com.nwcandroiddesign.listbuddy.ui.listeners.RecyclerViewClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_shopping_list.*
import kotlinx.android.synthetic.main.content_shopping_list.*
import java.util.*


class ShoppingListActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
    RecyclerViewClickListener {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ShoppingListViewModel
    private val disposable = CompositeDisposable()
    private var shoppingList = ArrayList<ShoppingListDTO>()
    private var mAdapter: ShoppingListAdapter? = null
    private var dialogCreateNamePositiveButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        setSupportActionBar(toolbar)

        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShoppingListViewModel::class.java)

        mAdapter = ShoppingListAdapter(shoppingList, this, this)

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                this,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.adapter = mAdapter


        fab.setOnClickListener {
            val alertDialogAndroid = getShoppingListDialog()
            alertDialogAndroid?.show()

            dialogCreateNamePositiveButton = alertDialogAndroid?.getButton(DialogInterface.BUTTON_POSITIVE)
            dialogCreateNamePositiveButton?.isEnabled = false

        }

        val itemTouchHelperCallback1 = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                // Row is swiped from recycler view
                // remove it from adapter
                if (viewHolder is ShoppingListAdapter.ViewHolder) {
                    // get the removed item itemName to display it in snack bar
                    val name = shoppingList[viewHolder.adapterPosition].name

                    // backup of removed item for undo purpose
                    val deletedItem = shoppingList[viewHolder.adapterPosition]
                    val deletedIndex = viewHolder.adapterPosition

                    // remove the item from recycler view
                    mAdapter?.removeItem(viewHolder.adapterPosition)
                    viewModel.archiveItem(deletedItem)


                    // showing snack bar with Undo option
                    val snackBar = Snackbar.make(coordinatorLayout, "$name is deleted!", Snackbar.LENGTH_LONG)
                    snackBar.setAction("UNDO") {
                        // undo is selected, restore the deleted item
                        mAdapter?.restoreItem(deletedItem, deletedIndex)
                        viewModel.reArchiveItem(deletedItem)

                    }
                    snackBar.setActionTextColor(Color.GREEN)
                    snackBar.show()
                }
            }

             override fun onChildDraw(c: Canvas, recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView)

    }

    override fun onStart() {
        super.onStart()
        disposable.add(viewModel.getShoppingLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { t ->
                    shoppingList.clear()
                    t?.forEach {
                        val completed = it.items.filter(ShoppingListItem::isCompleted)
                        val item = ShoppingListDTO(
                            it.id,
                            it.name,
                            it.timestamp,
                            it.isArchived,
                            completed.size,
                            it.items.size
                        )
                        shoppingList.add(item)
                    }

                    mAdapter?.notifyDataSetChanged()
                })
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    private fun goToArchiveListActivity() {
        val intent = Intent(this, ArchiveListActivity::class.java)
        startActivity(intent)
    }

    private fun goToShoppingListDetailsActivity(id: Int, isArchived: Boolean, listName: String) {
        val intent = Intent(this, ShoppingListDetailsActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("isArchived", isArchived)
        intent.putExtra("listName", listName)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_archived_list -> Toast.makeText(this, "working on this", Toast.LENGTH_SHORT).show() //goToArchiveListActivity()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int, position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        val id = shoppingList[position].id
        val listName = shoppingList[position].name
        val isArchived = shoppingList[position].isArchived
        goToShoppingListDetailsActivity(id, isArchived, listName)
    }

    @SuppressLint("CheckResult")
    fun getShoppingListDialog(): AlertDialog? {
        val layoutInflaterAndroid = LayoutInflater.from(this)
        val mView = layoutInflaterAndroid.inflate(R.layout.dialog_input_list_name, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(this)
        alertDialogBuilderUserInput.setView(mView)


        val nameInputDialogTextInputLayout = mView.findViewById(R.id.nameInputDialogTextInputLayout) as TextInputLayout
        val userInputDialogEditText = mView.findViewById(R.id.nameInputDialog) as EditText
        val itemInputNameObservable = RxTextView.textChanges(userInputDialogEditText)
                .map { inputText: CharSequence -> inputText.isEmpty() }
                .distinctUntilChanged()

        itemInputNameObservable.subscribe { inputIsEmpty: Boolean ->
            inputIsEmpty.toString()//  Log.v("itemInputNameObservable",

            nameInputDialogTextInputLayout.error = "Name must not be empty"
            nameInputDialogTextInputLayout.isErrorEnabled = inputIsEmpty

            dialogCreateNamePositiveButton?.isEnabled = !inputIsEmpty
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Create") { _, _ ->
                    viewModel.createShoppingList(userInputDialogEditText.text.toString())
                }

                .setNegativeButton("Cancel"
                ) { dialogBox, _ -> dialogBox.cancel() }

        return alertDialogBuilderUserInput.create()
    }
}
