package com.nwcandroiddesign.listbuddy.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nwcandroiddesign.listbuddy.R
import com.nwcandroiddesign.listbuddy.dto.ShoppingListDTO
import com.nwcandroiddesign.listbuddy.ui.listeners.RecyclerViewClickListener
import java.text.SimpleDateFormat
import java.util.*


class ShoppingListAdapter(private val list: ArrayList<ShoppingListDTO>, val context: Context,
                          private val listener: RecyclerViewClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.timestamp.text = convertDateTime(item.timeStamp)
        holder.completedShoppingListItems.text = item.itemsCompletedCount.toString()
        holder.allShoppingListItems.text = item.itemsAllCount.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    inner class ViewHolder(view: View, clickListener: RecyclerViewClickListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view),
        View.OnClickListener {

        var name: TextView = view.findViewById(R.id.name)
        var timestamp: TextView = view.findViewById(R.id.timestamp)
        var completedShoppingListItems: TextView = view.findViewById(R.id.completedShoppingListItems)
        var allShoppingListItems: TextView = view.findViewById(R.id.allShoppingListItems)
        private var viewClickListener: RecyclerViewClickListener? = null


        init {
            viewClickListener = clickListener
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            viewClickListener?.onClick(v, adapterPosition)
        }
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(shoppingListItem: ShoppingListDTO, position: Int) {
        list.add(position, shoppingListItem)
        notifyItemInserted(position)
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateTime(date: Date): String? {
        val dateFormat = SimpleDateFormat("MMM d, yyyy")
        return dateFormat.format(date)
    }
}
