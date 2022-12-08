package com.quico.tech.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityEditProfileBinding
import com.quico.tech.viewmodel.SharedViewModel
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpText()
        binding.apply {
            birthdayField.setOnClickListener {
                chooseDate()
            }
            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }

    fun chooseDate() {

        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        val dialog = DatePickerDialog(
            this,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            mDateSetListener,
            year, month, day
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    var mDateSetListener =
        OnDateSetListener { datePicker, year, month, day ->
            var month = month
            month = month + 1
            val date = "$month/$day/$year"
            binding.birthdayField.setText(date)
        }


    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.profile)
            fullNameText.text = viewModel.getLangResources().getString(R.string.full_name)
            nameField.hint = viewModel.getLangResources().getString(R.string.full_name)
            mobileNumberText.text = viewModel.getLangResources().getString(R.string.mobile_number)
            phoneField.hint = viewModel.getLangResources().getString(R.string.mobile_number)
            birthdateText.text = viewModel.getLangResources().getString(R.string.date_of_birth)
            birthdayField.hint = viewModel.getLangResources().getString(R.string.birthday_example)
            emailText.text = viewModel.getLangResources().getString(R.string.email)
            emailField.hint = viewModel.getLangResources().getString(R.string.email)
            saveBtn.text = viewModel.getLangResources().getString(R.string.save)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }
}