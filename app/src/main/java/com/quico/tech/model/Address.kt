package com.quico.tech.model

data class AddressBodyParameters(
    val params: Address
)

data class AddressResponse(
    val id: Any,
    val jsonrpc: String,
    val result: List<Address>,
    val error: ErrorData?
)

data class Address(
    val city: String,
    val country: String,
    val id: Int,
    val mobile: String,
    val name: String,
    val state: String,
    val street: String,
    val street2: String,
    val zip: String
) {
    constructor(
         city: String,
         name: String,
         state: String,
         street: String,
         street2: String,
         zip: String
    ) : this(
        city,
        "",
        0,
        "",
        name,
        state,
        street,
        street2,
        zip
    )
}