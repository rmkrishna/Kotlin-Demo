package com.muthu.salesmanager.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.Product
import com.muthu.salesmanager.ui.fragment.MyCartFragment
import com.muthu.salesmanager.ui.fragment.OrderListFragment
import com.muthu.salesmanager.ui.fragment.ProductListFragment
import com.muthu.salesmanager.util.PermissionUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.HashMap

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ProductListFragment.OnActionListener, MyCartFragment.OnMyCartActionListener {

    override fun onPaymentSuccess() {

        runOnUiThread {
            products.clear()

            updateTopView(R.id.nav_home)
        }
    }

    private var mAuth: FirebaseAuth? = null

    var userId: String? = null;

    private var currentId: Int = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val headerLayout = nav_view.getHeaderView(0)

        var emailTv: TextView = headerLayout.findViewById(R.id.main_tv_email)
        var nameTv: TextView = headerLayout.findViewById(R.id.main_tv_name)

        var firBaseUser: FirebaseUser? = mAuth?.currentUser;

        userId = firBaseUser?.uid

        emailTv.setText(firBaseUser?.email)
        nameTv.setText(firBaseUser?.displayName)

        updateTopView(R.id.nav_home)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (currentId == R.id.nav_home) {
                super.onBackPressed()
            } else {
                updateTopView(R.id.nav_home)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        updateTopView(item.itemId)

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun updateTopView(id: Int) {
        var fragment: Fragment? = null;

        currentId = id

        when (id) {
            R.id.nav_home -> {
                fragment = ProductListFragment()
            }

            R.id.nav_my_cart -> {

                PermissionUtil.hasExternalStoragePermission(this, 100)

                fragment = MyCartFragment()

                var cacheProducts = arrayListOf<Product>()

                if (!products.isEmpty()) {
                    for ((id, product) in products) {
                        cacheProducts.add(product)
                    }
                }

                var bundle = Bundle()
                bundle.putSerializable("products", cacheProducts)

                fragment.arguments = bundle
            }

            R.id.nav_orders -> {
                fragment = OrderListFragment()
            }

            R.id.nav_admin_panel -> {
                startActivity(Intent(this, InventoryManagementActivity::class.java))
            }
        }

        fragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.main_fl__fragments, it).commit()
        }
    }

    var products: MutableMap<String, Product> = HashMap()

    override fun onAddedProduct(product: Product): Int {

        products.put(product.id!!, product)

        var count: Int = 0;

        if (!products.isEmpty()) {
            for ((id, p) in products) {

                var countValue = p.stars.get(userId)

                countValue?.let { value ->

                    if (value > 0) {
                        count = count.plus(value)
                    } else {
                        //Do Nothing
                    }
                }
            }
        }

        return count
    }

    override fun onRemovedProduct(p: Product): Int {
        products.remove(p.id)

        var count: Int = 0;

        if (!products.isEmpty()) {
            for ((id, product) in products) {

                if (product.id == p.id) {
                    continue
                }

                var countValue = product.stars.get(userId)

                countValue?.let { value ->

                    if (value > 0) {
                        count = count.plus(value)
                    } else {
                        //Do Nothing
                    }
                }
            }
        }

        return count
    }

    override fun openCart() {
        updateTopView(R.id.nav_my_cart)
    }


}
