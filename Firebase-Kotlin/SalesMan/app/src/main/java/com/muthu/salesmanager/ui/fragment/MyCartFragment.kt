package com.muthu.salesmanager.ui.fragment

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.muthu.salesmanager.R
import kotlinx.android.synthetic.main.fragment_my_cart.*
import kotlinx.android.synthetic.main.layout_address_detail.*
import kotlinx.android.synthetic.main.layout_payment.*
import android.widget.*
import com.muthu.salesmanager.util.ext.alert
import java.text.SimpleDateFormat
import java.util.*
import android.support.v4.app.ShareCompat.IntentBuilder
import com.google.android.gms.location.places.ui.PlacePicker
import android.widget.Toast
import com.google.android.gms.location.places.Place
import android.content.Intent
import android.graphics.Rect
import com.muthu.salesmanager.R.id.place
import android.location.Geocoder
import com.muthu.salesmanager.model.*
import java.io.IOException
import kotlin.collections.ArrayList
import android.graphics.pdf.PdfDocument.PageInfo
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import com.muthu.salesmanager.util.PermissionUtil
import java.io.File
import java.io.FileOutputStream


class MyCartFragment : Fragment(), AdapterView.OnItemSelectedListener {


    interface OnMyCartActionListener {
        fun onPaymentSuccess()
    }

    var products: ArrayList<Product> = arrayListOf()

    private var mDatabase: DatabaseReference? = null

    private var mAuth: FirebaseAuth? = null

    private var listItemsView: LinearLayout? = null

    private var totalPrice: TextView? = null

    private var step: Int = 0

    private var totalItemCount: Int = 0

    var mListener: OnMyCartActionListener? = null;

    var cities: ArrayList<City> = ArrayList();
    var areas: ArrayList<Area> = ArrayList();
    var shops: ArrayList<Shop> = ArrayList();

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnMyCartActionListener) {
            mListener = context
        }

        if (arguments != null) {
            var productList: ArrayList<Product> = arguments!!["products"] as ArrayList<Product>

            products.addAll(productList)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view: View = inflater.inflate(R.layout.fragment_my_cart, container, false)

        mAuth = FirebaseAuth.getInstance()

        mDatabase = FirebaseDatabase.getInstance().reference

        listItemsView = view.findViewById(R.id.product_list)

        totalPrice = view.findViewById(R.id.total_price)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        pay.setOnClickListener {

            if (step == 1) {
                if (shopName.editableText.isEmpty()) {
                    showAlert("Enter shop name")
                    return@setOnClickListener
                }
                if (mobileNumber.editableText.isEmpty()) {
                    showAlert("Enter mobile number")
                    return@setOnClickListener
                }
                if (address.editableText.isEmpty()) {
                    showAlert("Enter address")
                    return@setOnClickListener
                }
            } else if (step == 2) {
                doPayment()
                return@setOnClickListener
            } else if (step == 0) {
                if (products.isEmpty()) {
                    return@setOnClickListener
                }
            }

            step += 1

            updateViews()
        }

        cart.setOnClickListener {
            if (step == 1 || step == 2) {
                step = 0

                updateViews()
            }
        }

        place.setOnClickListener {
            if (step == 2) {
                step = 1

                updateViews()
            }
        }

        addCity.setOnClickListener {
            addNewCity()
        }

        addArea.setOnClickListener {

            //            val builder = PlacePicker.IntentBuilder()
//
//            startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST)

            addNewArea()
        }

        addShop.setOnClickListener {
            addNewShop()
        }

        updateViews()

        getCities()
    }

    val PLACE_PICKER_REQUEST = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(activity, data)

                addressAlertEditText?.let {
                    it.setText(place.address)
                }
            }
        }
    }

    private fun getCities() {

        val myTopPostsQuery = mDatabase?.child("cities")

        myTopPostsQuery?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cities.clear()

                var cityNames: ArrayList<String> = ArrayList()

                for (child in dataSnapshot.children) {
                    val map = child.getValue() as HashMap<String, Any>

                    var city: City = City.getMap(map)

                    city?.let {

                        cities.add(city)
                        cityNames.add(city.cityName);
                    }
                }

                activity?.let {
                    it.runOnUiThread {
                        setSpinnerAdapter(city, cityNames)
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun getAreas() {

        println("Get areas")

        if (cities.isEmpty()) {

            address.setText("")
            mobileNumber.setText("")
            shopName.setText("")
            return
        }

        val myTopPostsQuery = mDatabase?.child("cities")?.child(cities[city.selectedItemPosition].cityId)?.child("areas")

        myTopPostsQuery?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                areas.clear()

                var areaNames: ArrayList<String> = ArrayList()

                for (child in dataSnapshot.children) {
                    val map = child.getValue() as HashMap<String, Any>

                    var area: Area = Area.getMap(map)

                    area?.let {

                        areas.add(area)
                        areaNames.add(area.areaName);
                    }
                }

                activity?.let {
                    it.runOnUiThread {
                        setSpinnerAdapter(area, areaNames)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun getShops() {

        println("getShops")

        if (cities.isEmpty() || areas.isEmpty()) {
            address.setText("")
            mobileNumber.setText("")
            shopName.setText("")
            return
        }

        val myTopPostsQuery = mDatabase?.child("cities")?.child(cities[city.selectedItemPosition].cityId)?.child("areas")?.child(areas[area.selectedItemPosition].areaId)?.child("shops")

        myTopPostsQuery?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                shops.clear()

                var shopNames: ArrayList<String> = ArrayList()

                for (child in dataSnapshot.children) {
                    val map = child.getValue() as HashMap<String, Any>

                    var shop: Shop = Shop.getMap(map)

                    shop?.let {

                        shops.add(shop)
                        shopNames.add(shop.shopName);
                    }
                }

                activity?.let {
                    it.runOnUiThread {

                        if (shops.isEmpty()) {
                            address.setText("")
                            mobileNumber.setText("")
                            shopName.setText("")
                        }
                        setSpinnerAdapter(shop, shopNames)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.city -> {
                getAreas()
            }
            R.id.area -> {
                getShops()
            }
            R.id.shop -> {
                var shopItem = shops[shop.selectedItemPosition]

                shopItem?.let {
                    address.setText(shopItem.address)
                    mobileNumber.setText(shopItem.mobileNumber)
                    shopName.setText(shopItem.shopName)
                }
            }
        }
    }

    private fun setSpinnerAdapter(spinner: Spinner?, items: ArrayList<String>) {
        spinner?.let {
            it.adapter = ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, items)

            it.onItemSelectedListener = this
        }
    }

    private fun addNewCity() {
        activity?.alert {
            var view: View = customView(R.layout.layout_add_new_city)

            var saveBtn: Button = view.findViewById(R.id.inventory__add_new_category_btn__save)
            var categoryName: EditText = view.findViewById(R.id.inventory__add_new_category_et__category)

            saveBtn.setOnClickListener {

                if (categoryName.editableText.isEmpty()) {
                    return@setOnClickListener
                }

                val value: String = categoryName.editableText.toString()

                for (city in cities) {
                    if (value.equals(city.cityName, true)) {
                        break
                        return@setOnClickListener
                    }
                }


                val key = mDatabase?.child("cities")?.push()?.key

                var city: City = City(value, key!!)

                mDatabase?.child("cities")?.child(key)?.setValue(city)

                dismiss()
            }

            show()
        }
    }

    private fun addNewArea() {

        if (cities.isEmpty()) {
            showAlert("Please add or select city")

            return
        }

        activity?.alert {
            var view: View = customView(R.layout.layout_add_new_area)

            var saveBtn: Button = view.findViewById(R.id.inventory__add_new_category_btn__save)
            var categoryName: EditText = view.findViewById(R.id.inventory__add_new_category_et__category)

            saveBtn.setOnClickListener {

                if (categoryName.editableText.isEmpty()) {
                    return@setOnClickListener
                }

                val value: String = categoryName.editableText.toString()

                for (area in areas) {
                    if (value.equals(area.areaName, true)) {
                        break
                        return@setOnClickListener
                    }
                }


                val key = mDatabase?.child("cities")?.child(cities[city.selectedItemPosition].cityId)?.child("areas")?.push()?.key

                var area: Area = Area(value, key!!)

                mDatabase?.child("cities")?.child(cities[city.selectedItemPosition].cityId)?.child("areas")?.child(key)?.setValue(area)

                dismiss()
            }

            show()
        }
    }

    var addressAlertEditText: EditText? = null;

    private fun addNewShop() {
        activity?.alert {
            var view: View = customView(R.layout.layout_add_new_shop)

            var saveBtn: Button = view.findViewById(R.id.inventory__add_new_category_btn__save)
            var addAddress: ImageButton = view.findViewById(R.id.addAddress)

            var shopName: EditText = view.findViewById(R.id.shopName)
            var mobileNumber: EditText = view.findViewById(R.id.mobileNumber)
            addressAlertEditText = view.findViewById(R.id.address)

            addAddress.setOnClickListener {
                val builder = PlacePicker.IntentBuilder()

                startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST)
            }

            saveBtn.setOnClickListener {

                if (shopName.editableText.isEmpty()) {
                    showAlert("Enter shop name")
                    return@setOnClickListener
                }

                if (mobileNumber.editableText.isEmpty()) {
                    showAlert("Enter mobile number")
                    return@setOnClickListener
                }

                val key = mDatabase?.child("shops")?.push()?.key

                var shop: Shop = Shop(key!!, shopName.editableText.toString(), mobileNumber.editableText.toString(), addressAlertEditText?.editableText.toString())

                mDatabase?.child("shops")?.child(key)?.setValue(shop)

                mDatabase?.child("cities")?.child(cities[city.selectedItemPosition].cityId)?.child("areas")?.child(areas[area.selectedItemPosition].areaId)?.child("shops")?.child(key)?.setValue(shop)

                dismiss()
            }

            show()
        }
    }

    private fun doPayment() {
//        showProgressDialog()

        val key = mDatabase?.child("orders")?.push()?.key

        var usetId: String = mAuth?.currentUser?.uid!!

        var order: Order = Order()

        order.shopName = shopName.editableText.toString()
        order.products.addAll(products)
        order.address = address.editableText.toString()
//        order.pincode = address.editableText.toString()
        order.phone = mobileNumber.editableText.toString()
        order.totalAmount = totalPrice?.text.toString()
        order.totalProduct = totalItemCount
        order.userId = usetId
        order.userName = mAuth?.currentUser?.displayName
        val longDateFormat = SimpleDateFormat("M/d/yy h:mm a").format(Date())
        order.dateTime = longDateFormat


        mDatabase?.child("orders")?.child(key)?.setValue(order)
        mDatabase?.child("user-orders")?.child(usetId)?.child(key)?.setValue(order)

        for (product in products) {
            val globalPostRef = mDatabase?.child("products")?.child(product.id)

            globalPostRef?.runTransaction(object : Transaction.Handler {
                override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapShot: DataSnapshot?) {

                }

                override fun doTransaction(mutableData: MutableData?): Transaction.Result {

                    val p = mutableData?.getValue<Product>(Product::class.java)
                            ?: return Transaction.success(mutableData)

                    p?.let {
                        var countValue = product.stars.get(usetId)

                        countValue?.let { value ->

                            if (value > 0) {
                                var newCount = product.availableCount - value

                                if (newCount <= 0) {
                                    p.availableCount = 0;
                                    p.isAvailable = false
                                } else {
                                    p.availableCount = newCount;
                                }
                            }
                        }

                        if (it.stars.containsKey(usetId)) {
                            it.stars.remove(usetId)
                        }
                    }

                    mutableData.value = p

                    return Transaction.success(mutableData)
                }

            })
        }

        if (PermissionUtil.hasExternalStoragePermission(activity)) {
            printPayment()
        } else {
            mListener?.let {
                it.onPaymentSuccess()
            }
        }

//        hideProgressDialog()
    }

    private fun printPayment() {
        val document = PdfDocument()

        // repaint the user's text into the page
        val content = printView

        // crate a page description
        val pageNumber = 1
        val pageInfo = PageInfo.Builder(content.getWidth(),
                content.getHeight() - 20, pageNumber).create()

        // create a new page from the PageInfo
        val page = document.startPage(pageInfo)

        content.draw(page.canvas)

//        val bounds = Rect()
//        content.getDrawingRect(bounds)
//
//        val bounds1 = Rect()
//        content.getHitRect(bounds1)
//
//        println("bounds right" + bounds.right + " bottom " + bounds.bottom + " left " + bounds.left)
//        println("getHitRect right" + bounds1.right + " bottom " + bounds1.bottom + " left " + bounds1.left)

        // do final processing of the page
        document.finishPage(page)

        val sdf = SimpleDateFormat("ddMMyyyyhhmmss")

        val pdfName = ("pdfdemo"
                + sdf.format(Calendar.getInstance().time) + ".pdf")

        val outputFile = File(Environment.getExternalStorageDirectory().getPath(), pdfName)

        try {
            outputFile.createNewFile()
            val out = FileOutputStream(outputFile)
            document.writeTo(out)
            document.close()
            out.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        mListener?.let {
            it.onPaymentSuccess()
        }

        var target = Intent(Intent.ACTION_VIEW)

        var apkURI: Uri = FileProvider.getUriForFile(
                activity!!,
                context?.getApplicationContext()?.getPackageName() + ".provider", outputFile);
        target.setDataAndType(apkURI, "application/pdf");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//        target.setDataAndType(Uri.fromFile(outputFile), "application/pdf");
//        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        var intent = Intent.createChooser(target, "Open File");

        startActivity(intent)
    }

    var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(activity)
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    private fun updateItems() {
        step = 0;

        listItemsView?.removeAllViews()

        var usetId: String = mAuth?.currentUser?.uid!!

        // TODO Need to change this
        val inflater = LayoutInflater.from(activity!!)

        var priceTotalValue: Float = 0.0f

        for (product in products) {
            var itemView: View = inflater.inflate(R.layout.layout_holder_my_cart_list, null)

            var titleView: TextView = itemView.findViewById(R.id.product_title)
            var count: TextView = itemView.findViewById(R.id.count)
            var total: TextView = itemView.findViewById(R.id.total)

            titleView.setText(product.productName)

            count.setText("0")

            product.let {
                var countValue = product.stars.get(usetId)

                countValue?.let { value ->

                    count.setText("" + value + " x Rs." + product.price)


                    var productTotalPrice = value * product.price

                    total.setText("Rs." + productTotalPrice)

                    priceTotalValue = priceTotalValue + productTotalPrice
                }
            }

            listItemsView?.addView(itemView)
        }

        totalPrice?.setText("Rs." + priceTotalValue)

        addressLayout.visibility = View.GONE
        listItemsView?.visibility = View.VISIBLE
        paymentLayout.visibility = View.GONE
        total_layout.visibility = View.VISIBLE

    }

    fun updateAddress() {
        step = 1

        addressLayout.visibility = View.VISIBLE
        listItemsView?.visibility = View.GONE
        paymentLayout.visibility = View.GONE
        total_layout.visibility = View.VISIBLE
    }

    fun updatePayment() {
        step = 2

        payment_product_list?.removeAllViews()

        var usetId: String = mAuth?.currentUser?.uid!!

        val inflater = LayoutInflater.from(activity)

        var priceTotalValue: Float = 0.0f

        totalItemCount = 0;

        for (product in products) {
            var itemView: View = inflater.inflate(R.layout.layout_holder_my_cart_list, null)

            var titleView: TextView = itemView.findViewById(R.id.product_title)
            var count: TextView = itemView.findViewById(R.id.count)
            var total: TextView = itemView.findViewById(R.id.total)

            titleView.setText(product.productName)

            count.setText("0")

            product.let {
                var countValue = product.stars.get(usetId)

                countValue?.let { value ->

                    totalItemCount += value

                    count.setText("" + value + " x Rs." + product.price)

                    var productTotalPrice = value * product.price

                    total.setText("Rs." + productTotalPrice)

                    priceTotalValue = priceTotalValue + productTotalPrice
                }
            }

            payment_product_list?.addView(itemView)
        }

        paymentDetailAddress.setText(shopName.editableText.toString() + " \n " +
                address.editableText.toString() + " \n " +
                mobileNumber.editableText.toString() + " \n "
        )

        total_price_final.setText("Rs." + priceTotalValue)

        addressLayout.visibility = View.GONE
        listItemsView?.visibility = View.GONE
        paymentLayout.visibility = View.VISIBLE
        total_layout.visibility = View.GONE
    }

    private fun updateViews() {
        when (step) {
            0 -> {
                cart.setImageResource(R.drawable.ic_shopping_cart_color)
                place.setImageResource(R.drawable.ic_place)
                payment.setImageResource(R.drawable.ic_credit_card)

                pay.setText("Next - Shipping")

                updateItems()
            }
            1 -> {
                cart.setImageResource(R.drawable.ic_shopping_cart_color)
                place.setImageResource(R.drawable.ic_place_color)
                payment.setImageResource(R.drawable.ic_credit_card)

                pay.setText("Next - Payment")

                updateAddress()
            }
            2 -> {

                cart.setImageResource(R.drawable.ic_shopping_cart_color)
                place.setImageResource(R.drawable.ic_place_color)
                payment.setImageResource(R.drawable.ic_credit_card_color)

                pay.setText("Confirm")

                updatePayment()
            }
        }
    }

    fun showAlert(message: String) {
        Snackbar.make(rootView!!, message, Snackbar.LENGTH_SHORT)
                .show();
    }
}
