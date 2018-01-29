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
import com.muthu.salesmanager.model.Order
import com.muthu.salesmanager.model.Product
import com.muthu.salesmanager.ui.adapter.OrderListAdapter
import com.muthu.salesmanager.ui.adapter.ProductListAdapter
import com.muthu.salesmanager.ui.adapter.viewholder.OrderListViewHolder
import com.muthu.salesmanager.ui.adapter.viewholder.ProductListViewHolder

class OrderListFragment : Fragment() {

    private var mDatabase: DatabaseReference? = null
    // [END define_database_reference]

    private var mAdapter: FirebaseRecyclerAdapter<Order, OrderListViewHolder>? = null
    private var mRecycler: RecyclerView? = null
    private var mManager: LinearLayoutManager? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view: View = inflater.inflate(R.layout.fragment_order_list, container, false)

        mAuth = FirebaseAuth.getInstance()

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().reference
        // [END create_database_reference]

        mRecycler = view.findViewById(R.id.order_list)
        mRecycler?.setHasFixedSize(true)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mManager = LinearLayoutManager(activity)
        mRecycler?.setLayoutManager(mManager)

        val query = mDatabase?.child("user-orders")?.child(mAuth?.currentUser?.uid)

        val options = FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query!!, Order::class.java!!)
                .build()

        mAdapter = OrderListAdapter(options)

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
