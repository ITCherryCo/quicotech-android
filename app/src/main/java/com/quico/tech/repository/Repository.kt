package com.quico.tech.repository

import com.quico.tech.connection.RetrofitInstance

class Repository {

    suspend fun getAddresses(customer_id:Int) = RetrofitInstance.api.getAddresses(customer_id)
    suspend fun getOngoingOrders(customer_id:Int) = RetrofitInstance.api.getOngoingOrders(customer_id)
    suspend fun getDoneOrders(customer_id:Int) = RetrofitInstance.api.getDoneOrders(customer_id)
    suspend fun termsAndConditions(store_id:Int) = RetrofitInstance.api.termsAndConditions(store_id)

}