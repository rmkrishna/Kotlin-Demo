package com.muthu.salesmanager.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Order
import com.muthu.salesmanager.model.Product
import com.muthu.salesmanager.ui.adapter.viewholder.OrderListViewHolder
import com.muthu.salesmanager.ui.adapter.viewholder.ProductListViewHolder


/**
 * Created by muthu on 28/1/18.
 */
class OrderListAdapter(options: FirebaseRecyclerOptions<Order>) : FirebaseRecyclerAdapter<Order, OrderListViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): OrderListViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        return OrderListViewHolder(inflater.inflate(R.layout.layout_holder_order_list, parent, false))
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int, model: Order) {
        holder.bindToPost(model)
    }

}