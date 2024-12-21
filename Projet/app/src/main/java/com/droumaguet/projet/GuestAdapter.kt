package com.droumaguet.projet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class GuestAdapter(
    private val context: Context,
    private val dataSource: ArrayList<String>
) : BaseAdapter()
{
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.guest_item, parent, false)

        val guest = getItem(position)

        val guestField: TextView = rowView.findViewById(R.id.guestLogin)
        guestField.text = guest.toString()

        val btnRemove: Button = rowView.findViewById(R.id.btnDeleteGuest)
        btnRemove.setOnClickListener {
            if (context is GuestManagementActivity) {
                context.deleteGuest(guest.toString())
            }
        }

        return rowView
    }
}