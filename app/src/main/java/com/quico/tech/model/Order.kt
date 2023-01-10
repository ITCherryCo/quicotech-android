package com.quico.tech.model

import android.content.res.Resources
import com.quico.tech.R
import com.quico.tech.data.Constant

data class OrderResponse(
    val id: Any,
    val jsonrpc: String,
    val result: List<Order>,
    val error: String?
)

data class Order(val id: Int)


// to create order pass product id and qty
data class OrderBodyParameters(
    val params: OrderParams
)

data class OrderParams(
    val address_id: Int,
    val order_lines: List<ProductParams>
)

enum class OrderStatus(val orderStatus: String) {
    DELIVERED(Constant.DELIVERED),
    TRACK_ORDER(Constant.TRACK_ORDER),
    CANCELED(Constant.CANCELED);

    companion object {

        fun getOrderStatusColor(resource: Resources, orderStatus: String): Int {
            return when (orderStatus) {
                DELIVERED.orderStatus -> R.color.green
                TRACK_ORDER.orderStatus -> R.color.color_primary_purple
                CANCELED.orderStatus -> R.color.red_dark
                else -> R.color.color_primary_purple
            }
        }
    }
}




