package com.quico.tech.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.EMAIL
import com.quico.tech.data.Constant.OPERATION_TYPE
import com.quico.tech.data.Constant.PHONE_NUMBER
import com.quico.tech.databinding.ActivityVerificationCodeBinding
import com.quico.tech.viewmodel.SharedViewModel

class VerificationCodeActivity : AppCompatActivity() {
    private lateinit var operation_type: String
    private lateinit var binding: ActivityVerificationCodeBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificationCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        operation_type = intent.extras?.getString(OPERATION_TYPE)!!
        checkOperationType()
        setUpText()
    }

    // to check wether i want to send an sms verification code or an email confirmation
    private fun checkOperationType(){
        binding.apply {
            val content = SpannableString("00:00")
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            timer.setText(content)

            title.text = viewModel.getLangResources().getString(R.string.verification)
            verifyBtn.text = viewModel.getLangResources().getString(R.string.verify)
            enterVerificationCodeText.text = viewModel.getLangResources().getString(R.string.enter_verification_code)

            when (operation_type){
                EMAIL -> {
                    sendMsgText.text = viewModel.getLangResources().getString(R.string.email_confirmation_code)
                    orText.visibility = View.GONE
                    callText.visibility = View.GONE
                }
                PHONE_NUMBER ->{
                    sendMsgText.text = viewModel.getLangResources().getString(R.string.phone_confirmation_code)
                    orText.text = viewModel.getLangResources().getString(R.string.or)
                    sendMsgText.text = viewModel.getLangResources().getString(R.string.email_confirmation_code)
                }
            }

            verifyBtn.setOnClickListener {
                startActivity(Intent(this@VerificationCodeActivity, CheckoutActivity::class.java))
            }
        }
    }

    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.profile)
            enterVerificationCodeText.text = viewModel.getLangResources().getString(R.string.enter_verification_code)
            sendMsgText.text = viewModel.getLangResources().getString(R.string.email_verification_example)
            resendText.text = viewModel.getLangResources().getString(R.string.resend_code)
            orText.text = viewModel.getLangResources().getString(R.string.or)
            callText.text = viewModel.getLangResources().getString(R.string.call)
            verifyBtn.text = viewModel.getLangResources().getString(R.string.verify)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }
}