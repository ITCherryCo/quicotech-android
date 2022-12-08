package com.quico.tech.connection

import com.quico.tech.model.AddressResponse
import com.quico.tech.model.OrderResponse
import com.quico.tech.model.WebInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface API {

    @GET("getAddresses.php")
    suspend fun getAddresses(@Query("customer_id") customer_id: Int?): Response<AddressResponse>

    @GET("getOngoingOrders.php")
    suspend fun getOngoingOrders(@Query("customer_id") customer_id: Int?): Response<OrderResponse>

    @GET("getDoneOrders.php")
    suspend fun getDoneOrders(@Query("customer_id") customer_id: Int?): Response<OrderResponse>

    @GET("termsAndConditions.php")
    suspend fun termsAndConditions(@Query("store_id") store_id: Int?): Response<WebInfoResponse>
}