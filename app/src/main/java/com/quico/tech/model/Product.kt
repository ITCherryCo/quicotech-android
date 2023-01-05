package com.quico.tech.model


class Product(val category: String,
              val description: String,
              val id: Int,
              val images: ArrayList<String>?,
              val image: String?,
              val is_on_sale: Boolean,
              val is_vip: Boolean,
              val name: String,
              val new_price: Double,
              val regular_price: Double,
              val specifications: ArrayList<Specifications>?) {

    constructor(
        name: String,
        new_price: Double
    ) : this(
        "",
        "",
        0,
        null,
        "",
        false,
        false,
        name,
        new_price,
        0.0,
        null
    )
 /*   constructor(
        name: String,
    ) : this(
        "",
        "",
        0,
        null,
        null,
        false,
        false,
        name,
        0.0,
        0.0,
        null
    )*/
}

data class ProductResponse(
    val id: Any,
    val jsonrpc: String,
    val result: Product,
    val error: ErrorData?
)

data class SearchBodyParameters(
    val params: NameParams
)
data class NameParams(
    val name: String
)
data class SearchResponse(
    val id: Any,
    val jsonrpc: String,
    val result: ArrayList<Product>,
    val error: ErrorData?
)


data class ProductDetails(
    var id:Int,var name: String, var value: String) {

}