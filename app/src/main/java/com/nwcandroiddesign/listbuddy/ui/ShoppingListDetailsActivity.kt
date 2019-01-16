package com.nwcandroiddesign.listbuddy.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nwcandroiddesign.listbuddy.Injection
import com.nwcandroiddesign.listbuddy.R
import com.nwcandroiddesign.listbuddy.dto.ShoppingListItemDTO
import com.nwcandroiddesign.listbuddy.ui.adapter.ShoppingListDetailsAdapter
import com.nwcandroiddesign.listbuddy.ui.listeners.RecyclerItemTouchHelper
import com.nwcandroiddesign.listbuddy.ui.listeners.RecyclerViewClickListener
import com.nwcandroiddesign.listbuddy.ui.listeners.ShoppingItemCheckboxListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_shopping_list_item.*
import kotlinx.android.synthetic.main.content_shopping_list_item.*
import java.util.*

class ShoppingListDetailsActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
    ShoppingItemCheckboxListener, RecyclerViewClickListener {

    companion object {
        const val EDIT_ITEM_REQUEST = 1
        const val EXTRA_ID = "id"
    }

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ShoppingListViewModel
    private var intExtra: Int? = null
    private var isArchived: Boolean? = null
    private val disposable = CompositeDisposable()
    private var shoppingListItems = ArrayList<ShoppingListItemDTO>()
    private var mAdapter: ShoppingListDetailsAdapter? = null
    private var dialogCreateNamePositiveButton: Button? = null
    private val paint = Paint()


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val listTitle = intent.getStringExtra("listName")
        title = listTitle

        intExtra = intent.getIntExtra("id", 0)
        isArchived = intent.getBooleanExtra("isArchived", false)

        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShoppingListViewModel::class.java)

        mAdapter = ShoppingListDetailsAdapter(shoppingListItems, this, this, this, isArchived!!)

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

        if (isArchived as Boolean) {
            fab.visibility = View.GONE
        } else {
            fab.setOnClickListener { _ ->
                val alertDialogAndroid = getShoppingListItemsDialog()
                alertDialogAndroid?.show()

                dialogCreateNamePositiveButton = alertDialogAndroid?.getButton(DialogInterface.BUTTON_POSITIVE)
                dialogCreateNamePositiveButton?.isEnabled = false
            }

        }

        val itemTouchHelperCallback1 = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                // Row is swiped from recycler view
                // remove it from adapter
                if (viewHolder is ShoppingListDetailsAdapter.ViewHolder) {
                    // get the removed item name to display it in snack bar
                    val name = shoppingListItems[viewHolder.adapterPosition].name

                    // backup of removed item for undo purpose
                    val deletedItem = shoppingListItems[viewHolder.adapterPosition]
                    val deletedIndex = viewHolder.adapterPosition

                    // remove the item from recycler view
                    mAdapter?.removeItem(viewHolder.adapterPosition)
                    viewModel.removeShoppingListItem(deletedItem, intExtra!!)

                    // showing snack bar with Undo option
                    val snackBar = Snackbar.make(coordinatorLayout, "$name is deleted!", Snackbar.LENGTH_LONG)
                    snackBar.setAction("UNDO") {
                        // undo is selected, restore the deleted item
                        mAdapter?.restoreItem(deletedItem, deletedIndex)
                        viewModel.restoreShoppingListItem(deletedItem, intExtra!!)
                    }
                    snackBar.setActionTextColor(Color.GREEN)
                    snackBar.show()
                }

                Log.v("Test", "List Details Test")
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: androidx.recyclerview.widget.RecyclerView,
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean) {
                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        paint.color = Color.parseColor("#388E3C")
                        val background =
                            RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, paint)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_delete_white)
                        val iconDest = RectF(
                            itemView.left.toFloat() + width,
                            itemView.top.toFloat() + width,
                            itemView.left.toFloat() + 2 * width,
                            itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, iconDest, paint)
                    } else {
                        paint.color = Color.parseColor("#388E3C")
                        val background = RectF(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                        )
                        c.drawRect(background, paint)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_delete_white)
                        val iconDest = RectF(
                            itemView.right.toFloat() - 2 * width,
                            itemView.top.toFloat() + width,
                            itemView.right.toFloat() - width,
                            itemView.bottom.toFloat() - width
                        )
                        c.drawBitmap(icon, null, iconDest, paint)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        if (!isArchived!!)
            ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView)
    }

    @SuppressLint("CheckResult")
    private fun getShoppingListItemsDialog(): AlertDialog? {
        val layoutInflaterAndroid = LayoutInflater.from(this)
        val mView = layoutInflaterAndroid.inflate(R.layout.dialog_input_item_name, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(this)
        alertDialogBuilderUserInput.setView(mView)

        val nameInputDialogTextInputLayout =
            mView.findViewById(R.id.itemNameInputDialogTextInputLayout) as TextInputLayout
        val userInputDialogEditText = mView.findViewById(R.id.itemNameEditText) as EditText
        val itemInputNameObservable = RxTextView.textChanges(userInputDialogEditText)
            .map { inputText: CharSequence -> inputText.isEmpty() }
            .distinctUntilChanged()

        itemInputNameObservable.subscribe { inputIsEmpty: Boolean ->
            Log.v("itemInputNameObservable", inputIsEmpty.toString())

            nameInputDialogTextInputLayout.error = "Name must not be empty"
            nameInputDialogTextInputLayout.isErrorEnabled = inputIsEmpty
            dialogCreateNamePositiveButton?.isEnabled = !inputIsEmpty
        }

        alertDialogBuilderUserInput
            .setCancelable(false)
            .setPositiveButton("Create") { _, _ ->

                viewModel.createShoppingListItem(userInputDialogEditText.text.toString(), intExtra!!)
            }

            .setNegativeButton(
                "Cancel"
            ) { dialogBox, _ -> dialogBox.cancel() }

        return alertDialogBuilderUserInput.create()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        if (intExtra != null)
            disposable.add(viewModel.getShoppingList(intExtra!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { t ->
                    shoppingListItems.clear()
                    t.items.forEach {
                        val item = ShoppingListItemDTO(
                            it.name,
                            it.isCompleted,
                            it.timestamp
                        )
                        shoppingListItems.add(item)
                    }

                    mAdapter?.notifyDataSetChanged()
                })
    }

    override fun onStop() {
        super.onStop()
        // clear subscriptions
        disposable.clear()
    }

    override fun onClick(position: Int, isChecked: Boolean) {
        shoppingListItems[position].isCompleted = isChecked
        viewModel.updateShoppingList(shoppingListItems, intExtra!!)

    }

    override fun onSwiped(
        viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        direction: Int,
        position: Int) {
    }

    override fun onClick(view: View, position: Int) {
        val name = shoppingListItems[position].name
        val intent = Intent(this, ShoppingListEditItemActivity::class.java)
        intent.putExtra(ShoppingListEditItemActivity.EXTRA_NAME, name)
        intent.putExtra(ShoppingListEditItemActivity.EXTRA_ID, position)
        startActivityForResult(intent, EDIT_ITEM_REQUEST)
    }
}