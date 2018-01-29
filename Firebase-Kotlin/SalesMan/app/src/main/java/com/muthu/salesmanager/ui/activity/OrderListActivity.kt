package com.muthu.salesmanager.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Order
import com.muthu.salesmanager.ui.adapter.OrderListAdapter
import com.muthu.salesmanager.ui.adapter.viewholder.OrderListViewHolder

class OrderListActivity : AppCompatActivity() {

    private var mDatabase: DatabaseReference? = null
    // [END define_database_reference]

    private var mAdapter: FirebaseRecyclerAdapter<Order, OrderListViewHolder>? = null
    private var mRecycler: RecyclerView? = null
    private var mManager: LinearLayoutManager? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_order_list)

        mAuth = FirebaseAuth.getInstance()

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().reference
        // [END create_database_reference]

        mRecycler = findViewById(R.id.order_list)
        mRecycler?.setHasFixedSize(true)
        mManager = LinearLayoutManager(this)
        mRecycler?.setLayoutManager(mManager)

        val query = mDatabase?.child("orders")

        val options = FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query!!, Order::class.java!!)
                .build()

        mAdapter = OrderListAdapter(options)

        mRecycler?.adapter = mAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            finish()
        }
        return true
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
