package com.quico.tech.model

data class ItemResponse(
    val items: List<Item>,
    val result: String
)

data class Item (val id:Int, val name:String){
}