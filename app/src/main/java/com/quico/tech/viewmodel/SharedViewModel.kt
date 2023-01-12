package com.quico.tech.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.quico.tech.R
import com.quico.tech.data.Constant.ADDRESS_TAG
import com.quico.tech.data.Constant.ALL
import com.quico.tech.data.Constant.BEST_SELLING_TAG
import com.quico.tech.data.Constant.BRAND_TAG
import com.quico.tech.data.Constant.CART_TAG
import com.quico.tech.data.Constant.CATEGORY_TAG
import com.quico.tech.data.Constant.CONNECTION

import com.quico.tech.data.Constant.EN
import com.quico.tech.data.Constant.ERROR
import com.quico.tech.data.Constant.EXCEPTION
import com.quico.tech.data.Constant.HOME_TAG
import com.quico.tech.data.Constant.ONGOING_ORDERS
import com.quico.tech.data.Constant.PRODUCTS_BY_CATEGORY_TAG
import com.quico.tech.data.Constant.PRODUCTS_BY_SUB_CATEGORY_TAG
import com.quico.tech.data.Constant.PRODUCT_TAG
import com.quico.tech.data.Constant.SERVICE_TAG
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
    private val _cart_items: MutableStateFlow<Resource<ProductsResponse>> =
        MutableStateFlow(Resource.Nothing())
    val cart_items: StateFlow<Resource<ProductsResponse>> get() = _cart_items

    private val _services: MutableStateFlow<Resource<ServiceResponse>> =
        MutableStateFlow(Resource.Nothing())
    val services: StateFlow<Resource<ServiceResponse>> get() = _services

    private val _service_types: MutableStateFlow<Resource<ServiceTypeResponse>> =
        MutableStateFlow(Resource.Nothing())
    val service_types: StateFlow<Resource<ServiceTypeResponse>> get() = _service_types

    private val _can_register: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val can_register: StateFlow<Boolean> get() = _can_register

    private val _send_otp: MutableStateFlow<String> = MutableStateFlow("")
    val send_otp: StateFlow<String> get() = _send_otp


    private val _product: MutableStateFlow<Resource<ProductResponse>> =
        MutableStateFlow(Resource.Nothing())
    val product: StateFlow<Resource<ProductResponse>> get() = _product

    private val _search_products: MutableStateFlow<Resource<SearchResponse>> =
        MutableStateFlow(Resource.Nothing())
    val search_products: StateFlow<Resource<SearchResponse>> get() = _search_products

    private val _search_compare_products: MutableStateFlow<Resource<ProductsResponse>> =
        MutableStateFlow(Resource.Nothing())
    val search_compare_products: StateFlow<Resource<ProductsResponse>> get() = _search_compare_products

    private val _categories: MutableStateFlow<Resource<CategoryResponse>> =
        MutableStateFlow(Resource.Nothing())
    val categories: StateFlow<Resource<CategoryResponse>> get() = _categories

    private val _allBestSellingProducts: MutableStateFlow<Resource<ProductsResponse>> =
        MutableStateFlow(Resource.Nothing())
    val allBestSellingProducts: StateFlow<Resource<ProductsResponse>> get() = _allBestSellingProducts

    private val _productsByCategory: MutableStateFlow<Resource<CategoryDetailResponse>> =
        MutableStateFlow(Resource.Nothing())
    val productsByCategory: StateFlow<Resource<CategoryDetailResponse>> get() = _productsByCategory

    private val _productsBySubCategory: MutableStateFlow<Resource<SubCategoryResponse>> =
        MutableStateFlow(Resource.Nothing())
    val productsBySubCategory: StateFlow<Resource<SubCategoryResponse>> get() = _productsBySubCategory

    private val _brands: MutableStateFlow<Resource<BrandResponse>> =
        MutableStateFlow(Resource.Nothing())
    val brands: StateFlow<Resource<BrandResponse>> get() = _brands

    private val _homeData: MutableStateFlow<Resource<HomeDataResponse>> =
        MutableStateFlow(Resource.Nothing())
    val homeData: StateFlow<Resource<HomeDataResponse>> get() = _homeData

    private val _wishlist: MutableStateFlow<Resource<ProductsResponse>> =
        MutableStateFlow(Resource.Nothing())
    val wishlist: StateFlow<Resource<ProductsResponse>> get() = _wishlist

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

    var vip_subsription: Boolean
        get() = prefManager.vip_subsription
        set(value) {
            prefManager.vip_subsription = value
        }

    var temporar_user: RegisterParams?
        get() = prefManager.temporar_user

        set(temporar_user) {
            prefManager.temporar_user = temporar_user
        }

    var operation_type: String
        get() = prefManager.operation_type
        set(operation_type) {
            prefManager.operation_type = operation_type
        }

    var verification_type: String
        get() = prefManager.verification_type
        set(verification_type) {
            prefManager.verification_type = verification_type
        }


    /*   var current_session_id:String?
           get() = prefManager.session_id
           set(new_session_id) {
               prefManager.session_id = new_session_id
           }*/

    var requested_serive_order: ServiceOrder?
        get() = prefManager.requested_serive_order

        set(serive_order) {
            prefManager.requested_serive_order = serive_order
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
                            responseStandard.onFailure(false, ERROR, response.body()?.error!!)
                            // responseStandard.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                            Log.d(USER_REGISTER_TAG, "$ERROR ${response.body()?.error}")
                        }
                    } else {
                        Log.d(USER_REGISTER_TAG, "FAILURE  $response")
                        // responseStandard.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                        responseStandard.onFailure(false, "FAILURE", response.body()?.error!!)

                    }
                } catch (e: Exception) {
                    Log.d(USER_REGISTER_TAG, "EXCEPTION ${e.message.toString()}")
                    //  responseStandard.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                    responseStandard.onFailure(false, "EXCEPTION", "${e.message.toString()}")
                }
            } else {
                Log.d(USER_REGISTER_TAG, "$CONNECTION}")
                responseStandard.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun resetSession(){
        user=null
        prefManager.session_id=null
        prefManager.cookies=null
    }

    fun login(params: RegisterBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    val response = repository.login(params)
                    if (response.isSuccessful) {
                        if (response.body()?.result != null) {
                            prefManager.cookies = null
                            //Log.d(USER_LOGIN_TAG, "user exists")
                            var session_id = ""
                            response.headers().get("Set-Cookie")?.let { cookieHeader ->
                                var sessionFirstPart = cookieHeader.substringBefore(";")
                                session_id = sessionFirstPart.substringAfter("=")
                                Log.d(USER_LOGIN_TAG, "first hit $session_id")
                            }
                            //var login_session_id = response.body()?.result?.session_id
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
                            responseStandard?.onFailure(false, ERROR, "${response.body()?.error}")
                            // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                        }
                    } else {
                        Log.d(USER_LOGIN_TAG, "FAILUER ${response.body()}")
                        responseStandard?.onFailure(false, "FAILURE", "${response.body()?.error}")
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

    fun logout(responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                        val response = repository.logout()


                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(USER_LOGOUT_TAG, "$SUCCESS")
                                user = null
                                prefManager.cookies = null
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.logged_out)
                                )
                            } else {
                                Log.d(USER_LOGOUT_TAG, "FAILURE  ${response.body()?.error}")
                                //responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "${response.body()?.error}"
                                )
                            }
                        } else {
                            Log.d(
                                USER_LOGOUT_TAG,
                                "$ERROR ${response.body()}  ${response.body()?.error}"
                            )
                            // responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                            responseStandard?.onFailure(
                                false,
                                "FAILURE",
                                "${response.body()?.error}"
                            )

                    }
                } catch (e: Exception) {
                    Log.d(USER_LOGOUT_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(false, ERROR, "${e.message.toString()}")
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

                                if (!params.params.dob.isNullOrEmpty())
                                    user = user!!.copy(dob = "${params.params.dob!!}")

                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.info_updated_successfully)
                                )
                            } else {
                                Log.d(USER_UPDATE_TAG, "$ERROR ${response.body()?.error}")
                                //  responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "${response.body()?.error}"
                                )
                            }
                        } else {
                            Log.d(USER_UPDATE_TAG, "FAILUER ${response.body()?.error}")
                            // responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                            responseStandard?.onFailure(
                                false,
                                "FAILURE",
                                "${response.body()?.error}"
                            )
                        }

                } catch (e: Exception) {
                    Log.d(USER_UPDATE_TAG, "EXCEPTION ${e.message.toString()}")
                    // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                    responseStandard?.onFailure(false, EXCEPTION, "${e.message.toString()}")
                }
            } else {
                Log.d(USER_UPDATE_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)

            }
        }
    }


    fun updateEmail(params: EmailBodyParameters, responseStandard: ResponseStandard?) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                        val response = repository.updateEmail(params) //_subcategories
                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(USER_LOGIN_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.info_updated_successfully)
                                )
                            } else {
                                Log.d(USER_UPDATE_TAG, "$ERROR ${response.body()?.error}")
                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                   // getLangResources().getString(R.string.error_msg)
                                   "${response.body()?.error}"
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(USER_UPDATE_TAG, "FAILUER ${response.body()?.error}")
                            responseStandard?.onFailure(
                                false,
                                ERROR,
                               // getLangResources().getString(R.string.error_msg)
                                "${response.body()?.error}"
                            )
                        }

                } catch (e: Exception) {
                    Log.d(USER_UPDATE_TAG, "EXCEPTION ${e.message.toString()}")
                    responseStandard?.onFailure(
                        false,
                        "EXCEPTION",
                       // getLangResources().getString(R.string.error_msg)
                        "${e.message.toString()}"
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
                        val response =
                            repository.updateMobile(params) //_subcategories
                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(USER_UPDATE_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.phone_changed_successfully)
                                )
                            } else {
                                Log.d(USER_UPDATE_TAG, "$ERROR ${response.body()?.error}")
                                // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "$ERROR ${response.body()?.error}"
                                )
                            }
                        } else {
                            Log.d(USER_UPDATE_TAG, "FAILUER ${response.body()?.error}")
                            // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                            responseStandard?.onFailure(
                                false,
                                ERROR,
                                "${response.body()?.error}"
                            )

                    }
                } catch (e: Exception) {
                    Log.d(USER_UPDATE_TAG, "EXCEPTION ${e.message.toString()}")
                    //responseStandard?.onFailure(false, ERROR, getLangResources().getString(R.string.error_msg))
                    responseStandard?.onFailure(false, EXCEPTION, "${e.message.toString()}")
                }
            } else {
                Log.d(USER_UPDATE_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun changePassword(
        reset: Boolean,
        params: PasswordBodyParameters,
        responseStandard: ResponseStandard?
    ) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    var response: Response<RegisterResponse>? = null
                    if (reset)
                        response = repository.forgetPassword(params)
                    else
                        response = repository.changePassword(params)

                    if (response!!.isSuccessful) {
                        if (response.body()?.result != null) {
                            user = null
                            prefManager.cookies = null
                            responseStandard?.onSuccess(
                                true,
                                SUCCESS,
                                getLangResources().getString(R.string.password_changed)
                            )
                        } else {
                            Log.d(USER_LOGIN_TAG, "$ERROR ${response.body()?.error}")
                            responseStandard?.onFailure(false, ERROR, "${response.body()?.error}")
                            // responseStandard?.onFailure(false, ERROR,getLangResources().getString(R.string.error_msg))
                        }
                    } else {
                        Log.d(USER_LOGIN_TAG, "FAILUER ${response.body()}")
                        responseStandard?.onFailure(false, "FAILUER", "${response.body()?.error}")
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


    fun addEditAddress(
        address_id: Int,
        params: AddressBodyParameters,
        responseStandard: ResponseStandard?
    ) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                        Log.d(ADDRESS_TAG, "${user?.session_id}")
                        Log.d("SESSION_ID", "${user?.session_id}")
                        var response: Response<RegisterResponse>? = null
                        if (address_id != 0)
                            response = repository.editAddress(address_id, params)
                        else
                            response = repository.addAddress(params)

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
                                Log.d(ADDRESS_TAG, "$ERROR ${response.body()?.error}")
                                /*  responseStandard?.onFailure(
                                      false,
                                      ERROR,
                                      getLangResources().getString(R.string.error_msg)
                                  )*/

                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "${response.body()?.error}"
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
                                "FAILURE",
                                "${response.body()?.error}"
                            )
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

                    val response = repository.getAddresses()

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(ADDRESS_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _addresses.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(ADDRESS_TAG, "ERROR ${response.body()?.error}")
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
                        Log.d(ADDRESS_TAG, "${user?.session_id}")
                        Log.d("SESSION_ID", "${user?.session_id}")

                        val response =
                            repository.deleteAddress(params)
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
                                Log.d(ADDRESS_TAG, "$ERROR ${response.body()?.error}")
                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    getLangResources().getString(R.string.error_msg)
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(ADDRESS_TAG, "FAILURE ${response.body()?.error}")
                            responseStandard?.onFailure(
                                false,
                                ERROR,
                                getLangResources().getString(R.string.error_msg)
                            )
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
                    val response = repository.getServices()

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(SERVICE_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _services.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(SERVICE_TAG, "ERROR ${response.body()?.error}")

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


    fun getServiceTypes(service_id: Int,subservice:Boolean) {
        viewModelScope.launch {

            _service_types.emit(Resource.Loading())

            if (checkInternet(context)) {
                try {
                    var response : Response<ServiceTypeResponse>?=null
                    if (subservice)
                        response = repository.getSubServiceTypes(service_id)
                    else
                        response = repository.getServiceTypes(service_id)

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(SERVICE_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _service_types.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(SERVICE_TAG, "ERROR ${response}}")

                        _service_types.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(SERVICE_TAG, "EXCEPTION ${e.message}}}")
                    _service_types.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(SERVICE_TAG, "$CONNECTION}")
                _service_types.emit(Resource.Connection())
            }
        }
    }

    fun getProduct(product_id: Int) {
        viewModelScope.launch {
            _product.emit(Resource.Loading())

            if (checkInternet(context)) {
                try {

                    val response = repository.getProduct(product_id)

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(PRODUCT_TAG, "SUCCESS")
                            _product.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(PRODUCT_TAG, "ERROR ${response}}")
                        _product.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(PRODUCT_TAG, "EXCEPTION ${e.message}}}")
                    _product.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(PRODUCT_TAG, "$CONNECTION}")
                _product.emit(Resource.Connection())
            }
        }
    }

    fun searchProducts(searchBodyParameters: SearchBodyParameters) {
        viewModelScope.launch {

            if (searchBodyParameters.params.page==1)
                _search_products.emit(Resource.Loading())
            else
                _search_products.emit(Resource.LoadingWithProducts())

            if (checkInternet(context)) {
                try {
                    val response = repository.search(searchBodyParameters)

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(PRODUCT_TAG, "SUCCESS ${resultResponse.result?.products.size}}")
                            _search_products.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(PRODUCT_TAG, "ERROR ${response}}")

                        _search_products.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(PRODUCT_TAG, "EXCEPTION ${e.message}}}")
                    _search_products.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(PRODUCT_TAG, "$CONNECTION}")
                _search_products.emit(Resource.Connection())
            }
        }
    }


    fun searchCompareProducts(searchBodyParameters: SearchBodyParameters) {
        viewModelScope.launch {

            _search_compare_products.emit(Resource.Loading())

            if (checkInternet(context)) {
                try {
                    val response = repository.searchCompare(searchBodyParameters)

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(PRODUCT_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _search_compare_products.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(PRODUCT_TAG, "ERROR ${response}}")

                        _search_compare_products.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(PRODUCT_TAG, "EXCEPTION ${e.message}}}")
                    _search_compare_products.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(PRODUCT_TAG, "$CONNECTION}")
                _search_compare_products.emit(Resource.Connection())
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

    fun addToCart(
        update: Boolean,
        params: ProductBodyParameters,
        responseStandard: ResponseStandard?
    ) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                        var response: Response<RegisterResponse>
                        if (update)
                            response = repository.updateCartQuantity(params)
                        else
                            response = repository.addToCart(params)


                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(CART_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.item_added_successfully)
                                )
                                if (update)
                                    loadCart(true)
                            } else {
                                Log.d(CART_TAG, "$ERROR ${response.body()?.error}")
                                /*  responseStandard?.onFailure(
                                      false,
                                      ERROR,
                                      getLangResources().getString(R.string.error_msg)
                                  )*/

                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "${response.body()?.error}"
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(CART_TAG, "FAILUER ${response.body()}")
                            /*  responseStandard?.onFailure(
                                  false,
                                  ERROR,
                                  getLangResources().getString(R.string.error_msg)
                              )*/
                            responseStandard?.onFailure(
                                false,
                                "FAILURE",
                                "${response.body()?.error}"
                            )
                    }
                } catch (e: Exception) {
                    Log.d(CART_TAG, "EXCEPTION ${e.message.toString()}")
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
                Log.d(CART_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun loadCart(reloadWithoutSwipe: Boolean) {
        viewModelScope.launch {

            if (!reloadWithoutSwipe)
                _cart_items.emit(Resource.Loading())
            if (checkInternet(context)) {
                try {
                    var response = repository.viewCart()
                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(CART_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _cart_items.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(CART_TAG, "ERROR ${response}}")
                        _cart_items.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(CART_TAG, "EXCEPTION  " + e.message.toString())
                    _cart_items.emit(Resource.Error(ERROR))
                }
            } else {
                _cart_items.emit(Resource.Error(CONNECTION))
            }
        }
    }


    fun removeFromCart(
        is_vip_charge_product:Boolean,
        params: ProductBodyParameters,
        responseStandard: ResponseStandard?
    ) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                        var response = repository.removeFromCart(params)

                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(CART_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.item_deleted_successfully)
                                )
                                if (is_vip_charge_product)
                                    vip_subsription =false

                                loadCart(true)
                            } else {
                                Log.d(CART_TAG, "ERROR ${response.body()?.error}")
                                /*  responseStandard?.onFailure(
                                      false,
                                      ERROR,
                                      getLangResources().getString(R.string.error_msg)
                                  )*/

                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "${response.body()?.error}"
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(CART_TAG, "FAILUER ${response.body()?.error}")
                            /*  responseStandard?.onFailure(
                                  false,
                                  ERROR,
                                  getLangResources().getString(R.string.error_msg)
                              )*/
                            responseStandard?.onFailure(
                                false,
                                "FAILURE",
                                "${response.body()?.error}")

                        }

                } catch (e: Exception) {
                    Log.d(CART_TAG, "EXCEPTION ${e.message.toString()}")
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
                Log.d(CART_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun removeFromWishlist(
        params: ProductBodyParameters,
        responseStandard: ResponseStandard?
    ) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                        var response = repository.removeFromWishlist(params)

                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(CART_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.item_added_successfully)
                                )
                                viewWishlist(true)

                            } else {
                                Log.d(CART_TAG, "ERROR ${response.body()?.error}")
                                /*  responseStandard?.onFailure(
                                      false,
                                      ERROR,
                                      getLangResources().getString(R.string.error_msg)
                                  )*/

                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "${response.body()?.error}"
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(CART_TAG, "FAILUER ${response.body()}")
                            /*  responseStandard?.onFailure(
                                  false,
                                  ERROR,
                                  getLangResources().getString(R.string.error_msg)
                              )*/
                            responseStandard?.onFailure(
                                false,
                                "FAILURE",
                                "${response.body()?.error}"
                            )

                    }
                } catch (e: Exception) {
                    Log.d(CART_TAG, "EXCEPTION ${e.message.toString()}")
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
                Log.d(CART_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun addToWishlist(
        params: ProductBodyParameters,
        responseStandard: ResponseStandard?
    ) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                        var response = repository.addToWishlist(params)
                        Log.d(PRODUCT_TAG, params.params.product_id.toString())

                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(PRODUCT_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.item_added_successfully)
                                )
                            } else {
                                Log.d(PRODUCT_TAG, "$ERROR ${response.body()?.error}")
                                /*  responseStandard?.onFailure(
                                      false,
                                      ERROR,
                                      getLangResources().getString(R.string.error_msg)
                                  )*/

                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "${response.body()?.error}"
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(PRODUCT_TAG, "FAILURE ${response.body()}")
                            /*  responseStandard?.onFailure(
                                  false,
                                  ERROR,
                                  getLangResources().getString(R.string.error_msg)
                              )*/
                            responseStandard?.onFailure(
                                false,
                                "FAILURE",
                                "${response.body()?.error}"
                            )
                        }

                } catch (e: Exception) {
                    Log.d(PRODUCT_TAG, "EXCEPTION ${e.message.toString()}")
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
                Log.d(PRODUCT_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun viewWishlist(reloadWithoutSwipe: Boolean) {
        viewModelScope.launch {

            if (!reloadWithoutSwipe)
                _wishlist.emit(Resource.Loading())
            if (checkInternet(context)) {
                try {
                    var response = repository.viewWishlist()
                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(PRODUCT_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _wishlist.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(PRODUCT_TAG, "ERROR ${response}}")
                        _wishlist.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(PRODUCT_TAG, "EXCEPTION  " + e.message.toString())
                    _wishlist.emit(Resource.Error(ERROR))
                }
            } else {
                _wishlist.emit(Resource.Error(CONNECTION))
            }
        }
    }

    fun subscribeToVip(
        responseStandard: ResponseStandard?
    ) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                        var response = repository.subscribeToVip()

                        if (response.isSuccessful) {
                            if (response.body()?.result?.status != null) {
                                Log.d(CART_TAG, "Success")
                                responseStandard?.onSuccess(
                                    true,
                                    SUCCESS,
                                    getLangResources().getString(R.string.item_added_successfully)
                                )
                            } else {
                                Log.d(CART_TAG, "$ERROR ${response.body()}")
                                /*  responseStandard?.onFailure(
                                      false,
                                      ERROR,
                                      getLangResources().getString(R.string.error_msg)
                                  )*/

                                responseStandard?.onFailure(
                                    false,
                                    ERROR,
                                    "${response.body()?.error}"
                                )
                            }
                            //  getUser(session_id)
                        } else {
                            Log.d(CART_TAG, "FAILUER ${response.body()}")
                            /*  responseStandard?.onFailure(
                                  false,
                                  ERROR,
                                  getLangResources().getString(R.string.error_msg)
                              )*/
                            responseStandard?.onFailure(
                                false,
                                "FAILURE",
                                "${response.body()?.error}")

                        }

                } catch (e: Exception) {
                    Log.d(CART_TAG, "EXCEPTION ${e.message.toString()}")
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
                Log.d(CART_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

    fun getAllCategories() {
        viewModelScope.launch {

            _categories.emit(Resource.Loading())

            if (checkInternet(context)) {
                try {
                    val response = repository.getAllCategories()

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(CATEGORY_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _categories.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(CATEGORY_TAG, "ERROR ${response}}")

                        _categories.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(CATEGORY_TAG, "EXCEPTION ${e.message}}}")
                    _categories.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(CATEGORY_TAG, "$CONNECTION}")
                _categories.emit(Resource.Connection())
            }
        }
    }

    fun getAllBestSellingProducts() {
        viewModelScope.launch {

            _allBestSellingProducts.emit(Resource.Loading())

            if (checkInternet(context)) {
                try {
                    val response = repository.getAllBestSellingProducts()

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(BEST_SELLING_TAG, "SUCCESS ${resultResponse.result.size}}")
                            _allBestSellingProducts.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(BEST_SELLING_TAG, "ERROR ${response}}")

                        _allBestSellingProducts.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(BEST_SELLING_TAG, "EXCEPTION ${e.message}}}")
                    _allBestSellingProducts.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(BEST_SELLING_TAG, "$CONNECTION}")
                _allBestSellingProducts.emit(Resource.Connection())
            }
        }
    }

    fun getProductsByCategory(category_id: Int, paginationBodyParameters: PaginationBodyParameters) {
        viewModelScope.launch {

            if (paginationBodyParameters.params.page==1)
                _productsByCategory.emit(Resource.Loading())
            else
                _productsByCategory.emit(Resource.LoadingWithProducts())

            if (checkInternet(context)) {
                try {
                    val response = repository.getProductsByCategory(category_id, paginationBodyParameters)

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(PRODUCTS_BY_CATEGORY_TAG, "SUCCESS ${resultResponse.result?.products?.size}}")
                            _productsByCategory.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(PRODUCTS_BY_CATEGORY_TAG, "ERROR ${response}}")

                        _productsByCategory.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(PRODUCTS_BY_CATEGORY_TAG, "EXCEPTION ${e.message}}}")
                    _productsByCategory.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(PRODUCTS_BY_CATEGORY_TAG, "$CONNECTION}")
                _productsByCategory.emit(Resource.Connection())
            }
        }
    }

    fun getProductsBySubCategory(paginationProductBySubCategoryBodyParameters: PaginationProductBySubCategoryBodyParameters) {
        viewModelScope.launch {

            if (paginationProductBySubCategoryBodyParameters.params.page==1)
                _productsBySubCategory.emit(Resource.Loading())
            else
                _productsBySubCategory.emit(Resource.LoadingWithProducts())

            if (checkInternet(context)) {
                try {
                    val response = repository.getProductsBySubcategory(paginationProductBySubCategoryBodyParameters)

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(PRODUCTS_BY_SUB_CATEGORY_TAG, "SUCCESS ${resultResponse.result?.products?.size}}")
                            _productsBySubCategory.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(PRODUCTS_BY_SUB_CATEGORY_TAG, "ERROR ${response}}")
                        _productsBySubCategory.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(PRODUCTS_BY_SUB_CATEGORY_TAG, "EXCEPTION ${e.message}}}")
                    _productsBySubCategory.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(PRODUCTS_BY_CATEGORY_TAG, "$CONNECTION}")
                _productsBySubCategory.emit(Resource.Connection())
            }
        }
    }

    fun getAllBrands() {
        viewModelScope.launch {

            _brands.emit(Resource.Loading())

            if (checkInternet(context)) {
                try {
                    val response = repository.getAllBrands()

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            Log.d(BRAND_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _brands.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(BRAND_TAG, "ERROR ${response}}")

                        _brands.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(BRAND_TAG, "EXCEPTION ${e.message}}}")
                    _brands.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(BRAND_TAG, "$CONNECTION}")
                _brands.emit(Resource.Connection())
            }
        }
    }

    fun getHomeData() {
        viewModelScope.launch {

            _homeData.emit(Resource.Loading())

            if (checkInternet(context)) {
                try {
                    val response = repository.getHomeData()

                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            //Log.d(HOME_TAG, "SUCCESS ${resultResponse.result?.size}}")
                            _homeData.emit(Resource.Success(resultResponse))
                        }
                    } else {
                        Log.d(HOME_TAG, "ERROR ${response}}")

                        _homeData.emit(Resource.Error(response.message()))
                    }
                } catch (e: Exception) {
                    Log.d(HOME_TAG, "EXCEPTION ${e.message}}}")
                    _homeData.emit(Resource.Error(ERROR))
                }
            } else {
                Log.d(HOME_TAG, "$CONNECTION}")
                _homeData.emit(Resource.Connection())
            }
        }
    }

    fun createDeliveryOrder(
        params: OrderBodyParameters,
        responseStandard: ResponseStandard?
    ) {
        viewModelScope.launch {
            if (checkInternet(context)) {
                try {
                    var response = repository.createDeliveryOrder(params)

                    if (response.isSuccessful) {
                        if (response.body()?.result?.status != null) {
                            Log.d(CART_TAG, "Success")
                            responseStandard?.onSuccess(
                                true,
                                SUCCESS,
                                getLangResources().getString(R.string.order_created_successfully)
                            )

                        } else {
                            Log.d(CART_TAG, "ERROR ${response.body()?.error}")
                            /*  responseStandard?.onFailure(
                                  false,
                                  ERROR,
                                  getLangResources().getString(R.string.error_msg)
                              )*/

                            responseStandard?.onFailure(
                                false,
                                ERROR,
                                "${response.body()?.error}"
                            )
                        }
                        //  getUser(session_id)
                    } else {
                        Log.d(CART_TAG, "FAILUER ${response.body()?.error}")
                        /*  responseStandard?.onFailure(
                              false,
                              ERROR,
                              getLangResources().getString(R.string.error_msg)
                          )*/
                        responseStandard?.onFailure(
                            false,
                            "FAILURE",
                            "${response.body()?.error}")

                    }

                } catch (e: Exception) {
                    Log.d(CART_TAG, "EXCEPTION ${e.message.toString()}")
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
                Log.d(CART_TAG, "$CONNECTION}")
                responseStandard?.onFailure(false, CONNECTION, CONNECTION)
            }
        }
    }

}