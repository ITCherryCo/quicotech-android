package com.quico.tech.model

data class ErrorData(
    val arguments: List<String>,
    val context: Any,
    val debug: String,
    val message: String,
    val name: String
)

