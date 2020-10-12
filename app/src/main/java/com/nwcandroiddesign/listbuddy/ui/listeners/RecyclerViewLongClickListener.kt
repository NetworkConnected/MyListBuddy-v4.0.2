package com.nwcandroiddesign.listbuddy.ui.listeners

import android.view.View
//long click for editing items names
interface RecyclerViewLongClickListener : RecyclerViewClickListener {

    fun onLongClick(view: View, position: Int)
}