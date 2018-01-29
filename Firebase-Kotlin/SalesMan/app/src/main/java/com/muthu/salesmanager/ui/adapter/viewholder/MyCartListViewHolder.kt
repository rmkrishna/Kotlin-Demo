package com.muthu.salesmanager.ui.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Product

/**
 * Created by muthu on 28/1/18.
 */
class MyCartListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var titleView: TextView
    var count: TextView
    var removeBtn: ImageButton

    init {
        titleView = itemView.findViewById(R.id.product_title)
        count = itemView.findViewById(R.id.count)
        removeBtn = itemView.findViewById(R.id.remove)
    }

    fun bindToPost(product: Product, plusClickListener: View.OnClickListener, usetId: String) {
        titleView.setText(product.productName)

        count.setText("0")

        removeBtn.isEnabled = true

        product.let {
            var countValue = product.stars.get(usetId)

            countValue?.let { value ->

                count.setText("" + value)
            }
        }

        removeBtn.setOnClickListener(plusClickListener)
    }
}