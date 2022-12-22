package com.quico.tech.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.CHANGE_PHONE_NUMBER
import com.quico.tech.data.Constant.CHECKOUT_TYPE
import com.quico.tech.data.Constant.EMAIL
import com.quico.tech.data.Constant.OPERATION_TYPE
import com.quico.tech.data.Constant.VERIFICATION_TYPE
import com.quico.tech.data.Constant.ORDERS
import com.quico.tech.data.Constant.PHONE_NUMBER
import com.quico.tech.data.Constant.REGISTER
import com.quico.tech.data.Constant.TRACKING_ON
import com.quico.tech.databinding.ActivityVerificationCodeBinding
import com.quico.tech.utils.Common
import com.quico.tech.utils.Common.cancelProgressDialog
import com.quico.tech.utils.Common.isProgressIsLoading
import com.quico.tech.utils.Common.setUpProgressDialog
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class VerificationCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationCodeBinding
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var verification_type: String
    private var operation_type: String? = null
    private var phone_number: String? = null
    private var code_by_system: String? = ""
    private var sms_code: String? = ""
    private var firebaseAuth: FirebaseAuth? = null
    private val SMS_TAG = "SMS_CODE"
    var countDownTimer: CountDownTimer? = null
    private val mTimeLeftInMillis: Long = 45000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificationCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verification_type = intent.extras?.getString(VERIFICATION_TYPE)!!
        operation_type = intent.extras?.getString(OPERATION_TYPE)
        phone_number = intent.extras?.getString(PHONE_NUMBER)
        firebaseAuth = FirebaseAuth.getInstance()
        checkOperationType()
        setUpText()
        viewModel.canRegister = false
        Constant.can_register = false
    }


    // to check wether i want to send an sms verification code or an email confirmation
    private fun checkOperationType() {
        binding.apply {
            val content = SpannableString("00:00")
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            timer.text = content

            title.text = viewModel.getLangResources().getString(R.string.verification)
            verifyBtn.text = viewModel.getLangResources().getString(R.string.verify)
            enterVerificationCodeText.text =
                viewModel.getLangResources().getString(R.string.enter_verification_code)

            when (verification_type) {
                EMAIL -> {
                    sendMsgText.text =
                        viewModel.getLangResources().getString(R.string.email_confirmation_code)

                    verifyBtn.setOnClickListener {
                        startActivity(
                            Intent(this@VerificationCodeActivity, CheckoutActivity::class.java)
                                .putExtra(TRACKING_ON, false)
                                .putExtra(CHECKOUT_TYPE, ORDERS)
                        )
                    }
                    resendText.setOnClickListener {
                        // MUST RESEND AN EMAIL
                    }
                }
                PHONE_NUMBER -> {
                    sendMsgText.text =
                        viewModel.getLangResources().getString(R.string.phone_confirmation_code)
                    phone_number?.let {
                        setUpCode("+961", it)
                    }
                    verifyBtn.setOnClickListener {
                        check_code()
                    }

                    resendText.setOnClickListener {
                        setUpCode(
                            "+961",
                            phone_number!!
                        )
                    }
                }
            }
        }
    }

    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.profile)
            enterVerificationCodeText.text =
                viewModel.getLangResources().getString(R.string.enter_verification_code)
            resendText.text = viewModel.getLangResources().getString(R.string.resend_code)
            verifyBtn.text = viewModel.getLangResources().getString(R.string.verify)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    private val mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, p1: PhoneAuthProvider.ForceResendingToken) {
                try {
                    super.onCodeSent(s, p1)
                    code_by_system = s
                    Log.d(SMS_TAG, "msg is sent: $s")
                } catch (e: Exception) {
                    Log.d(SMS_TAG, "ERROR_ON_VERIFICATION ${e.message.toString()}")
                }
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                try {
                    sms_code = phoneAuthCredential.smsCode
                    Log.d(SMS_TAG, "onVerificationCompleted: $sms_code")
                    if (sms_code != null) {
                        //  finish_everything(code);
                        Log.d(SMS_TAG, "onVerificationCompleted: $sms_code")
                        binding.pinview.setText(sms_code)
                        check_code()
                    } else
                        Log.d(SMS_TAG, "null")

                } catch (e: Exception) {
                    Log.d(SMS_TAG, "ERROR_ON_VERIFICATION ${e.message.toString()}")
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                try {
                    Log.d(SMS_TAG, "SMS_CODE_ERROR ${e.message.toString()} $sms_code")
                    //dialogs.setUpErrorDialog(resources.getString(R.string.error), e.getMessage());
                    Common.setUpAlert(
                        this@VerificationCodeActivity, false,
                        viewModel.getLangResources().getString(R.string.error),
                        viewModel.getLangResources().getString(R.string.error_msg),
                        viewModel.getLangResources().getString(R.string.ok),
                        null
                    )
                } catch (e: Exception) {
                    Log.d(SMS_TAG, "ERROR_FAIL_SEND_CODE ${e.message.toString()}")
                }
            }
        }

    fun setUpCode(country_code: String, phone_number: String) {
        if (Common.checkInternet(this)) {
            try {
                startTimer()
                send_code_to_user(country_code, phone_number)
                Log.d(SMS_TAG, "call send")

            } catch (e: Exception) {
                Log.d(SMS_TAG, "ERROR_SEND_CODE ${e.message.toString()}")
            }

        } else {
            Common.setUpAlert(
                this@VerificationCodeActivity, false,
                viewModel.getLangResources().getString(R.string.connection),
                viewModel.getLangResources().getString(R.string.check_connection),
                viewModel.getLangResources().getString(R.string.ok),
                null
            )
        }
    }

    fun startTimer() {
        binding.apply {
            timer.setEnabled(false)
            resendText.setEnabled(false)
            resendText.setTextColor(resources.getColor(R.color.gray_dark))
            countDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val timeLeftFormatted = String.format(
                        Locale.ENGLISH, "%02d",
                        millisUntilFinished / 1000
                    )
                    timer.text = "0:$timeLeftFormatted"
                }

                @SuppressLint("ResourceType")
                override fun onFinish() {
                    timer.text = "0:00"
                    resendText.setEnabled(true)
                    resendText.setTextColor(resources.getColor(R.drawable.ripple_text_purple_gray))
                    countDownTimer!!.cancel()
                }
            }.start()
        }
    }

    private fun send_code_to_user(country_code: String, phone_number: String) {
        try {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth!!)
                .setPhoneNumber("$country_code$phone_number") // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                // .setActivity((Activity) TaskExecutors.MAIN_THREAD)                 // Activity (for callback binding)
                .setCallbacks(mCallback) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            Log.d(SMS_TAG, "try to send $country_code$phone_number")

        } catch (e: Exception) {
            Log.d(SMS_TAG, "ERROR_SEND_CODE_2 ${e.message.toString()}")
        }
    }

    private fun check_code() {
        binding.apply {
            val user_entered_otp: String = binding.pinview.getText().toString()
            if (user_entered_otp.isEmpty() || user_entered_otp.length < 6) {
                Toast.makeText(
                    this@VerificationCodeActivity,
                    viewModel.getLangResources().getString(R.string.invalid_otp),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val phoneAuthCredential =
                    PhoneAuthProvider.getCredential(code_by_system!!, user_entered_otp)
                val second_sms_code = phoneAuthCredential.smsCode
                if (user_entered_otp == sms_code || user_entered_otp == second_sms_code) {

                    verifyBtn.setEnabled(false)
                    if (Common.checkInternet(this@VerificationCodeActivity)) {
                        if (!isProgressIsLoading()!!)
                            setUpProgressDialog(this@VerificationCodeActivity)
                        signInWithPhoneAuthCredential(phoneAuthCredential)

                    } else {
                        cancelProgressDialog()
                        verifyBtn.setEnabled(true)
                        Common.setUpAlert(
                            this@VerificationCodeActivity, false,
                            viewModel.getLangResources().getString(R.string.connection),
                            viewModel.getLangResources().getString(R.string.check_connection),
                            viewModel.getLangResources().getString(R.string.ok),
                            null
                        )
                    }
                } else {
                    verifyBtn.setEnabled(true)
                    cancelProgressDialog()
                    Toast.makeText(
                        this@VerificationCodeActivity,
                        viewModel.getLangResources().getString(R.string.wrong_otp),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult> { task ->
                    cancelProgressDialog()
                    if (task.isSuccessful) {
                        Log.d(SMS_TAG, "signInWithCredential:success")
                        val user = task.result.user

                        operation_type?.let { operation_type ->

                            when (operation_type) {
                                REGISTER -> {
                                    viewModel.canRegister = true
                                    Constant.can_register = true
                                    onBackPressed()
                                }
                                CHANGE_PHONE_NUMBER -> Log.d(
                                    SMS_TAG,
                                    "call change number api"
                                )
                                else -> {}
                            }
                        }
                    } else {
                        cancelProgressDialog()
                        Common.setUpAlert(
                            this@VerificationCodeActivity, false,
                            viewModel.getLangResources().getString(R.string.error),
                            viewModel.getLangResources().getString(R.string.error_msg),
                            viewModel.getLangResources().getString(R.string.ok),
                            null
                        )
                        Log.w(SMS_TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        }
                    }
                })
    }
}