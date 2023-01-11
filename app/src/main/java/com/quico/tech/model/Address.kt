package com.quico.tech.model

import android.os.Parcel
import android.os.Parcelable

data class AddressBodyParameters(
    val params: Address
)

data class IDBodyParameters(
    val params: ID
)

data class ID(
    val id: Int
)

data class AddressResponse(
    val id: Any,
    val jsonrpc: String,
    val result: List<Address>?,
    val error: String?
    //val error: ErrorData?

)

data class Address(
    val city: String,
    val country: String,
    val id: Int,
    val mobile: String,
    val name: String,
    val email: String?,
    val state: String?,
    val street: String,
    val street2: String,
    val zip: String
) :Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    // to send params
    constructor(
         city: String,
         name: String,
         state: String,
         street: String,
         street2: String,
         zip: String
    ) : this(
        city,
        "",
        0,
        "",
        name,
        null,
        state,
        street,
        street2,
        zip
    )

    // to receive params

    constructor(
        city: String,
        country: String,
        id: Int,
        mobile: String,
        name: String,
        street: String,
        street2: String,
        zip: String
    ) : this(
        city,
        country,
        id,
        mobile,
        name,
        null,
        "",
        street,
        street2,
        zip
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeString(city)
        p0?.writeString(country)
        p0?.writeInt(id)
        p0?.writeString(mobile)
        p0?.writeString(name)
        p0?.writeString(street)
        p0?.writeString(street2)
        p0?.writeString(zip)
    }


    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}