package com.quico.tech.connection

import com.quico.tech.model.*
import retrofit2.Response
import retrofit2.http.*


interface API {

    @POST("register")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body registerBody: RegisterBodyParameters): Response<StandardResponse>

    @POST("login")
   // @Headers("Content-Type: application/json")
    suspend fun login(@Body registerBody: RegisterBodyParameters): Response<StandardResponse>

    @GET("getUser")
   // @Headers("Content-Type: application/json")
    suspend fun getUser(@Header("Cookie") session_id:String): Response<UserResponse>

    @GET("logout")
    suspend fun logout(@Header("Cookie") session_id:String): Response<UserResponse>

    @GET("getAddresses")
    suspend fun getAddresses(@Query("customer_id") customer_id: Int?): Response<AddressResponse>

    @GET("getOngoingOrders")
    suspend fun getOngoingOrders(@Query("customer_id") customer_id: Int?): Response<OrderResponse>

    @GET("getDoneOrders")
    suspend fun getDoneOrders(@Query("customer_id") customer_id: Int?): Response<OrderResponse>

    @GET("termsAndConditions")
    suspend fun termsAndConditions(@Query("store_id") store_id: Int?): Response<WebInfoResponse>

    @GET("services")
    suspend fun services(@Query("store_id") store_id: Int?,@Query("store_id") maintenance_id: Int?): Response<ServiceResponse>

    @GET("loadCart")
    suspend fun loadCart(@Query("store_id") store_id: Int?,@Query("order_id") maintenance_id: Int?): Response<CartResponse>


}