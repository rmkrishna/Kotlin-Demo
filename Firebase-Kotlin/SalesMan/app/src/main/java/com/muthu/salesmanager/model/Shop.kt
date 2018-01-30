package com.muthu.salesmanager.model

import java.util.HashMap

/**
 * Created by muthu on 27/1/18.
 */
class Shop(var shopId: String, var shopName: String, var mobileNumber: String, var address: String) {


    companion object {
        fun getMap(map: Map<String, Any>): Shop = Shop(map.get("shopId") as String,
                map.get("shopName") as String,
                map.get("mobileNumber") as String,
                map.get("address") as String)
    }
}