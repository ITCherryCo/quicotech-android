package com.quico.tech.repository

import android.content.Context
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

    suspend fun getProduct(product_id: Int) = RetrofitInstance.api.getProduct(product_id)

    suspend fun getOngoingOrders(customer_id: Int) = RetrofitInstance.api.getOngoingOrders(customer_id)
    suspend fun getDoneOrders(customer_id: Int) = RetrofitInstance.api.getDoneOrders(customer_id)

    suspend fun addToCart( cartBody: CartBodyParameters) = RetrofitInstance.api.addToCart(cartBody)
    suspend fun removeFromCart( cartBody: CartBodyParameters) = RetrofitInstance.api.removeFromCart(cartBody)
    suspend fun viewCart() = RetrofitInstance.api.viewCart()

    suspend fun search(searchBodyParameters: SearchBodyParameters) = RetrofitInstance.api.search(searchBodyParameters)

    suspend fun getAllCategories() = RetrofitInstance.api.getAllCategories()


    suspend fun getAllBrands() = RetrofitInstance.api.getAllBrands()
    suspend fun getHomeData() = RetrofitInstance.api.getHomeData()

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