package com.quico.tech.model

import android.provider.Telephony.Mms.Addr

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
    val have_sub_service: Boolean?,
    val id: Int,
    val image: String,
    val type: String
)

data class ServiceBodyParameters(
    val params: ServiceOrderParams
)

data class ServiceOrderParams(
    val audio: String,
    val audio_filename: String ,
    val delivery_type: String ,
    val description: String ,
    val address_id: Int? ,
    val image1: String,
    val image2: String ,
    val image3: String ,
    val image4: String ,
    val service_id: Int ,
    val service_type_id: Int,
) {
    constructor(service_id: Int) : this(
        "",
        "",
        "",
        "",
        null,
        "",
        "",
        "",
        "",
        service_id,
        0
    )

    override fun toString(): String {
        return "ServiceOrderParams(audio='$audio',\n audio_filename='$audio_filename',\n delivery_type='$delivery_type',\n description='$description',\n address_id=$address_id,\n image1='$image1',\n image2='$image2',\n image3='$image3',\n image4='$image4',\n service_id=$service_id,\n service_type_id=$service_type_id)"
    }


}

data class ServiceOrderResponse(
    val id: Any,
    val jsonrpc: String,
    val result: ServiceOrder?,
    val error: String?
    //val error: ErrorData?
)

data class ServiceOrder(
    val delivery_type: String,
    val id: Int,
    val image_1: String,
    val image_2: String,
    val image_3: String,
    val image_4: String,
    val problem_description: String,
    val scheduled_date_end: String,
    val scheduled_date_start: String,
    val scheduled_duration: String,
    val service: String,
    val service_nb: String,
    val service_type: String,
    val status: String,
    val address:Address?
)