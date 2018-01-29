package com.muthu.salesmanager.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Product
import com.muthu.salesmanager.ui.adapter.ProductListAdapter
import com.muthu.salesmanager.ui.adapter.viewholder.ProductListViewHolder

class ProductListFragment : Fragment(), ProductListAdapter.OnActionListener {

    private var mDatabase: DatabaseReference? = null
    // [END define_database_reference]

    private var mAdapter: FirebaseRecyclerAdapter<Product, ProductListViewHolder>? = null
    private var mRecycler: RecyclerView? = null
    private var mManager: LinearLayoutManager? = null

    private var mAuth: FirebaseAuth? = null

    var myCatTv: TextView? = null;

    interface OnActionListener {
        fun onAddedProduct(product: Product): Int
        fun onRemovedProduct(product: Product): Int

        fun openCart()
    }

    var mListener: OnActionListener? = null;

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnActionListener) {
            mListener = context
        }
    }

    override fun onAddedProduct(product: Product) {
        if (mListener != null) {
            var canShow = mListener?.onAddedProduct(product)

            canShow?.let {
                activity?.let { activty ->
                    activty.runOnUiThread() {
                        if (it > 0) {
                            myCatTv?.visibility = View.VISIBLE
                            myCatTv?.setText("Order(" + it + ")")
                        } else {
                            myCatTv?.visibility = View.GONE
                            myCatTv?.setText("No Order")
                        }
                    }
                }

            }
        }
    }

    override fun onRemovedProduct(product: Product) {
        if (mListener != null) {
            var canShow = mListener?.onRemovedProduct(product)

            canShow?.let {
                activity?.let { activty ->
                    activty.runOnUiThread() {
                        if (it > 0) {
                            myCatTv?.visibility = View.VISIBLE
                            myCatTv?.setText("Order(" + it + ")")
                        } else {
                            myCatTv?.visibility = View.GONE
                            myCatTv?.setText("No Order")
                        }
                    }
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view: View = inflater.inflate(R.layout.fragment_product_list, container, false)

        myCatTv = view.findViewById(R.id.main_tv_my_cart)

        myCatTv?.visibility = View.VISIBLE

        myCatTv?.setOnClickListener {
            mListener?.let {
                it.openCart()
            }
        }

        mAuth = FirebaseAuth.getInstance()

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().reference
        // [END create_database_reference]

        mRecycler = view.findViewById(R.id.product_list)
        mRecycler?.setHasFixedSize(true)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mManager = LinearLayoutManager(activity)
        mRecycler?.setLayoutManager(mManager)

        val query = mDatabase?.child("products")

        val options = FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query!!, Product::class.java!!)
                .build()

        mAdapter = ProductListAdapter(options, mAuth?.currentUser?.uid!!, mDatabase!!, this)


        mRecycler?.adapter = mAdapter
    }

    override fun onStart() {
        super.onStart()

        mAdapter?.let {
            it.startListening()
        }
    }


    override fun onStop() {
        super.onStop()

        mAdapter?.let {
            it.stopListening()
        }
    }
}
