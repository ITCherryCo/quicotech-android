package com.quico.tech.connection

import com.quico.tech.model.*
import retrofit2.Response
import retrofit2.http.*


interface API {

    @POST("register")
   // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun register(@Body registerBody: RegisterBodyParameters): Response<RegisterResponse>

    @POST("login")
   // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun login(@Body registerBody: RegisterBodyParameters): Response<UserResponse>

    @GET("logout")
    suspend fun logout(): Response<RegisterResponse>

    @POST("changePassword")
    @Headers("Content-Type: text/html")
    suspend fun changePassword(@Body passwordBody: PasswordBodyParameters): Response<RegisterResponse>

    @POST("resetPassword")
   // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun forgetPassword(@Body passwordBody: PasswordBodyParameters): Response<RegisterResponse>

    @PUT("updateUserInfo")
    @Headers("Content-Type: text/html")
    suspend fun updateUserInfo(@Body updateUserBody: UpdateUserBodyParameters): Response<RegisterResponse>

    @POST("updateEmail")
   // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun updateEmail(
        @Body emailBody: EmailBodyParameters
    ): Response<RegisterResponse>

    @PUT("updateMobile")
    suspend fun updateMobile(@Body updateUserBody: RegisterBodyParameters): Response<RegisterResponse>

    @POST("createDeliveryAddress")
    @Headers("Content-Type: text/html")
    suspend fun addAddress(@Body updateUserBody: AddressBodyParameters): Response<RegisterResponse>

    @PUT("updateDeliveryAddress/{address_id}")
    @Headers("Content-Type: text/html")
    suspend fun editAddress(@Path("address_id") address_id: Int, @Body updateUserBody: AddressBodyParameters): Response<RegisterResponse>

    @GET("getServiceTypes/{service_id}")
    suspend fun getServiceTypes(@Path("service_id") service_id: Int): Response<ServiceTypeResponse>

    @GET("getServices")
    suspend fun getServices(): Response<ServiceResponse>

    @GET("getDeliveryAddresses")
    suspend fun getAddresses(): Response<AddressResponse>

    @POST("removeDeliveryAddress")
    @Headers("Content-Type: text/html")
    suspend fun deleteAddress(@Body idBodyParameters: IDBodyParameters): Response<RegisterResponse>

    @GET("getProduct/{product_id}")
    suspend fun getProduct(@Path("product_id") product_id: Int): Response<ProductResponse>

    @GET("getSession")
    suspend fun getSession(): Response<SessionResponse>

    @GET("getOngoingOrders")
    suspend fun getOngoingOrders(@Query("customer_id") customer_id: Int?): Response<OrderResponse>

    @GET("getDoneOrders")
    suspend fun getDoneOrders(@Query("customer_id") customer_id: Int?): Response<OrderResponse>

    @POST("addToCart")
    //@Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun addToCart(@Body productBodyParameters: ProductBodyParameters): Response<RegisterResponse>

    @POST("updateCartQuantity")
   // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun updateCartQuantity(@Body productBodyParameters: ProductBodyParameters): Response<RegisterResponse>

    @POST("removeFromCart")
   // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun removeFromCart(@Body productBodyParameters: ProductBodyParameters): Response<RegisterResponse>

    @GET("viewCart")
    suspend fun viewCart(): Response<ProductsResponse>

    @POST("addToWishlist")
   // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun addToWishlist(@Body productBodyParameters: ProductBodyParameters): Response<RegisterResponse>

    @POST("removeFromWishlist")
   // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun removeFromWishlist(@Body productBodyParameters: ProductBodyParameters): Response<RegisterResponse>

    @GET("viewWishlist")
    suspend fun viewWishlist(): Response<ProductsResponse>

    @POST("subscribe")
   // @Headers("Content-Type: application/json")
   // @Headers("Content-Type: text/html")
    suspend fun subscribeToVip(): Response<RegisterResponse>

    @POST("search")
   // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun search(
        @Body searchBodyParameters: SearchBodyParameters
    ): Response<SearchResponse>

    @POST("searchCompare")
    // @Headers("Content-Type: application/json")
    @Headers("Content-Type: text/html")
    suspend fun searchCompare(@Body searchBodyParameters: SearchBodyParameters): Response<ProductsResponse>

    @GET("getCategories")
    // @Headers("Content-Type: application/json")
    suspend fun getAllCategories(): Response<CategoryResponse>

    @GET("getBrands")
    // @Headers("Content-Type: application/json")
    suspend fun getAllBrands(): Response<BrandResponse>

    @GET("homepage")
    // @Headers("Content-Type: application/json")
    suspend fun getHomeData(): Response<HomeDataResponse>

}