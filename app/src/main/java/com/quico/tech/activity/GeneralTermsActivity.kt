package com.quico.tech.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.TERMS_OF_USE
import com.quico.tech.data.Constant.VIP_BENEFITS
import com.quico.tech.databinding.ActivityGeneralTermsBinding
import com.quico.tech.databinding.TermsAlertDialogBinding
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class GeneralTermsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGeneralTermsBinding
    private val viewModel: SharedViewModel by viewModels()

    private var activity_type:String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneralTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity_type = intent.extras?.getString(Constant.ACTIVITY_TYPE)!!

        binding.apply {
            backArrow.setOnClickListener {
                onBackPressed()
            }
            title.text = viewModel.getLangResources().getString(R.string.terms_and_conditions)
            //webView.setBackgroundColor(Color.parseColor("#EFEFEF"))
        }
        initStatusBar()
        setUpText()

        // onRefresh()
        //  monitorInfo()
        // viewModel.generalWebInfo()
    }

    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun setUpText() {
        binding.apply {

            when(activity_type){
                VIP_BENEFITS->{
                    title.text = viewModel.getLangResources().getString(R.string.vip_benefits)
                    infoTitle.text = viewModel.getLangResources().getString(R.string.vip_benefits)
                    description.text = viewModel.getLangResources().getString(R.string.vip_benefits_description)
                    subscribeBtn.visibility = View.VISIBLE
                    subscribeBtn.setOnClickListener {
                        setSubscribeDialog()
                    }
                }

                TERMS_OF_USE->{
                    title.text = viewModel.getLangResources().getString(R.string.terms_and_conditions)
                    infoTitle.text = viewModel.getLangResources().getString(R.string.terms_and_conditions)
                    description.text = viewModel.getLangResources().getString(R.string.terms_description)
                    subscribeBtn.visibility = View.GONE
                }
            }
            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    private fun setSubscribeDialog(){

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.terms_alert_dialog)
            val binding: TermsAlertDialogBinding =
                TermsAlertDialogBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(binding.getRoot())
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.apply {

                termsCheckBox.text = viewModel.getLangResources().getString(R.string.terms_and_conditions)
                acceptBtn.text = viewModel.getLangResources().getString(R.string.accept)
                rejectBtn.text = viewModel.getLangResources().getString(R.string.reject)

                dialog.show()
                acceptBtn.setOnClickListener {
                    if (termsCheckBox.isChecked)
                        dialog.dismiss()
                }

                rejectBtn.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }

    }
