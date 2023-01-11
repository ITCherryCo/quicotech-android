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

data class Order(val id: Int, val order_nb:String, val status:String, val total_price:Double?)


// to create order pass product id and qty
data class OrderBodyParameters(
    val params: OrderParams
)

data class OrderParams(
    val address_id: Int,
    val order_lines: List<ProductParams>
)

enum class OrderStatus(val orderStatus: String) {
    ORDER_RECEIVED(Constant.ORDER_RECEIVED),
    PACKAGING(Constant.PACKAGING),
    ON_THE_WAY(Constant.ON_THE_WAY),
    DELIVERED(Constant.DELIVERED),
   // TRACK_ORDER(Constant.TRACK_ORDER),
    CANCELED(Constant.CANCELED);

    companion object {

        fun getOrderStatus(resource: Resources, orderStatus: String): String {
            return when (orderStatus) {
                ORDER_RECEIVED.orderStatus ->resource.getString(R.string.order_received)
                PACKAGING.orderStatus -> resource.getString(R.string.packaging)
                ON_THE_WAY.orderStatus -> resource.getString(R.string.on_the_way)
                DELIVERED.orderStatus -> resource.getString(R.string.delivered)
                CANCELED.orderStatus -> resource.getString(R.string.canceled)
                else -> resource.getString(R.string.order_received)
            }
        }

        fun getOrderStatusColor(resource: Resources, orderStatus: String): Int {
            return when (orderStatus) {
                ORDER_RECEIVED.orderStatus -> R.color.orange
                PACKAGING.orderStatus -> R.color.color_primary_purple
                ON_THE_WAY.orderStatus -> R.color.normal_orange
                DELIVERED.orderStatus -> R.color.green
                //TRACK_ORDER.orderStatus -> R.color.color_primary_purple
                CANCELED.orderStatus -> R.color.red_dark
                else -> R.color.orange
            }
        }
    }
}




