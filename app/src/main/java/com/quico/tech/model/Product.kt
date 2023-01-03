package com.quico.tech.model

import android.graphics.drawable.Drawable


class Product(var name: String, var image: Drawable, var price: Float) {

}

data class ProductResponse(
    val id: Any,
    val jsonrpc: String,
    val result: Product,
    val error: ErrorData?
)

data class ProductDetails(var id:Int,var name: String, var value: String) {

}