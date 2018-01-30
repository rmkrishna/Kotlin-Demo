package com.muthu.salesmanager.model

import java.util.HashMap

/**
 * Created by muthu on 27/1/18.
 */
class City(val cityName: String, val cityId: String = "") {


    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["cityName"] = cityName
        result["cityId"] = cityId
        return result
    }

    companion object {
        fun getMap(map: Map<String, Any>): City = City(map.get("cityName") as String, map.get("cityId") as String)
    }

}