package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
            if (viewModel.current_session_id.isNullOrEmpty())
                logoutContainer.visibility = View.GONE

            viewModel.current_session_id?.let {
                logoutContainer.visibility = View.VISIBLE
                logoutBtn.setOnClickListener {
                    logoutAlert()
                }
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
                    startActivity(Intent(this@SettingsActivity, HomeActivity::class.java))
                }
            }

            arabicRadioBtn.setOnClickListener {
                if (!viewModel.getLanguage().equals(AR)) {
                    viewModel.updateLanguage(AR)
                    startActivity(Intent(this@SettingsActivity, HomeActivity::class.java))
                }
            }
        }
    }

    private fun logoutAlert() {
        Common.setUpAlert(
            this, false, viewModel.getLangResources().getString(R.string.logout), viewModel.getLangResources().getString(R.string.sure_logout),
            viewModel.getLangResources().getString(R.string.yes),
            object : Common.ResponseConfirm{
                override fun onConfirm() {
                    viewModel.current_session_id?.let { session_id->

                        Common.setUpProgressDialog(this@SettingsActivity)
                        viewModel.logout(
                            session_id,
                            object : SharedViewModel.ResponseStandard {
                                override fun onSuccess(
                                    success: Boolean,
                                    resultTitle: String,
                                    message: String
                                ) {
                                    Common.cancelProgressDialog()
                                    viewModel.getLangResources().getString(R.string.logged_out)
                                    startActivity(
                                        Intent(this@SettingsActivity, LoginActivity::class.java)
                                    )
                                }

                                override fun onFailure(
                                    success: Boolean,
                                    resultTitle: String,
                                    message: String
                                ) {
                                    Common.cancelProgressDialog()

                                    Common.setUpAlert(
                                        this@SettingsActivity, false,
                                        viewModel.getLangResources().getString(R.string.error),
                                        viewModel.getLangResources().getString(R.string.error_msg),
                                        viewModel.getLangResources().getString(R.string.ok),
                                        null
                                    )
                                }
                            })
                    }
                }
            }
        )
    }
}