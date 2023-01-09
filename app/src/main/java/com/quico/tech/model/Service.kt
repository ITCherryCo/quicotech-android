package com.quico.tech.model

data class ServiceResponse(
    val id: Any,
    val jsonrpc: String,
    val result: List<Service>?,
    val error: String?
    //val error: ErrorData?
)

data class Service(
    val description: String,
    val id: Int,
    val name: String
)