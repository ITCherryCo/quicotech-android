package com.quico.tech.repository

import com.quico.tech.connection.RetrofitInstance
import com.quico.tech.model.AddressBodyParameters
import com.quico.tech.model.RegisterBodyParameters
import com.quico.tech.model.UpdateUserBodyParameters
import com.quico.tech.model.UpdateUserParams

class Repository {

    suspend fun register(registerBody: RegisterBodyParameters) = RetrofitInstance.api.register(registerBody)
    suspend fun login(registerBody: RegisterBodyParameters) = RetrofitInstance.api.login(registerBody)
    suspend fun getUser(session_id:String) = RetrofitInstance.api.getUser(session_id)
    suspend fun logout(session_id:String) = RetrofitInstance.api.logout(session_id)
    suspend fun updateUserInfo(updateBody:UpdateUserBodyParameters) = RetrofitInstance.api.updateUserInfo(updateBody)
    suspend fun updateEmail(session_id:String,updateBody:UpdateUserBodyParameters) = RetrofitInstance.api.updateEmail(session_id,updateBody)
    suspend fun updateMobile(session_id:String,updateBody:RegisterBodyParameters) = RetrofitInstance.api.updateMobile(session_id,updateBody)

    suspend fun addAddress(session_id:String,updateBody:AddressBodyParameters) = RetrofitInstance.api.addAddress(session_id,updateBody)
    suspend fun getAddresses(session_id:String) = RetrofitInstance.api.getAddresses(session_id)
    suspend fun getSession() = RetrofitInstance.api.getSession()

    suspend fun getOngoingOrders(customer_id:Int) = RetrofitInstance.api.getOngoingOrders(customer_id)
    suspend fun getDoneOrders(customer_id:Int) = RetrofitInstance.api.getDoneOrders(customer_id)
    suspend fun termsAndConditions(store_id:Int) = RetrofitInstance.api.termsAndConditions(store_id)
    suspend fun services(store_id:Int, maintenance_id:Int) = RetrofitInstance.api.services(store_id,maintenance_id)
    suspend fun loadCart(store_id:Int, order_id:Int) = RetrofitInstance.api.loadCart(store_id,order_id)

}