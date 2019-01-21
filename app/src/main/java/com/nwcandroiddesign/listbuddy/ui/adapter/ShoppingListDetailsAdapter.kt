package com.nwcandroiddesign.listbuddy.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.nwcandroiddesign.listbuddy.R
import com.nwcandroiddesign.listbuddy.dto.ShoppingListItemDTO
import com.nwcandroiddesign.listbuddy.ui.listeners.RecyclerViewClickListener
import com.nwcandroiddesign.listbuddy.ui.listeners.ShoppingItemCheckboxListener


class ShoppingListDetailsAdapter(
    private val list: ArrayList<ShoppingListItemDTO>,
    val context: Context,
    private val listener: ShoppingItemCheckboxListener,
    private val clickListener: RecyclerViewClickListener,
    private val isArchived: Boolean) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ShoppingListDetailsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_shopping_list_element, parent, false)
        return ViewHolder(itemView, clickListener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.itemName
        if (item.isCompleted) {
            holder.isCompleted.isChecked = true
        }
        holder.isCompleted.isEnabled = !isArchived

        holder.isCompleted.setOnCheckedChangeListener { _, isChecked ->
            item.isCompleted = isChecked
            listener.onClick(position, isChecked)
        }
    }

    inner class ViewHolder(view: View, clickListener: RecyclerViewClickListener) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var name: TextView = view.findViewById(R.id.itemName)
        var isCompleted: CheckBox = view.findViewById(R.id.checkbox)
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
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position)
    }

    fun restoreItem(item: ShoppingListItemDTO, position: Int) {
        list.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }
}

