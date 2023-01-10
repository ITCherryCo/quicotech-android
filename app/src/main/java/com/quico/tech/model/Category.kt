package com.quico.tech.model

import android.graphics.drawable.Drawable


//class Category(var name: String, var image: Drawable) {
//
//}

data class CategoryResponse(
    val id: Any,
    val jsonrpc: String,
    val result: List<Category>?,
    val error: String?
    //val error: ErrorData?
)

data class Category(
    val id: Int,
    val name: String,
    val image: String,
)