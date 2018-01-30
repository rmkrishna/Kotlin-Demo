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
class ProductListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var titleView: TextView
    var count: TextView
    var plusBtn: ImageButton
    var removeBtn: ImageButton
    var totlCount: TextView
    var priceValue: TextView
    var icon: ImageView

    init {
        titleView = itemView.findViewById(R.id.product_title)
        totlCount = itemView.findViewById(R.id.available_count)
        count = itemView.findViewById(R.id.count)
        plusBtn = itemView.findViewById(R.id.add)
        removeBtn = itemView.findViewById(R.id.remove)
        priceValue = itemView.findViewById(R.id.price_value)
        icon = itemView.findViewById(R.id.icon)
    }

    fun bindToPost(product: Product, plusClickListener: View.OnClickListener, minusClickListener: View.OnClickListener, usetId: String) {
        titleView.setText(product.productName)

        count.setText("0")
        totlCount.setText("" + product.availableCount)

        plusBtn.isEnabled = true

        println("product type " + product.brandType)

        if ("bulb".contains(product.brandType!!.toLowerCase(), true)) {
            if ("sony".contains(product.productType!!.toLowerCase(), true)) {
                icon.setBackgroundResource(R.drawable.bulb1)
            } else {
                icon.setBackgroundResource(R.drawable.bulb2)
            }
        } else {
            icon.setBackgroundResource(R.drawable.tube1)
        }


        priceValue.setText("" + product.price)

        product.let {
            var countValue = product.stars.get(usetId)

            countValue?.let { value ->

                count.setText("" + value)
                if (value > 0) {
                    var newCount = product.availableCount - value

                    if (newCount <= 0) {
                        plusBtn.isEnabled = false
                    } else {
                        plusBtn.isEnabled = true
                    }

                    totlCount.setText("" + (product.availableCount - value))
                } else {
                    totlCount.setText("" + product.availableCount)
                }
            }
        }

        plusBtn.setOnClickListener(plusClickListener)
        removeBtn.setOnClickListener(minusClickListener)
    }
}