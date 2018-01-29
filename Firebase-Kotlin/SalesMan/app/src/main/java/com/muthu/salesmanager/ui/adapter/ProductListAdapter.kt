package com.muthu.salesmanager.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Product
import com.muthu.salesmanager.ui.adapter.viewholder.ProductListViewHolder


/**
 * Created by muthu on 28/1/18.
 */
class ProductListAdapter(options: FirebaseRecyclerOptions<Product>, var userId: String,
                         var databaseReference: DatabaseReference, var onActionListener: OnActionListener) : FirebaseRecyclerAdapter<Product, ProductListViewHolder>(options) {

    interface OnActionListener {
        fun onAddedProduct(product: Product)
        fun onRemovedProduct(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ProductListViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        return ProductListViewHolder(inflater.inflate(R.layout.layout_holder_product_list, parent, false))
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int, model: Product) {
        val postRef = getRef(position)

        val postKey = postRef.key

        holder.bindToPost(model, View.OnClickListener {
            updateItem(true, postKey)
        }, View.OnClickListener {
            updateItem(false, postKey)
        }, userId)
    }

    fun updateItem(add: Boolean, key: String) {
        val globalPostRef = databaseReference.child("products").child(key)

        globalPostRef?.runTransaction(object : Transaction.Handler {
            override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapShot: DataSnapshot?) {

            }

            override fun doTransaction(mutableData: MutableData?): Transaction.Result {

                val p = mutableData?.getValue<Product>(Product::class.java)
                        ?: return Transaction.success(mutableData)

                p?.let {
                    if (add) {
                        var count = p.stars.get(userId)

                        if (count == null) {
                            count = 0
                        }

                        it.stars.put(userId, count?.plus(1)!!)


                        onActionListener.onAddedProduct(p)

                    } else {
                        if (it.stars.containsKey(userId)) {
                            var count = p.stars.get(userId)

                            count?.let { countIt ->
                                if (countIt > 0) {
                                    it.stars.put(userId, countIt.minus(1))

                                    if (countIt == 1) {
                                        onActionListener.onRemovedProduct(p)
                                    } else {
                                        // We are just updating
                                        // TODO need to write logic later
                                        onActionListener.onAddedProduct(p)
                                    }
                                } else {
                                    it.stars.remove(userId)
                                    onActionListener.onRemovedProduct(p)
                                }
                            }
                        } else {
                            // DO Nothing
                        }
                    }
                }

                mutableData.value = p

                return Transaction.success(mutableData)
            }

        })
    }

}