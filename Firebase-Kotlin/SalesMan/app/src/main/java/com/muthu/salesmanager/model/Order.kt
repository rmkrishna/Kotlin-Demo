package com.muthu.salesmanager.model

/**
 * Created by muthu on 27/1/18.
 */
class Order() {
    var userName:String? = null
    var userId:String? = null
    var dateTime:String? = null
    var shopName: String? = null
    var totalAmount: String? = null
    var totalProduct: Int = 0
    var address: String? = null
    var pincode: String? = null
    var phone: String? = null
    var products: ArrayList<Product> = arrayListOf()
}