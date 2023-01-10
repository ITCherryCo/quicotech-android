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

data class ServiceOrder(
    val audio: String,
    val audio_filename: String,
    val delivery_type: String,
    val description: String,
    val image1: String,
    val image2: String,
    val image3: String,
    val image4: String,
    val service_id: Int,
    val service_type_id: Int
)