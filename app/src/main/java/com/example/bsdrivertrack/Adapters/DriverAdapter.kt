package com.example.bsdrivertrack.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bsdrivertrack.Models.DriverModel
import com.example.bsdrivertrack.R

class DriverAdapter (private var driverList: List<DriverModel>, private  val context: Context)
    : RecyclerView.Adapter<DriverAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driverModel=driverList.get(position)
//        holder.driver_numberTextView.text=driverModel.username
//        holder.driver_numberTextView.text=driverModel.driver_number

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_driver, parent, false
            )
        )
    }
    override fun getItemCount(): Int {
        return driverList.size
    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
        var driver_numberTextView: TextView = itemLayoutView.findViewById(R.id.driver_number)

    }

}