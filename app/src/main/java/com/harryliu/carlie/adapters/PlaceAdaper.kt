package com.harryliu.carlie.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.gms.location.places.AutocompletePrediction
import com.harryliu.carlie.R

/**
 * @author Harry Liu
 *
 * @version Feb 25, 2018
 */

class PlaceAdapter(context: Context) : ArrayAdapter<AutocompletePrediction>(context, R.layout.list_item_place) {
    private class ViewHolder {
        var placeNameTextView: TextView? = null
        var placeAddressTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val place = getItem(position)
        val newConvertView: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            newConvertView = inflater.inflate(R.layout.list_item_place, parent, false)
            viewHolder = ViewHolder()
            viewHolder.placeNameTextView = newConvertView.findViewById(R.id.place_name_text_view)
            viewHolder.placeAddressTextView = newConvertView.findViewById(R.id.place_address_text_view)
            newConvertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            newConvertView = convertView
        }

        viewHolder.placeNameTextView!!.text = place.getPrimaryText(null)
        viewHolder.placeAddressTextView!!.text = place.getSecondaryText(null)

        return newConvertView
    }
}