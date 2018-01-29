package com.muthu.salesmanager.model

import java.io.Serializable
import java.util.HashMap

/**
 * Created by muthu on 27/1/18.
 */
class Product() : Serializable {

    var id: String? = null
    var brandType: String? = null
    var productType: String? = null
    var productName: String? = null
    var totalCount: Int = 0
    var price: Float = 0.0f
    var availableCount: Int = 0
    var isAvailable: Boolean = false;
    var stars: MutableMap<String, Int> = HashMap()

    constructor(brand: String?, productType: String, productName: String,
                totalCount: Int, price: Float, availableCount: Int = totalCount, isAvailable: Boolean = true) : this() {
        this.brandType = brand
        this.productType = productType
        this.productName = productName
        this.totalCount = totalCount
        this.availableCount = availableCount
        this.isAvailable = isAvailable
        this.price = price
    }
}