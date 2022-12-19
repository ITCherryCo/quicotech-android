package com.quico.tech.model

data class StandardResponse(
    val error: Error,
    val id: Any,
    val jsonrpc: String,
    val result: Result
)

