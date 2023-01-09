package com.quico.tech.model

import android.graphics.drawable.Drawable
import com.quico.tech.R

/*
data class Brand(var id:Int, var name: String?, var image: Int?) {
}*/


data class BrandResponse(
    val id: Any,
    val jsonrpc: String,
    val result: List<Brand>?,
    val error: String?
    //val error: ErrorData?
)

data class Brand(
    val id: Int,
    val name: String,
    val image: String,
)