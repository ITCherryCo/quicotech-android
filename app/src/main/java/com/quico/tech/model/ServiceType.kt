package com.quico.tech.model

data class ServiceTypeResponse(
    val id: Any,
    val jsonrpc: String,
    val result: List<ServiceType>?,
    val error: String?
    //val error: ErrorData?
)

data class ServiceType(
    val have_sub_service: Boolean,
    val id: Int,
    val image: String,
    val type: String
)