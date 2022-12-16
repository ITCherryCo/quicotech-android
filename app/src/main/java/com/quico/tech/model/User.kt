package com.quico.tech.model

data class RegisterBodyParameters(
    val params: RegisterParams
)

data class RegisterParams(
    val login: String,
    val mobile: String?,
    val name: String?,
    val password: String
) {
    constructor(
        login: String,
        password: String
    ) : this(
        login,
        null,
        null,
        password
    )
}

data class UserResponse(
    val code: Int,
    val data: ArrayList<Data>?,
    val success: Boolean,
    val status: String?
)

data class Result(
    val status: String?,
    val session_id: String?
){
    constructor(
        session_id: String?,
    ) : this(
        null,
        session_id
    )
}


data class Data(
    val city: String,
    val email: String,
    val mobile: String,
    val name: String,
    val partner_id: Int,
    val street: String,
    val street2: String,
    val user_id: Int,
    val zip: String
)