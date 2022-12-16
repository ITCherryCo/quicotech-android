package com.quico.tech.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.quico.tech.data.Constant.EN
import com.quico.tech.data.Constant.SESSION_ID
import com.quico.tech.model.Data

class PrefManager(var context: Context) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor

    // shared pref mode
    var USER = "user"
    var PRIVATE_MODE = 0

    var isFirstTimeLaunch: Boolean
        get() = sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    companion object {
        // Shared  preferences file name
        private const val PREF_NAME = "my-intro-slider"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
        private const val LANGUAGE = "language"
    }

    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = sharedPreferences.edit()
    }

    var language: String?
        get() = sharedPreferences.getString(LANGUAGE, EN)
        set(new_language) {
            editor.putString(LANGUAGE, new_language)
            editor.apply()
        }

    var current_user:Data?
        get() {
            val gson = Gson()
            val json: String = sharedPreferences.getString(USER, null).toString()
            var user: Data? = null
            user = gson.fromJson(json, Data::class.java)
            return user
        }

    set(current_user) {
        val gson = Gson()
        val json: String = gson.toJson(current_user)
        editor.putString(USER, json)
        editor.apply()
    }

    var session_id:String?
    get() = sharedPreferences.getString(SESSION_ID, null)
    set(new_session_id) {
        editor.putString(SESSION_ID,new_session_id)
        editor.apply()
    }

}