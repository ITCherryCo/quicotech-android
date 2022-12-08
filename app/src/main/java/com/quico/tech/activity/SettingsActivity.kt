package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.quico.tech.R
import com.quico.tech.data.Constant.AR
import com.quico.tech.data.Constant.EN
import com.quico.tech.databinding.ActivitySettingsBinding
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            backArrow.setOnClickListener {
                onBackPressed()
            }
            logoutBtn.setOnClickListener {
                logoutAlert()
            }

            setUpText()
            checkLanguage()
        }
    }

    private fun setUpText(){
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.settings)
            languageText.text = viewModel.getLangResources().getString(R.string.language)
            englishRadioBtn.text = viewModel.getLangResources().getString(R.string.english)
            arabicRadioBtn.text = viewModel.getLangResources().getString(R.string.arabic)
            logoutText.text = viewModel.getLangResources().getString(R.string.logout)
            logoutBtn.text = viewModel.getLangResources().getString(R.string.logout)

            if (viewModel.getLanguage().equals(EN))
                englishRadioBtn.setChecked(true)
            else {
                arabicRadioBtn.setChecked(true)
                backArrow.scaleX = -1f
            }
        }
    }

    private fun checkLanguage(){
        binding.apply {
            englishRadioBtn.setOnClickListener {
                if (!viewModel.getLanguage().equals(EN)) {
                    viewModel.updateLanguage(EN)
                    startActivity(Intent(this@SettingsActivity, HomeTestActivity::class.java))
                }
            }

            arabicRadioBtn.setOnClickListener {
                if (!viewModel.getLanguage().equals(AR)) {
                    viewModel.updateLanguage(AR)
                    startActivity(Intent(this@SettingsActivity, HomeTestActivity::class.java))
                }
            }
        }
    }

    private fun logoutAlert() {
        Common.setUpAlert(
            this, false, "", viewModel.getLangResources().getString(R.string.sure_logout),
            viewModel.getLangResources().getString(R.string.yes),
            object : Common.ResponseConfirm{
                override fun onConfirm() {

                }
            }
        )
    }
}