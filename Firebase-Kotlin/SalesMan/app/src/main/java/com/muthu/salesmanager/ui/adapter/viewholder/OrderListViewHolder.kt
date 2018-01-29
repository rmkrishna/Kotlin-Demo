package com.muthu.salesmanager.ui.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Order
import com.muthu.salesmanager.model.Product

/**
 * Created by muthu on 28/1/18.
 */
class OrderListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var titleView: TextView
    var count: TextView
    var date: TextView
    var priceValue: TextView
    var address: TextView

    init {
        titleView = itemView.findViewById(R.id.product_title)
        count = itemView.findViewById(R.id.count)
        priceValue = itemView.findViewById(R.id.price_value)
        date = itemView.findViewById(R.id.date)
        address = itemView.findViewById(R.id.address)
    }

    fun bindToPost(order: Order) {
        titleView.setText(order.userName)
        count.setText("" + order.totalProduct)
        priceValue.setText(order.totalAmount)
        date.setText(order.dateTime)
        address.setText(order.shopName + " \n " + order.address + " \n " + order.pincode + " \n " + order.phone)

    }
}