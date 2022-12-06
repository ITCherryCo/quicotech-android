package com.quico.tech.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quico.tech.data.Constant.ERROR
import com.quico.tech.data.PrefManager
import com.quico.tech.model.AddressResponse
import com.quico.tech.repository.Repository
import com.quico.tech.utils.LocalHelper
import com.quico.tech.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application)  {
    private lateinit var prefManager: PrefManager
    private var set_language = "fr"
    lateinit var resources: Resources
    private val localeHelper: LocalHelper = LocalHelper()
    private lateinit var context: Context
    private val repository = Repository()

    private val _addresses: MutableStateFlow<Resource<AddressResponse>> =
        MutableStateFlow(Resource.Nothing())
    val addresses: StateFlow<Resource<AddressResponse>> get() = _addresses

    init {
        context = getApplication<Application>().applicationContext
        prefManager = PrefManager(context)
    }

    fun getStoreId(): Int {
        if (getLanguage().equals("en"))
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
        prefManager.language=new_language
    }

    fun getAddresses(
        customer_id: Int,
    ) {
        viewModelScope.launch {
                _addresses.emit(Resource.Loading())
            if (checkInternet()) {
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

    fun checkInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        return if (isConnected) {
            true
        } else {
            false
        }
    }


}