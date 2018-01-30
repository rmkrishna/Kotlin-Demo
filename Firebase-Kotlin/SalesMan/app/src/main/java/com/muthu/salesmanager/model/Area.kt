package com.muthu.salesmanager.model

import java.util.HashMap

/**
 * Created by muthu on 27/1/18.
 */
class Area(val areaName: String, val areaId: String = "") {


    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["areaName"] = areaName
        result["areaId"] = areaId
        return result
    }

    companion object {
        fun getMap(map: Map<String, Any>): Area = Area(map.get("areaName") as String, map.get("areaId") as String)
    }

}