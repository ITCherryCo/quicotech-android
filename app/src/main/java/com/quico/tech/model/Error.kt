package com.quico.tech.model

data class Error(
    val code: Int,
    val `data`: ErrorData,
    val message: String
)