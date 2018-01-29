package com.muthu.salesmanager.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Product
import com.muthu.salesmanager.ui.adapter.viewholder.MyCartListViewHolder
import com.muthu.salesmanager.ui.adapter.viewholder.ProductListViewHolder

/**
 * Created by muthu on 28/1/18.
 */
class MyCartListAdapter(var userId: String) : RecyclerView.Adapter<MyCartListViewHolder>() {

    var products: ArrayList<Product> = arrayListOf()
        set(value) {
            products.clear()

            if (!value.isEmpty()) {
                products.addAll(value)
            }

            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyCartListViewHolder {
        val inflater = LayoutInflater.from(parent?.context)

        return MyCartListViewHolder(inflater.inflate(R.layout.layout_holder_my_cart_list, parent, false))
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: MyCartListViewHolder?, position: Int) {
        var product: Product = products.get(position)

        holder?.bindToPost(product, View.OnClickListener {

        }, userId)
    }
}