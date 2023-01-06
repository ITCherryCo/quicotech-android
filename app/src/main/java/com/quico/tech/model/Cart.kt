package com.quico.tech.model


class ProductBodyParameters(
    val params: ProductParams
)

data class ProductParams(
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