package com.quico.tech.connection

import com.quico.tech.model.*
import retrofit2.Response
import retrofit2.http.*


interface API {

    @POST("register")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body registerBody: RegisterBodyParameters): Response<RegisterResponse>

    @POST("login")
   // @Headers("Content-Type: application/json")
    suspend fun login(@Body registerBody: RegisterBodyParameters): Response<UserResponse>

    @GET("getUser")
   // @Headers("Content-Type: application/json")
    suspend fun getUser(@Header("Cookie") session_id:String): Response<UserResponse>

    @GET("logout")
    suspend fun logout(@Header("Cookie") session_id:String): Response<RegisterResponse>

    @PUT("updateUserInfo")
   // suspend fun updateUserInfo(@Header("Cookie") session_id:String,@Body updateUserBody: UpdateUserBodyParameters): Response<RegisterResponse>
    suspend fun updateUserInfo(@Body updateUserBody: UpdateUserBodyParameters): Response<RegisterResponse>

    @PUT("updateEmail")
    suspend fun updateEmail(@Header("Cookie") session_id:String,@Body updateUserBody: UpdateUserBodyParameters): Response<RegisterResponse>

    @PUT("updateMobile")
    suspend fun updateMobile(@Header("Cookie") session_id:String,@Body updateUserBody: RegisterBodyParameters): Response<RegisterResponse>

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