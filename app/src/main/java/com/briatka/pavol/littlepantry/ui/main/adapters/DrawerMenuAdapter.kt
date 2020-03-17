package com.briatka.pavol.littlepantry.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.briatka.pavol.littlepantry.R

class DrawerMenuAdapter(context: Context) : BaseAdapter() {

    private lateinit var menuItems: List<String>

    private var inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val root = convertView ?: inflater.inflate(R.layout.drawer_menu_item, parent, false)

        root.findViewById<TextView>(R.id.tv_menu_item_drawer).text = getItem(position) as CharSequence

        return root
    }

    override fun getItem(position: Int): Any {
        return menuItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return menuItems.size
    }

    fun setMenuItems(items: List<String>) {
        menuItems = items
    }
}