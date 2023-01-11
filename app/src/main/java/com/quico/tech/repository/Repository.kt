package com.quico.tech.repository

import com.quico.tech.connection.RetrofitInstance
import com.quico.tech.model.*

class Repository() {

    suspend fun register(registerBody: RegisterBodyParameters) = RetrofitInstance.api.register(registerBody)
    suspend fun login(registerBody: RegisterBodyParameters) = RetrofitInstance.api.login(registerBody)
    suspend fun getSession() = RetrofitInstance.api.getSession()
    suspend fun logout() = RetrofitInstance.api.logout()
    suspend fun changePassword(passwordBody: PasswordBodyParameters) = RetrofitInstance.api.changePassword(passwordBody)
    suspend fun forgetPassword(passwordBody: PasswordBodyParameters) = RetrofitInstance.api.forgetPassword(passwordBody)

    suspend fun updateUserInfo(updateBody: UpdateUserBodyParameters) = RetrofitInstance.api.updateUserInfo(updateBody)
    suspend fun updateEmail(updateBody: EmailBodyParameters) = RetrofitInstance.api.updateEmail(updateBody)
    suspend fun updateMobile( updateBody: RegisterBodyParameters) = RetrofitInstance.api.updateMobile( updateBody)

    suspend fun addAddress( updateBody: AddressBodyParameters) = RetrofitInstance.api.addAddress( updateBody)
    suspend fun editAddress( address_id: Int, updateBody: AddressBodyParameters) = RetrofitInstance.api.editAddress( address_id, updateBody)
    suspend fun getAddresses() = RetrofitInstance.api.getAddresses()
    suspend fun deleteAddress( idBodyParameters: IDBodyParameters) = RetrofitInstance.api.deleteAddress( idBodyParameters)

    suspend fun getServices() = RetrofitInstance.api.getServices()
    suspend fun getServiceTypes(service_id:Int) = RetrofitInstance.api.getServiceTypes(service_id)
    suspend fun getSubServiceTypes(service_type_id:Int) = RetrofitInstance.api.getSubServiceTypes(service_type_id)

    suspend fun getProduct(product_id: Int) = RetrofitInstance.api.getProduct(product_id)

    suspend fun getDeliveryOrders() = RetrofitInstance.api.getDeliveryOrders()
    suspend fun getServiceOrders() = RetrofitInstance.api.getServiceOrders()

    suspend fun addToCart( productBody: ProductBodyParameters) = RetrofitInstance.api.addToCart(productBody)
    suspend fun updateCartQuantity( productBody: ProductBodyParameters) = RetrofitInstance.api.updateCartQuantity(productBody)
    suspend fun removeFromCart( productBody: ProductBodyParameters) = RetrofitInstance.api.removeFromCart(productBody)
    suspend fun viewCart() = RetrofitInstance.api.viewCart()
    suspend fun subscribeToVip() = RetrofitInstance.api.subscribeToVip()

    suspend fun search(searchBodyParameters: SearchBodyParameters) = RetrofitInstance.api.search(searchBodyParameters)
    suspend fun searchCompare(searchBodyParameters: SearchBodyParameters) = RetrofitInstance.api.searchCompare(searchBodyParameters)
    suspend fun getAllCategories() = RetrofitInstance.api.getAllCategories()
    suspend fun getAllBrands() = RetrofitInstance.api.getAllBrands()
    suspend fun getHomeData() = RetrofitInstance.api.getHomeData()

    suspend fun addToWishlist( productBody: ProductBodyParameters) = RetrofitInstance.api.addToWishlist(productBody)
    suspend fun removeFromWishlist(productBody: ProductBodyParameters) = RetrofitInstance.api.removeFromWishlist(productBody)
    suspend fun viewWishlist() = RetrofitInstance.api.viewWishlist()

    suspend fun createDeliveryOrder( orderBody: OrderBodyParameters) = RetrofitInstance.api.createDeliveryOrder(orderBody)

    /* suspend fun getSession() = RetrofitInstance.newRetrofit(context).getSession()
     suspend fun getAddresses(session_id: String) = RetrofitInstance.newRetrofit(context).getAddresses(session_id)
     suspend fun logout(session_id: String) = RetrofitInstance.newRetrofit(context).logout(session_id)
     suspend fun login(registerBody: RegisterBodyParameters) = RetrofitInstance.newRetrofit(context).login(registerBody)//RetrofitInstance.api.login(registerBody)
     suspend fun editAddress(session_id: String, address_id: Int, updateBody: AddressBodyParameters) = RetrofitInstance.api.editAddress(session_id, address_id, updateBody)
     suspend fun deleteAddress(session_id: String, idBodyParameters: IDBodyParameters) = RetrofitInstance.api.deleteAddress(session_id, idBodyParameters)
     suspend fun addAddress(session_id: String, updateBody: AddressBodyParameters) = RetrofitInstance.api.addAddress(session_id, updateBody)
     suspend fun updateMobile(session_id: String, updateBody: RegisterBodyParameters) = RetrofitInstance.api.updateMobile(session_id, updateBody)
 */
}