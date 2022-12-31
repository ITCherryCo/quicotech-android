package com.quico.tech.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quico.tech.R
import com.quico.tech.data.Constant.ADDRESS_TAG
import com.quico.tech.data.Constant.ALL
import com.quico.tech.data.Constant.CONNECTION

import com.quico.tech.data.Constant.EN
import com.quico.tech.data.Constant.ERROR
import com.quico.tech.data.Constant.ONGOING_ORDERS
import com.quico.tech.data.Constant.SERVICE_TAG
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
   // private lateinit var repository:Repository
    private  val repository=Repository()

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

    private val _send_otp: MutableStateFlow<String> = MutableStateFlow("")
    val send_otp: StateFlow<String> get() = _send_otp

    // I called general web info because it could load multiple page as About us, terms , privacy policy...
    private val _general_web_info: MutableStateFlow<Resource<WebInfoResponse>> =
        MutableStateFlow(Resource.Nothing())
    val general_web_info: StateFlow<Resource<WebInfoResponse>> get() = _general_web_info

    init {
        context = getApplication<Application>().applicationContext
        prefManager = PrefManager(context)
       // repository= Repository(context)
    }

    interface ResponseStandard {
        fun onSuccess(success: Boolean, resultTitle: String, message: String)
        fun onFailure(success: Boolean, resultTitle: String, message: String)
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

    var user: User?
        get() = prefManager.current_user
        set(new_user) {
            prefManager.current_user = new_user
        }

    var canRegister: Boolean
        get() = can_register.value
        set(value) {
            viewModelScope.launch {
                _can_register.emit(value)
            }
        }

    var sendOtpPhoneNumber: String
        get() = send_otp.value
        set(value) {
            viewModelScope.launch {
                _send_otp.emit(value)
            }
        }

    /*   var current_session_id:String?
           get() = prefManager.session_id
           set(new_session_id) {
               prefManager.session_id = new_session_id
           }*/

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
                    val response = repository.register(params)
                    if (response.isSuccessful) {

                        if (response.body()?.result?.status != null) {
                            Log.d(USER_REGISTER_TAG, "$SUCCESS $response")
                            responseStandard.onSuccess(
                                true,
                                SUCCESS,
                                response.body()?.result!!.status!!
                            )

                            delay(200)
                            login(loginParams, null)
                        } else {
                            responseStandard.onFailure(
                                false,
                                ERROR,
                                getLangResources().getString(R.string.error_msg)
                            )
                            Log.d(USER_REGISTER_TAG, "$ERROR ${response.body()?.result?.error}")
                        }
                    } else {
                        Log.d(USER_REGISTER_TAG, "FAILUE  $response")
                        responseStandard.onFailure(
                            false,
                            ERROR,
                            getLangResources().getString(R.string.error_msg)
                        )
                    }
                } catch (e: Exception) {
                    Log.d(USER_REGISTER_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard.onFailure(
                        false,
                        ERROR,
                        getLangResources().getString(R.string.error_msg)
                    )
                }
            } else {
                Log.d(USER_REGISTER_TAG, "$CONNECTION}")
                responseStandard.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun login(params: RegisterBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    val response = repository.login(params)
                    if (response.isSuccessful) {
                        if (response.body()?.result != null) {
                            prefManager.cookies=null
                            //Log.d(USER_LOGIN_TAG, "user exists")
                            var session_id = ""
                            response.headers().get("Set-Cookie")?.let { cookieHeader ->
                                var sessionFirstPart = cookieHeader.substringBefore(";")
                                session_id = sessionFirstPart.substringAfter("=")
                                Log.d(USER_LOGIN_TAG, "first hit $session_id")
                            }
                            var login_session_id = response.body()?.result?.session_id
                            Log.d(USER_LOGIN_TAG, "SESSION_IDS $session_id ")

                            user = response.body()?.result
                            user = user!!.copy(session_id = session_id)
                            prefManager.session_id = session_id
                            prefManager.cookies = response.headers().get("Set-Cookie")

                            responseStandard?.onSuccess(
                                true,
                                SUCCESS,
                                getLangResources().getString(R.string.login_successfully)
                            )
                        } else {
                            Log.d(USER_LOGIN_TAG, "$ERROR ${response.body()}")
                            responseStandard?.onFailure(false, ERROR, response.body().toString())
                            // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                        }
                    } else {
                        Log.d(USER_LOGIN_TAG, "FAILUER ${response.body()}")
                        responseStandard?.onFailure(false, "FAILUER", response.body().toString())
                        //  responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))

                    }
                } catch (e: Exception) {
                    Log.d(USER_LOGIN_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(false, "EXCEPTION", e.message.toString())
                    // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))

                }
            } else {
                Log.d(USER_LOGIN_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)

            }
        }
    }

    fun getSessionID(responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            val response = repository.getSession()
            if (response.isSuccessful) {
                if (response.isSuccessful) {
                    /* var session_id = ""
                     response.headers().get("Set-Cookie")?.let { cookieHeader ->
                         var sessionFirstPart = cookieHeader.substringBefore(";")
                         session_id = sessionFirstPart.substringAfter("=")
                         Log.d(USER_LOGIN_TAG, "first hit $session_id")
                     }*/
                    user = user!!.copy(session_id = response.body()?.result)

                    responseStandard?.onSuccess(
                        true,
                        SUCCESS,
                        getLangResources().getString(R.string.login_successfully)
                    )
                    Log.d(USER_LOGIN_TAG, "GET_SESSION_ID ${response.body()?.result}")

                } else {
                    Log.d(USER_LOGIN_TAG, "$ERROR ${response.body()}")
                }
            } else {
                Log.d(USER_LOGIN_TAG, "FAILUER ${response.body()}")

            }
        }

    }
/* fun getUser(session_id: String) {
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
 }*/

    fun logout(session_id: String, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    user?.session_id?.let { session_id ->
                        //val response = repository.logout2("$SESSION_ID=$session_id")
                        val response = repository.logout()

                        Log.d("SESSION_ID", "$session_id")

                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(USER_LOGOUT_TAG, "$SUCCESS")
                                user = null
                                prefManager.cookies = null
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    response.body().toString()
                                )
                            } else {
                                Log.d(USER_LOGOUT_TAG, "FAILURE  ${response.body()}")
                                //responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                                responseStandard?.onFailure(false, "FAILURE", "${response.body()}")
                            }
                        } else {
                            Log.d(
                                USER_LOGOUT_TAG,
                                "$ERROR ${response.body()}  ${response.body()?.result}"
                            )
                            // responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                            responseStandard?.onFailure(false, "NOT SUCCESS", "${response.body()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.d(USER_LOGOUT_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(false, ERROR, "EXCEPTION ${e.message.toString()}")
                    //responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                }

            } else {
                responseStandard?.onFailure(false, CONNECTION, "$CONNECTION")
                Log.d(USER_LOGOUT_TAG, "$CONNECTION")
            }
        }
    }


    fun updateUserInfo(params: UpdateUserBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    user?.session_id?.let { session_id ->
                        val response =
                            //repository.updateUserInfo(session_id, params)
                            repository.updateUserInfo(params)
                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(USER_UPDATE_TAG, "Success ${response.body()?.result?.status}")
                                user = user!!.copy(name = params.params.name!!)
                                // later when receiving dob with user change it
                                if (!params.params.image.isNullOrEmpty())
                                    user = user!!.copy(image = "data:image/jpeg;base64,${params.params.image!!}")

                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.info_updated_successfully)
                                )
                            } else {
                                Log.d(USER_UPDATE_TAG, "$ERROR ${response.body()?.result?.error}")
                                //  responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "$ERROR ${response.body()}"
                                )
                            }
                        } else {
                            Log.d(USER_UPDATE_TAG, "FAILUER ${response.body()}")
                            // responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                            responseStandard?.onFailure(false, ERROR, "FAILUER ${response.body()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.d(USER_UPDATE_TAG, "EXCEPTION ${e.message.toString()}")
                    // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                    responseStandard?.onFailure(false, ERROR, "EXCEPTION ${e.message.toString()}")
                }
            } else {
                Log.d(USER_UPDATE_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)

            }
        }
    }


    fun updateEmail(params: UpdateUserBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    user?.session_id?.let { session_id ->
                        val response = repository.updateEmail(session_id!!, params) //_subcategories
                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(USER_LOGIN_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.info_updated_successfully)
                                )
                            } else {
                                Log.d(USER_UPDATE_TAG, "$ERROR ${response.body()}")
                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    getLangResources().getString(R.string.error_msg)
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(USER_UPDATE_TAG, "FAILUER ${response.body()}")
                            responseStandard?.onFailure(
                                false,
                                ERROR,
                                getLangResources().getString(R.string.error_msg)
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.d(USER_UPDATE_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(
                        false,
                        ERROR,
                        getLangResources().getString(R.string.error_msg)
                    )
                }
            } else {
                Log.d(USER_UPDATE_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun updateMobile(params: RegisterBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    user?.session_id?.let { session_id ->
                        val response =
                            repository.updateMobile(session_id!!, params) //_subcategories
                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(USER_UPDATE_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.phone_changed_successfully)
                                )
                            } else {
                                Log.d(USER_UPDATE_TAG, "$ERROR ${response.body()?.result?.error}")
                                // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "$ERROR ${response.body()}"
                                )
                            }
                        } else {
                            Log.d(USER_UPDATE_TAG, "FAILUER ${response.body()}")
                            // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                            responseStandard?.onFailure(false, ERROR, "FAILUER ${response.body()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.d(USER_UPDATE_TAG, "EXCEPTION ${e.message.toString()}")
                    //responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                    responseStandard?.onFailure(false, ERROR, "EXCEPTION ${e.message.toString()}")
                }
            } else {
                Log.d(USER_UPDATE_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun addEditAddress(
        address_id: Int,
        params: AddressBodyParameters,
        responseStandard: ResponseStandard?
    ) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    user?.session_id?.let { session_id ->
                        Log.d(ADDRESS_TAG, "$session_id")
                        Log.d("SESSION_ID", "$session_id")
                        var response: Response<RegisterResponse>? = null
                        if (address_id != 0)
                            response = repository.editAddress( address_id, params)
                           // response = repository.editAddress(session_id!!, address_id, params)
                        else
                            response = repository.addAddress( params)
                           // response = repository.addAddress(session_id!!, params)

                        /* val response: Response<String> = Ion.with(context)
                             .load("POST", URLbuilder.getURL())
                             .setHeader("x-api", " API KEY HERE ")
                             .setStringBody(feedback.toJson())
                             .asString()
                             .withResponse()
                             .get()*/

                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(ADDRESS_TAG, "Success")
                                var msg =
                                    getLangResources().getString(R.string.address_created_successfully)
                                if (address_id != 0)
                                    msg =
                                        getLangResources().getString(R.string.address_updated_successfully)
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    msg
                                )
                            } else {
                                Log.d(ADDRESS_TAG, "$ERROR ${response.body()}")
                                /*  responseStandard?.onFailure(
                                      false,
                                      ERROR,
                                      getLangResources().getString(R.string.error_msg)
                                  )*/

                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    response.body().toString()
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(ADDRESS_TAG, "FAILUER ${response.body()}")
                            /*  responseStandard?.onFailure(
                                  false,
                                  ERROR,
                                  getLangResources().getString(R.string.error_msg)
                              )*/
                            responseStandard?.onFailure(
                                false,
                                "FAILUER",
                                response.body().toString()
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.d(ADDRESS_TAG, "EXCEPTION ${e.message.toString()}")
                    /* responseStandard?.onFailure(
                         false,
                         ERROR,
                         getLangResources().getString(R.string.error_msg)
                     )*/
                    responseStandard?.onFailure(
                        false,
                        "EXCEPTION",
                        "${e.message.toString()}"
                    )
                }
            } else {
                Log.d(ADDRESS_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun getAddresses(loadWithSwipeRefresh: Boolean) {
        viewModelScope.launch {
            if (loadWithSwipeRefresh)
                _addresses.emit(Resource.Loading())

            if (checkInternet(context)) {
                try {
                    Log.d("SESSION_ID", "${user?.session_id}")

                    // val response = repository.getAddresses2(user?.session_id!!)
                    val response = repository.getAddresses()

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(ADDRESS_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _addresses.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(ADDRESS_TAG, "ERROR ${response}}")

                        _addresses.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(ADDRESS_TAG, "EXCEPTION ${e.message}}}")
                    _addresses.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(ADDRESS_TAG, "$CONNECTION}")
                _addresses.emit(Resource.Connection())
            }
        }
    }

    fun deleteAddress(params: IDBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    user?.session_id?.let { session_id ->
                        Log.d(ADDRESS_TAG, "$session_id")
                        Log.d("SESSION_ID", "$session_id")

                        val response =
                            repository.deleteAddress(params)
                           // repository.deleteAddress(session_id!!, params)
                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(ADDRESS_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.address_deleted_successfully)
                                )
                                getAddresses(false)
                                // must load
                            } else {
                                Log.d(ADDRESS_TAG, "$ERROR ${response.body()}")
                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    getLangResources().getString(R.string.error_msg)
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(ADDRESS_TAG, "FAILUER ${response.body()}")
                            responseStandard?.onFailure(
                                false,
                                ERROR,
                                getLangResources().getString(R.string.error_msg)
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.d(ADDRESS_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(
                        false,
                        ERROR,
                        getLangResources().getString(R.string.error_msg)
                    )
                }
            } else {
                Log.d(ADDRESS_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun getServices() {
        viewModelScope.launch {

            _services.emit(Resource.Loading())

            if (checkInternet(context)) {
                try {
                    Log.d("SESSION_ID", "${user?.session_id}")
                    val response = repository.getServices()

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(SERVICE_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _services.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(SERVICE_TAG, "ERROR ${response}}")

                        _services.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(SERVICE_TAG, "EXCEPTION ${e.message}}}")
                    _services.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(SERVICE_TAG, "$CONNECTION}")
                _services.emit(Resource.Connection())
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