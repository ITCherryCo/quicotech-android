package com.quico.tech.model

data class AddressResponse(
    val addresses: List<Address>,
    val result: String
)

data class Address( val id:Int) {
}