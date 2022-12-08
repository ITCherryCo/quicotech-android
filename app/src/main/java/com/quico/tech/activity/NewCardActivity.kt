package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCardListBinding
import com.quico.tech.databinding.ActivityEditProfileBinding
import com.quico.tech.databinding.ActivityNewCardBinding
import com.quico.tech.viewmodel.SharedViewModel

class NewCardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewCardBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        setUpText()
    }


    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.new_card)
            cardNumberField.hint = viewModel.getLangResources().getString(R.string.card_number)
            cardNameField.hint = viewModel.getLangResources().getString(R.string.name_on_card)
            expirationField.hint = viewModel.getLangResources().getString(R.string.card_expiration)
            cvvEditField.hint = viewModel.getLangResources().getString(R.string.cvv)
            saveBtn.text = viewModel.getLangResources().getString(R.string.save)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }
}