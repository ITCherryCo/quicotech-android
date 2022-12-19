package com.quico.tech.model

data class ServiceResponse(
    val services: List<Service>,
    val result: String
)
data class Service(val id:Int) {
}