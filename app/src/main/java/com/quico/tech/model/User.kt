package com.quico.tech.model

data class RegisterBodyParameters(
    val params: RegisterParams
)

data class RegisterParams(
    val login: String?,
    val mobile: String?,
    val name: String?,
    val password: String?
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

    constructor(
        mobile: String,
    ) : this(
        null,
        mobile,
        null,
        null
    )
}

data class RegisterResponse(
    val id: Any,
    val jsonrpc: String,
    val result: Result
)


data class UserResponse(
    val code: Int,
    val data: ArrayList<Data>?,
    val success: Boolean,
    val status: String?
)

data class Result(
    val status: String?,
    val error: String?,
    val session_id: String?,
    val user_id: Int?,
    val partner_id: Int?,
){
    // this is for login successfully
    constructor(
        session_id: String?,
        user_id: Int?,
        partner_id: Int?
    ) : this(
        null,
        null,
        session_id,
        partner_id,
        user_id
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
    val zip: String
)

data class UpdateUserBodyParameters(
    val params: UpdateUserParams
)

data class UpdateUserParams(
    val name: String?,
    val dob: String?,
    val image: String?,
    val email: String?,
    val mobile: String?,
) {
    constructor(
        name: String,
        dob: String,
        image: String
    ) : this(
        name,
        dob,
        image,
        null,
        null
    )

    constructor(
        email: String
    ) : this(
        null,
        null,
        null,
        email,
        null
    )



}