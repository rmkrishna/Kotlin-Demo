package com.muthu.salesmanager.model

import com.google.firebase.database.Exclude
import java.util.HashMap

/**
 * Created by muthu on 27/1/18.
 */
class Category(val categoryName: String, val categoryId: String = "") {


    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["categoryName"] = categoryName
        result["categoryId"] = categoryId
        return result
    }

    companion object {
        fun getMap(map: Map<String, Any>): Category = Category(map.get("categoryName") as String, map.get("categoryId") as String)
    }

}