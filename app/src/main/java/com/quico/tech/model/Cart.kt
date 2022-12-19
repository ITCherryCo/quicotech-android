package com.quico.tech.model

data class CartResponse(
    val items: List<Item>,
    val result: String,
    val message: String
)

class Cart {
}