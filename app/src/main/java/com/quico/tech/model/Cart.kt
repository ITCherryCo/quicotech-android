package com.quico.tech.model

data class CartResponse(
    val id: Any,
    val jsonrpc: String,
    val error: ErrorData,
    val result: ArrayList<Product>
)

class CartBodyParameters(
    val params: CartParams
)

data class CartParams(
    val is_vip_price: Boolean?,
    val product_id: Int,
    val quantity: Int?
) {
    constructor(
         product_id: Int,
    ) : this(
        false,
        product_id,
        null,
    )

    constructor(
        product_id: Int,
        quantity: Int,
    ) : this(
        false,
        product_id,
        quantity,
    )
}