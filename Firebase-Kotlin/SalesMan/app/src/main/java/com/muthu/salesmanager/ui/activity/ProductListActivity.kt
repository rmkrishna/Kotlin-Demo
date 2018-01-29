package com.muthu.salesmanager.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.muthu.salesmanager.R

class ProductListActivity : AppCompatActivity() {

    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_list)

        mDatabase = FirebaseDatabase.getInstance().reference

    }
}
