package com.quico.tech.model

data class HomeDataResponse(
    val id: Any,
    val jsonrpc: String,
    val result: HomeData?,
    val error: String?
    //val error: ErrorData?
)

data class HomeData(
    val categories: List<Category>?,
    val vip_products: List<Product>?,
    val brands: List<Brand>?,
   /* val bundles: List<Product>?,
    val challenges: List<Product>?,*/
    val hot_deals: List<Product>?,
    val best_selling: List<Product>?,
    val offers: List<Product>?,
)