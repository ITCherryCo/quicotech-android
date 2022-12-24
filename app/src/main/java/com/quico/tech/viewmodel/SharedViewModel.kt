package com.quico.tech.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quico.tech.R
import com.quico.tech.connection.RetrofitInstance
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.ALL
import com.quico.tech.data.Constant.CONNECTION
import com.quico.tech.data.Constant.EN
import com.quico.tech.data.Constant.ERROR
import com.quico.tech.data.Constant.EXCEPTION
import com.quico.tech.data.Constant.ONGOING_ORDERS
import com.quico.tech.data.Constant.SESSION_ID
import com.quico.tech.data.Constant.SUCCESS
import com.quico.tech.data.Constant.USER_LOGIN_TAG
import com.quico.tech.data.Constant.USER_LOGOUT_TAG
import com.quico.tech.data.Constant.USER_REGISTER_TAG
import com.quico.tech.data.Constant.USER_UPDATE_TAG
import com.quico.tech.data.PrefManager
import com.quico.tech.model.*
import com.quico.tech.repository.Repository
import com.quico.tech.utils.Common.checkInternet
import com.quico.tech.utils.LocalHelper
import com.quico.tech.utils.Resource
import kotlinx.coroutines.delay
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

    private val _can_register: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val can_register: StateFlow<Boolean> get() = _can_register

    // I called general web info because it could load multiple page as About us, terms , privacy policy...
    private val _general_web_info: MutableStateFlow<Resource<WebInfoResponse>> =
        MutableStateFlow(Resource.Nothing())
    val general_web_info: StateFlow<Resource<WebInfoResponse>> get() = _general_web_info

    init {
        context = getApplication<Application>().applicationContext
        prefManager = PrefManager(context)
    }

    interface ResponseStandard {
        fun onSuccess(success: Boolean,resultTitle:String, message: String)
        fun onFailure(success: Boolean,resultTitle:String, message: String)
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

    var canRegister: Boolean
        get() = can_register.value
        set(value) {
            viewModelScope.launch {
                _can_register.emit(value)
            }
        }

    var current_session_id:String?
        get() = prefManager.session_id
        set(new_session_id) {
            prefManager.session_id = new_session_id
        }

    fun updateOrderFilterType(order_filter: String) {
        viewModelScope.launch {
            _orders_filter_type.emit(order_filter)
        }
    }

    fun register(params: RegisterBodyParameters, responseStandard: ResponseStandard) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    val loginParams = RegisterBodyParameters(
                        RegisterParams(
                            params.params.login!!, params.params.password!!
                        )
                    )
                    val response = repository.register(params) //_subcategories
                    if (response.isSuccessful) {

                        if (response.body()?.result?.status!=null) {
                            Log.d(USER_REGISTER_TAG, "$SUCCESS $response")
                            responseStandard.onSuccess(
                                true,
                                SUCCESS,
                                response.body()?.result!!.status!!
                            )

                            delay(200)
                            login(loginParams, null)
                        }
                        else{
                            responseStandard.onFailure(
                                false,
                                ERROR,
                                getLangResources().getString(R.string.error_msg)
                            )
                            Log.d(USER_REGISTER_TAG, "$ERROR ${response.body()?.result?.error}")
                        }
                    } else {
                        Log.d(USER_REGISTER_TAG, "FAILUE  $response")
                        responseStandard.onFailure(false,ERROR, getLangResources().getString(R.string.error_msg))
                    }
                } catch (e: Exception) {
                    Log.d(USER_REGISTER_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard.onFailure(false,ERROR, getLangResources().getString(R.string.error_msg))
                }
            } else {
                Log.d(USER_REGISTER_TAG, "$CONNECTION}")
                responseStandard.onFailure(false,CONNECTION, CONNECTION)
            }
        }
    }

    fun login(params: RegisterBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    val response = repository.login(params) //_subcategories
                    if (response.isSuccessful) {
                        if (response.body()?.result?.session_id!=null) {
                            Log.d(USER_LOGIN_TAG, "yessss")
                            var session_id = ""
                            response.headers().get("Set-Cookie")?.let { cookieHeader ->
                                var sessionFirstPart = cookieHeader.substringBefore(";")
                                session_id = sessionFirstPart.substringAfter("=")
                                Log.d(USER_LOGIN_TAG, "first hit $session_id")
                            }
                            current_session_id = session_id

                            responseStandard?.onSuccess(true, SUCCESS, getLangResources().getString(R.string.login_successfully))
                        }
                        else{
                            Log.d(USER_LOGIN_TAG, "$ERROR ${response.body()}")
                            responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))

                        }
                      //  getUser(session_id)
                    } else {
                        Log.d(USER_LOGIN_TAG, "FAILUER ${response.body()}")
                        responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))

                    }
                } catch (e: Exception) {
                    Log.d(USER_LOGIN_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))

                }
            } else {
                Log.d(USER_LOGIN_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION,CONNECTION)

            }
        }
    }

    fun getUser(session_id: String) {
       // Toast.makeText(context, "logged in ${session_id}", Toast.LENGTH_LONG) .show()
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    Toast.makeText(
                        context,
                        "logged in ${current_session_id}",
                        Toast.LENGTH_LONG
                    ).show()
                    val response = repository.getUser("$SESSION_ID=$session_id") //_subcategories
                    Log.d(USER_LOGIN_TAG, " $response")
                    Toast.makeText(context, "success ${response.body()?.data}", Toast.LENGTH_LONG) .show()
                    if (response.isSuccessful) {
                        Log.d(USER_LOGIN_TAG, "$SUCCESS ")
                       // prefManager.current_user =
//                        response.headers().get("Set-Cookie")?.let { cookieHeader->
//                            var sessionFirstPart = cookieHeader.substringBefore(";")
//                            var session_id = sessionFirstPart.substringAfter("=")
//                            Log.d(USER_LOGIN_TAG, "second hit $session_id")
//                        }
                    } else {
                        Log.d(USER_LOGIN_TAG, "$ERROR $response")
                    }
                } catch (e: Exception) {
                    Log.d(USER_LOGIN_TAG, "EXCEPTION ${e.message.toString()}")
                }
            } else {
                Log.d(USER_LOGIN_TAG, "$CONNECTION}")
            }
        }
    }

    fun logout(session_id: String, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {

                    val response = repository.logout("$SESSION_ID=$current_session_id") //_subcategories
                    if (response.isSuccessful) {
                        if (response.body()?.result?.status!=null) {
                            Log.d(USER_LOGOUT_TAG, "$SUCCESS")
                            current_session_id = null
                            responseStandard?.onSuccess(
                                true,
                                SUCCESS,
                                response.body().toString()
                            )
                        }
                        else{
                            Log.d(USER_LOGOUT_TAG, "FAILURE  ${response.body()}")
                            responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                           // responseStandard?.onFailure(false, "FAILURE","${response.body()}")
                        }
                    } else {
                        Log.d(USER_LOGOUT_TAG, "$ERROR ${response.body()}")
                        responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                       // responseStandard?.onFailure(false, "NOT SUCCESS","${response.body()}")
                    }
                } catch (e: Exception) {
                    Log.d(USER_LOGOUT_TAG, "EXCEPTION ${e.message.toString()}")
                   // responseStandard?.onFailure(false,ERROR,"EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                }
            } else {
                responseStandard?.onFailure(false,CONNECTION,"$CONNECTION")
                Log.d(USER_LOGOUT_TAG ,"$CONNECTION")
            }
        }
    }


    fun updateUserInfo(params: UpdateUserBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {

                    val response = repository.updateUserInfo(current_session_id!!,params) //_subcategories
                    if (response.isSuccessful) {
                        if (response.body()?.result?.status!=null) {
                            Log.d(USER_UPDATE_TAG, "Success ${response.body()?.result?.status}")
                            responseStandard?.onSuccess(true, SUCCESS, getLangResources().getString(R.string.info_updated_successfully))
                        }
                        else{
                            Log.d(USER_UPDATE_TAG, "$ERROR ${response.body()?.result?.error}")
                            responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                           // responseStandard?.onFailure(false, ERROR,"$ERROR ${response.body()}")
                        }
                    } else {
                        Log.d(USER_UPDATE_TAG, "FAILUER ${response.body()}")
                        responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                       // responseStandard?.onFailure(false, ERROR,"FAILUER ${response.body()}")
                    }
                } catch (e: Exception) {
                    Log.d(USER_UPDATE_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                   // responseStandard?.onFailure(false, ERROR,"EXCEPTION ${e.message.toString()}")
                }
            } else {
                Log.d(USER_UPDATE_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION,CONNECTION)

            }
        }
    }


    fun updateEmail(params: UpdateUserBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    val response = repository.updateEmail(current_session_id!!,params) //_subcategories
                    if (response.isSuccessful) {
                        if (response.body()?.result?.status!=null) {
                            Log.d(USER_LOGIN_TAG, "Success")
                            responseStandard?.onSuccess(true, SUCCESS, getLangResources().getString(R.string.info_updated_successfully))
                        }
                        else{
                            Log.d(USER_UPDATE_TAG, "$ERROR ${response.body()}")
                            responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                        }
                        //  getUser(session_id)
                    } else {
                        Log.d(USER_UPDATE_TAG, "FAILUER ${response.body()}")
                        responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                    }
                } catch (e: Exception) {
                    Log.d(USER_UPDATE_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                }
            } else {
                Log.d(USER_UPDATE_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION,CONNECTION)
            }
        }
    }

    fun updateMobile(params: RegisterBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    val response = repository.updateMobile(current_session_id!!,params) //_subcategories
                    if (response.isSuccessful) {
                        if (response.body()?.result?.status!=null) {
                            Log.d(USER_UPDATE_TAG, "Success")
                            responseStandard?.onSuccess(true, SUCCESS, getLangResources().getString(R.string.phone_changed_successfully))
                        }
                        else{
                            Log.d(USER_UPDATE_TAG, "$ERROR ${response.body()?.result?.error}")
                           // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                             responseStandard?.onFailure(false, ERROR,"$ERROR ${response.body()}")
                        }
                    } else {
                        Log.d(USER_UPDATE_TAG, "FAILUER ${response.body()}")
                       // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                         responseStandard?.onFailure(false, ERROR,"FAILUER ${response.body()}")
                    }
                } catch (e: Exception) {
                    Log.d(USER_UPDATE_TAG, "EXCEPTION ${e.message.toString()}")
                    //responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                     responseStandard?.onFailure(false, ERROR,"EXCEPTION ${e.message.toString()}")
                }
            } else {
                Log.d(USER_UPDATE_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION,CONNECTION)
            }
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