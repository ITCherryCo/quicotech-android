package com.quico.tech.model


class Product(
    val category: String?,
    val description: String?,
    val id: Int,
    val images: ArrayList<String>?,
    val image: String?,
    val is_on_sale: Boolean,
    val is_vip: Boolean,
    val is_vip_charge_product: Boolean,
    val name: String,
    val new_price: Double,
    val regular_price: Double,
    val subtotal: Double,
    val quantity: Int?,
    val quantity_available: Int?,
    val in_stock: Boolean?,
    val specifications: ArrayList<Specifications>?
) {

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
        false,
        name,
        new_price,
        0.0,
        0.0,
        1,
        1,
        true,
        null
    )

    constructor(
        id:Int,
        name: String,
        quantity: Int,
        subtotal: Double
    ) : this(
        "",
        "",
        id,
        null,
        "",
        false,
        false,
        false,
        name,
        0.0,
        0.0,
        subtotal,
        quantity,
        1,
        true,
        null
    )
}

data class ProductResponse(
    val id: Any,
    val jsonrpc: String,
    val result: Product,
    val error: String?
    //val error: ErrorData
)

data class SearchBodyParameters(
    val params: SearchParams
)

data class SearchParams(
    val page: Int,
    val name: String
) {
    constructor(name: String) : this(
        0,
        name
    )
}

data class ProductsResponse(
    val id: Any,
    val jsonrpc: String,
    val result: ArrayList<Product>,
    val error: String?
    //val error: ErrorData?
)

data class SearchResponse(
    val id: Any,
    val jsonrpc: String,
    val result: SearchResult,
    val error: String?
    //val error: ErrorData?
)

data class SearchResult(
    val products: ArrayList<Product>,
    val pagination: Pagination?,
)

data class Pagination(
    val page: Int,
    val size: Int,
    val total_pages: Int
)

data class ProductDetails(
    var id: Int, var name: String, var value: String
) {

}