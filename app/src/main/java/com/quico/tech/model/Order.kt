package com.quico.tech.model

import android.content.res.Resources
import com.kofigyan.stateprogressbar.StateProgressBar
import com.quico.tech.R
import com.quico.tech.data.Constant

data class OrderResponse(
    val id: Any,
    val jsonrpc: String,
    val result: ArrayList<Order>,
    val error: String?
)

data class Order(val id: Int, val order_nb:String,val service_nb:String? ,val status:String, val total_price:Double?)

// to create order pass product id and qty
data class OrderBodyParameters(
    val params: OrderParams
)

data class OrderParams(
    val address_id: Int,
    val order_lines: List<ProductParams>
)


data class DeliveryOrderResponse(
    val id: Any,
    val jsonrpc: String,
    val result: DeliveryOrder,
    val error: String?
)

data class DeliveryOrder(
    val VAT_amount: Double,
    val VAT_percentage: String,
    val id: Int,
    val order_lines: List<Product>,
    //    val order_lines: List<OrderLine>,
    val order_nb: String,
    val payment_method: String,
    val status: String,
    val total_price: Double,
    val total_without_vat: Double,
    val address: Address
)

/*data class OrderLine(
    val product: String,
    val quantity: Int,
    val subtotal: Double
)*/
enum class OrderStatus(val orderStatus: String) {
    ORDER_RECEIVED(Constant.ORDER_RECEIVED),
    PACKAGING(Constant.PACKAGING),
    ON_THE_WAY(Constant.ON_THE_WAY),
    DELIVERED(Constant.DELIVERED),
   // TRACK_ORDER(Constant.TRACK_ORDER),
    CANCELED(Constant.CANCELED);

    companion object {

        fun getOrderState(orderStatus: String): StateProgressBar.StateNumber {
            return when (orderStatus.lowercase()) {
                ORDER_RECEIVED.orderStatus.lowercase() ->StateProgressBar.StateNumber.ONE
                PACKAGING.orderStatus.lowercase() -> StateProgressBar.StateNumber.TWO
                ON_THE_WAY.orderStatus.lowercase() -> StateProgressBar.StateNumber.THREE
                DELIVERED.orderStatus.lowercase() ->StateProgressBar.StateNumber.FOUR
                CANCELED.orderStatus.lowercase() ->StateProgressBar.StateNumber.FIVE
                else ->StateProgressBar.StateNumber.ONE
            }
        }

        fun getOrderStatus(resource: Resources, orderStatus: String): String {
            return when (orderStatus.lowercase()) {
                ORDER_RECEIVED.orderStatus.lowercase() ->resource.getString(R.string.order_received)
                PACKAGING.orderStatus.lowercase() -> resource.getString(R.string.packaging)
                ON_THE_WAY.orderStatus.lowercase() -> resource.getString(R.string.on_the_way)
                DELIVERED.orderStatus.lowercase() -> resource.getString(R.string.delivered)
                CANCELED.orderStatus.lowercase() -> resource.getString(R.string.canceled)
                else -> resource.getString(R.string.order_received)
            }
        }

        fun getOrderStatusColor(resource: Resources, orderStatus: String): Int {
            return when (orderStatus.lowercase()) {
                ORDER_RECEIVED.orderStatus.lowercase() -> R.color.orange
                PACKAGING.orderStatus.lowercase() -> R.color.color_primary_purple
                ON_THE_WAY.orderStatus.lowercase() -> R.color.normal_orange
                DELIVERED.orderStatus.lowercase() -> R.color.green
                //TRACK_ORDER.orderStatus -> R.color.color_primary_purple
                CANCELED.orderStatus.lowercase() -> R.color.red_dark
                else -> R.color.orange
            }
        }
    }
}




