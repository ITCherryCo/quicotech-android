package com.quico.tech.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quico.tech.data.Constant.ALL
import com.quico.tech.data.Constant.CONNECTION
import com.quico.tech.data.Constant.EN
import com.quico.tech.data.Constant.ERROR
import com.quico.tech.data.Constant.ONGOING_ORDERS
import com.quico.tech.data.Constant.SUCCESS
import com.quico.tech.data.PrefManager
import com.quico.tech.model.*
import com.quico.tech.repository.Repository
import com.quico.tech.utils.Common.checkInternet
import com.quico.tech.utils.LocalHelper
import com.quico.tech.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var prefManager: PrefManager
    private var set_language = "fr"
    lateinit var resources: Resources
    private val localeHelper: LocalHelper = LocalHelper()
    private lateinit var context: Context
    private val repository = Repository()

    private val _addresses: MutableStateFlow<Resource<AddressResponse>> =
        MutableStateFlow(Resource.Nothing())
    val addresses: StateFlow<Resource<AddressResponse>> get() = _addresses

    private val _orders_filter_type: MutableStateFlow<String> = MutableStateFlow(ALL)
    val orders_filter_type: StateFlow<String> get() = _orders_filter_type

    private val _orders: MutableStateFlow<Resource<OrderResponse>> =
        MutableStateFlow(Resource.Nothing())
    val orders: StateFlow<Resource<OrderResponse>> get() = _orders

    private val _cards: MutableStateFlow<Resource<OrderResponse>> =
        MutableStateFlow(Resource.Nothing())
    val cards: StateFlow<Resource<OrderResponse>> get() = _cards

    // for cart items
    private val _cart_items: MutableStateFlow<Resource<CartResponse>> =
        MutableStateFlow(Resource.Nothing())
    val cart_items: StateFlow<Resource<CartResponse>> get() = _cart_items

    private val _services: MutableStateFlow<Resource<ServiceResponse>> =
        MutableStateFlow(Resource.Nothing())
    val services: StateFlow<Resource<ServiceResponse>> get() = _services

    // I called general web info because it could load multiple page as About us, terms , privacy policy...
    private val _general_web_info: MutableStateFlow<Resource<WebInfoResponse>> =
        MutableStateFlow(Resource.Nothing())
    val general_web_info: StateFlow<Resource<WebInfoResponse>> get() = _general_web_info

    init {
        context = getApplication<Application>().applicationContext
        prefManager = PrefManager(context)
    }

    // store id is the language id that we want to display from database if store_id = 1-> language is english
    // if store_id = 2-> language is arabic
    fun getStoreId(): Int {
        if (getLanguage().equals(EN))
            return 1
        else
            return 2
    }

    fun getLangResources(): Resources {
        set_language = prefManager.language.toString()
        var ctx = localeHelper.setLocale(context, set_language)
        resources = ctx.resources
        return resources
    }

    fun getLanguage(): String = prefManager.language.toString()

    fun updateLanguage(new_language: String) {
        prefManager.language = new_language
        getLangResources()
    }

//    var order_filter: String?
//        get() = order
//        set(new_language) {
//            editor.putString(PrefManager.LANGUAGE, new_language)
//            editor.apply()
//        }

    fun updateOrderFilterType(order_filter: String) {
        viewModelScope.launch {
            _orders_filter_type.emit(order_filter)
        }
    }


    fun getAddresses(
        customer_id: Int,
    ) {
        viewModelScope.launch {
            _addresses.emit(Resource.Loading())
            if (checkInternet(context)) {
                try {
                    val response = repository.getAddresses(customer_id) //_subcategories

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            _addresses.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        _addresses.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    _addresses.emit(Resource.Error(ERROR))
                }
            } else {
                _addresses.emit(Resource.Connection())
            }
        }
    }

    fun getOrders(customer_id: Int, orders_type: String) {
        viewModelScope.launch {
            _orders.emit(Resource.Loading())
            if (checkInternet(context)) {
                try {
                    var response: Response<OrderResponse>? = null
                    response = if (orders_type.equals(ONGOING_ORDERS)) repository.getOngoingOrders(
                        customer_id
                    ) else
                        repository.getOngoingOrders(customer_id)

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            _orders.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        _orders.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d("PROJECT_RESPONSE", "EXCEPTION  " + e.message.toString())
                    _orders.emit(Resource.Error(ERROR))
                }
            } else {
                _orders.emit(Resource.Connection())
            }
        }
    }

    fun getServices(
        maintenance_id: Int
    ) {
        viewModelScope.launch {
            _services.emit(Resource.Loading())
            if (checkInternet(context)) {
                try {
                    val response =
                        repository.services(getStoreId(), maintenance_id) //_subcategories

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            _services.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        _services.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    _services.emit(Resource.Error(ERROR))
                }
            } else {
                _services.emit(Resource.Connection())
            }
        }
    }

    fun loadCart(reloadWithoutSwipe: Boolean, order_id: Int) {
        viewModelScope.launch {

            if (!reloadWithoutSwipe)
                _cart_items.emit(Resource.Loading())
            if (checkInternet(context)) {
                try {
                    var response = repository.loadCart(getStoreId(), order_id)
                    if (response!!.isSuccessful) {
                        response?.body()?.let { resultResponse ->
                            if (resultResponse.result.equals(SUCCESS)) {
                                _cart_items.emit(Resource.Success(resultResponse))
                            } else {
                                _cart_items.emit(Resource.Error(resultResponse.message))
                                Log.d("CART_RESPONSE", "no")
                            }
                        }
                    } else {
                        _cart_items.emit(Resource.Error(response.message()))
                        Log.d("CART_RESPONSE", "not success")
                    }
                } catch (e: Exception) {
                    Log.d("CART_RESPONSE", "EXCEPTION  " + e.message.toString())
                    _cart_items.emit(Resource.Error(ERROR))
                }
            } else {
                _cart_items.emit(Resource.Error(CONNECTION))
            }
        }
    }


    fun generalWebInfo(
    ) {
        viewModelScope.launch {
            _general_web_info.emit(Resource.Loading())
            if (checkInternet(context)) {
                try {
                    val response = repository.termsAndConditions(getStoreId()) //_subcategories

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            _general_web_info.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        _general_web_info.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    _general_web_info.emit(Resource.Error(ERROR))
                }
            } else {
                _general_web_info.emit(Resource.Connection())
            }
        }
    }

}