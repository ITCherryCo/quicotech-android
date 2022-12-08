package com.quico.tech.model

import android.content.res.Resources
import com.quico.tech.R
import com.quico.tech.data.Constant

data class OrderResponse(
    val orders: List<Order>,
    val result: String
)

data class Order(val id: Int)

enum class OrderStatus(val orderStatus: String) {
    DELIVERED(Constant.DELIVERED),
    TRACK_ORDER(Constant.TRACK_ORDER),
    CANCELED(Constant.CANCELED);

    companion object {

        fun getOrderStatusColor(resource: Resources, orderStatus: String): Int {
            return when (orderStatus) {
                DELIVERED.orderStatus -> R.color.green
                TRACK_ORDER.orderStatus -> R.color.purple_quico
                CANCELED.orderStatus -> R.color.red_dark
                else -> R.color.purple_quico
            }
        }
    }
}




