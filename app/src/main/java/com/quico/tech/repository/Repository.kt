package com.quico.tech.repository

import com.quico.tech.connection.RetrofitInstance
import com.quico.tech.model.RegisterBodyParameters

class Repository {

    suspend fun register(registerBody: RegisterBodyParameters) = RetrofitInstance.api.register(registerBody)
    suspend fun login(registerBody: RegisterBodyParameters) = RetrofitInstance.api.login(registerBody)
    suspend fun getUser(session_d:String) = RetrofitInstance.api.getUser(session_d)
    suspend fun logout(session_d:String) = RetrofitInstance.api.logout(session_d)

    suspend fun getAddresses(customer_id:Int) = RetrofitInstance.api.getAddresses(customer_id)
    suspend fun getOngoingOrders(customer_id:Int) = RetrofitInstance.api.getOngoingOrders(customer_id)
    suspend fun getDoneOrders(customer_id:Int) = RetrofitInstance.api.getDoneOrders(customer_id)
    suspend fun termsAndConditions(store_id:Int) = RetrofitInstance.api.termsAndConditions(store_id)
    suspend fun services(store_id:Int, maintenance_id:Int) = RetrofitInstance.api.services(store_id,maintenance_id)
    suspend fun loadCart(store_id:Int, order_id:Int) = RetrofitInstance.api.loadCart(store_id,order_id)

}