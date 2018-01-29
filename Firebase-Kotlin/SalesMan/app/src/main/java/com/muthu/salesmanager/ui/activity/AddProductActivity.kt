package com.muthu.salesmanager.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.database.*
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Category
import com.muthu.salesmanager.model.Product
import com.muthu.salesmanager.util.SimpleCrypto
import com.muthu.salesmanager.util.ext.textWatcher
import java.util.*
import kotlin.collections.ArrayList

class AddProductActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    private var bulbDetailsTv: TextView? = null;
    private var bulbDetailEt: EditText? = null;
    private var bulbPriceEt: EditText? = null;
    private var bulbTotalEt: EditText? = null;
    private var bulbSpinner: Spinner? = null;
    private var brandSpinner: Spinner? = null;

    private var rootView: View? = null;


    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_product)

        mDatabase = FirebaseDatabase.getInstance().reference

        rootView = findViewById<View>(R.id.root_view)

        bulbSpinner = findViewById<Spinner>(R.id.bulbs)
        brandSpinner = findViewById<Spinner>(R.id.brands)

        bulbDetailEt = findViewById<EditText>(R.id.main_et__bulb_detail)
        bulbPriceEt = findViewById<EditText>(R.id.main_et__bulb_price)
        bulbTotalEt = findViewById<EditText>(R.id.main_et__total_item)

        bulbDetailEt?.textWatcher {
            afterTextChanged {
                updateDescription()
            }
        }

        bulbDetailsTv = findViewById<EditText>(R.id.main_tv_bulb_cmt_details)

        findViewById<Button>(R.id.main_btn__submit).setOnClickListener(this)


        getCategory()
        getSubCategory()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            finish()
        }
        return true
    }

    private fun getCategory() {
        val myTopPostsQuery = mDatabase?.child("categories")

        myTopPostsQuery?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var categories: ArrayList<String> = ArrayList();

                for (child in dataSnapshot.children) {
                    val map = child.getValue() as HashMap<String, Any>
                    var category: Category = Category.getMap(map)

                    category?.let {
                        println("category " + category.categoryName)

                        categories.add(category.categoryName);
                    }
                }

                setSpinnerAdapter(bulbSpinner, categories)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun getSubCategory() {
        val myTopPostsQuery = mDatabase?.child("sub_categories")

        myTopPostsQuery?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var categories: ArrayList<String> = ArrayList();

                for (child in dataSnapshot.children) {
                    val map = child.getValue() as HashMap<String, Any>
                    var category: Category = Category.getMap(map)

                    category?.let {
                        println("category " + category.categoryName)

                        categories.add(category.categoryName);
                    }
                }

                setSpinnerAdapter(brandSpinner, categories)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun setSpinnerAdapter(spinner: Spinner?, listId: Int) {
        spinner?.let {
            it.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, resources.getStringArray(listId))

            it.onItemSelectedListener = this
        }
    }

    private fun setSpinnerAdapter(spinner: Spinner?, categories: ArrayList<String>) {
        spinner?.let {
            it.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, categories)

            it.onItemSelectedListener = this
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var id = parent?.id

        when (id) {
            R.id.bulbs -> {
                println("Bulbs")
            }
            R.id.brands -> {
                println("Brands")
            }
        }

        updateDescription()
    }

    private fun updateDescription() {
        var value: String? = bulbDetailEt?.editableText.toString()

        value?.let {
            bulbDetailsTv?.text = bulbSpinner?.selectedItem.toString() + " " + it + " " + brandSpinner?.selectedItem.toString()
        }
    }

    override fun onClick(v: View?) {
        var id: Int = v?.id as Int;

        when (id) {
            R.id.main_btn__submit -> save()
        }
    }

    private fun save() {
        var brandType: String = brandSpinner?.selectedItem.toString();
        var bulbType: String = bulbSpinner?.selectedItem.toString();

        if (bulbDetailEt?.text?.isEmpty()!!) {
            showAlert("Please enter bulb details")
            return
        }

        if (bulbTotalEt?.text?.isEmpty()!!) {
            showAlert("Please enter total amount")
            return
        }

        if (bulbPriceEt?.text?.isEmpty()!!) {
            showAlert("Please enter price amount")
            return
        }

        var name: String = bulbType + " " + bulbDetailEt?.editableText.toString() + " " + brandType

        var totalCount: Int = Integer.parseInt(bulbTotalEt?.text.toString())
        var priceValue: Float = bulbPriceEt?.text.toString().toFloat()

        var product: Product = Product(brandType, bulbType, name, totalCount, priceValue)

        var key: String? = mDatabase?.child("products")?.push()?.key
        product.id = key

        mDatabase?.child("products")?.child(key)?.setValue(product)

        bulbDetailEt?.setText("")
        bulbTotalEt?.setText("")
        bulbPriceEt?.setText("")

        showAlert("Product added successfully")
    }

    fun showAlert(message: String) {
        Snackbar.make(rootView!!, message, Snackbar.LENGTH_SHORT)
                .show();
    }
}
