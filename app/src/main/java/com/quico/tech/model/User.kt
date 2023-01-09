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
        login: String,
        mobile: String,
        name: String
    ) : this(
        login,
        mobile,
        name,
        null
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
    val result: Result,
    val error: String
   // val error: ErrorData?
)

data class SessionResponse(
    val result: String,
)

data class Result(
    val status: String?,
    val error: String?
) {
}

data class UserResponse(
    val id: Any,
    val jsonrpc: String,
    val result: User
)

data class User(
    val session_id: String?,
    val is_vip: Boolean,
    val email_verified: Boolean,
    val partner_id: Int,
    val user_id: Int,
    val name: String,
    val email: String,
    val mobile: String,
    val image: String,
    val error: String?
) {
    // this is for login successfully
    constructor(
        email: String,
        mobile: String,
    ) : this(
        null,
        false,
        false,
        0,
        0,
        "",
        email,
        mobile,
        "",
        null
    )

    // to update user info: phone
    constructor(
        mobile: String,
    ) : this(
        null,
        false,
        false,
        0,
        0,
        "",
        "",
        mobile,
        "",
        null
    )
}


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
    // to update user info: name, dob and image
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

    // to update user info: name
    constructor(
        name: String
    ) : this(
        name,
        null,
        null,
        null,
        null
    )

    // to update user info: name and dob
    constructor(
        name: String,
        dob: String,
    ) : this(
        name,
        dob,
        null,
        null,
        null
    )


}

data class PasswordBodyParameters(
    val params: PasswordParams
)

data class PasswordParams(
    val login: String?,
    val new_password: String?
) {
    constructor(
        new_password: String,
    ) : this(

        null,
        new_password
    )
}

data class EmailBodyParameters(
    val params: EmailParams
)

data class EmailParams(
    val login: String
)