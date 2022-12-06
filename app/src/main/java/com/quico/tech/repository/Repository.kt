package com.quico.tech.repository

import com.quico.tech.connection.RetrofitInstance

class Repository {

    suspend fun getAddresses(customer_id:Int) = RetrofitInstance.api.getAddresses(customer_id)

}