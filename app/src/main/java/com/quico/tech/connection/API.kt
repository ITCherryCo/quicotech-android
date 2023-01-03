package com.quico.tech.connection

import com.quico.tech.model.*
import retrofit2.Response
import retrofit2.http.*


interface API {

    @POST("register")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body registerBody: RegisterBodyParameters): Response<RegisterResponse>

    @POST("login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body registerBody: RegisterBodyParameters): Response<UserResponse>

    @GET("logout")
    suspend fun logout(): Response<RegisterResponse>

    @PUT("updateUserInfo")
    // suspend fun updateUserInfo(@Header("Cookie") session_id:String,@Body updateUserBody: UpdateUserBodyParameters): Response<RegisterResponse>
    suspend fun updateUserInfo(@Body updateUserBody: UpdateUserBodyParameters): Response<RegisterResponse>

    @PUT("updateEmail")
    suspend fun updateEmail(
        @Header("Cookie") session_id: String,
        @Body updateUserBody: UpdateUserBodyParameters
    ): Response<RegisterResponse>

    @PUT("updateMobile")
    suspend fun updateMobile(
        @Body updateUserBody: RegisterBodyParameters
    ): Response<RegisterResponse>


    @POST("createDeliveryAddress")
    suspend fun addAddress(

        @Body updateUserBody: AddressBodyParameters
    ): Response<RegisterResponse>

    @PUT("updateDeliveryAddress/{address_id}")
    suspend fun editAddress(
        @Path("address_id") address_id: Int,
        @Body updateUserBody: AddressBodyParameters
    ): Response<RegisterResponse>

    @GET("getServiceTypes/{service_id}")
    suspend fun getServiceTypes(@Path("service_id") service_id: Int): Response<ServiceTypeResponse>

    @GET("getServices")
    // @Headers("Content-Type: application/json")
    suspend fun getServices(): Response<ServiceResponse>
    /*
    *  @PUT("updateMobile")
    suspend fun updateMobile(
        @Header("Cookie") session_id: String,
        @Body updateUserBody: RegisterBodyParameters
    ): Response<RegisterResponse>
*/
   /* @POST("createDeliveryAddress")
    suspend fun addAddress(
        @Header("Cookie") session_id: String,
        @Body updateUserBody: AddressBodyParameters
    ): Response<RegisterResponse>*/



 /*   @PUT("updateDeliveryAddress/{address_id}")
    suspend fun editAddress(
        @Header("Cookie") session_id: String,
        @Path("address_id") address_id: Int,
        @Body updateUserBody: AddressBodyParameters
    ): Response<RegisterResponse> */

    /*  @PUT
      suspend fun editAddress2(
          @Header("Cookie") session_id: String,
          @Url updateAddressURL: String,
          @Body updateUserBody: AddressBodyParameters
      ): Response<RegisterResponse> */

    @GET("getDeliveryAddresses")
    suspend fun getAddresses2(@Header("Cookie") session_id: String): Response<AddressResponse>

    @GET("getDeliveryAddresses")
   // @Headers("Content-Type: application/json")
    suspend fun getAddresses(): Response<AddressResponse>

    @POST("removeDeliveryAddress")
    suspend fun deleteAddress(
        @Body idBodyParameters: IDBodyParameters
    ): Response<RegisterResponse>

    @PUT("getProduct/{product_id}")
    suspend fun getProduct(
        @Path("product_id") product_id: Int
    ): Response<ProductResponse>

    /*
    *  @POST("removeDeliveryAddress")
    suspend fun deleteAddress(
        @Header("Cookie") session_id: String,
        @Body idBodyParameters: IDBodyParameters
    ): Response<RegisterResponse>
*/

    @GET("getSession")
    suspend fun getSession(): Response<SessionResponse>



    @GET("getOngoingOrders")
    suspend fun getOngoingOrders(@Query("customer_id") customer_id: Int?): Response<OrderResponse>

    @GET("getDoneOrders")
    suspend fun getDoneOrders(@Query("customer_id") customer_id: Int?): Response<OrderResponse>

    @GET("termsAndConditions")
    suspend fun termsAndConditions(@Query("store_id") store_id: Int?): Response<WebInfoResponse>

    @GET("services")
    suspend fun services(
        @Query("store_id") store_id: Int?,
        @Query("store_id") maintenance_id: Int?
    ): Response<ServiceResponse>

    @GET("loadCart")
    suspend fun loadCart(
        @Query("store_id") store_id: Int?,
        @Query("order_id") maintenance_id: Int?
    ): Response<CartResponse>


    @GET("getCategories")
    // @Headers("Content-Type: application/json")
    suspend fun getAllCategories(): Response<CategoryResponse>

}