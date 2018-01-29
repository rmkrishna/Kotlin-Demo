package com.muthu.salesmanager.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Category
import com.muthu.salesmanager.util.ext.alert


class InventoryManagementActivity : AppCompatActivity() {

    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inventory_management)

        mDatabase = FirebaseDatabase.getInstance().reference

        setBtnClickListener(R.id.inventory_btn__add_new_category, ::addNewCategory)
        setBtnClickListener(R.id.inventory_btn__add_new_sub_category, ::addNewSubCategory)
        setBtnClickListener(R.id.inventory_btn__add_new_product, ::addNewProduct)
        setBtnClickListener(R.id.inventory_btn__edit_product, ::editProduct)
        setBtnClickListener(R.id.inventory_btn__view_order, ::viewOrder)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            finish()
        }
        return true
    }

    fun setBtnClickListener(id: Int, action: () -> Unit) {
        findViewById<Button>(id).setOnClickListener {
            action()
        }
    }

    private fun addNewCategory() {
        alert {
            var view: View = customView(R.layout.layout_add_new_category)

            var saveBtn: Button = view.findViewById(R.id.inventory__add_new_category_btn__save)
            var categoryName: EditText = view.findViewById(R.id.inventory__add_new_category_et__category)

            saveBtn.setOnClickListener {

                if (categoryName.editableText.isEmpty()) {
                    return@setOnClickListener
                }

                val value: String = categoryName.editableText.toString()


                val key = mDatabase?.child("categories")?.push()?.key

                var category: Category = Category(value, key!!)

                mDatabase?.child("categories")?.child(key)?.setValue(category)

                dismiss()
            }

            show()
        }
    }

    private fun addNewSubCategory() {

        alert {
            var view: View = customView(R.layout.layout_add_new_sub_category)

            var saveBtn: Button = view.findViewById(R.id.inventory__add_new_category_btn__save)
            var categoryName: EditText = view.findViewById(R.id.inventory__add_new_category_et__category)

            saveBtn.setOnClickListener {

                if (categoryName.editableText.isEmpty()) {
                    return@setOnClickListener
                }

                val value: String = categoryName.editableText.toString()


                val key = mDatabase?.child("sub_categories")?.push()?.key

                var category: Category = Category(value, key!!)

                mDatabase?.child("sub_categories")?.child(key)?.setValue(category)

                dismiss()
            }

            show()
        }
    }

    private fun addNewProduct() {
        startActivity(Intent(this, AddProductActivity::class.java))
    }

    private fun editProduct() {

    }

    private fun viewOrder() {
        startActivity(Intent(this, OrderListActivity::class.java))
    }
}
