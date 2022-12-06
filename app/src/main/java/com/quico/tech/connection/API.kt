package com.quico.tech.connection

import com.quico.tech.model.AddressResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface API {

    @GET("getAddresses.php")
    suspend fun getAddresses(@Query("customer_id") customer_id: Int?): Response<AddressResponse>
}