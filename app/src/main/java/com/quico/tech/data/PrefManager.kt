package com.quico.tech.data

import android.content.Context
import android.content.SharedPreferences
import com.quico.tech.data.Constant.EN
import com.quico.tech.data.PrefManager

/**
 * Created by Darush on 9/2/2016.
 */
class PrefManager(var context: Context) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor

    // shared pref mode
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


}