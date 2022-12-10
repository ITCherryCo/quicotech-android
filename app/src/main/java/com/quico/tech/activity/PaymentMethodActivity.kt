package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.CardSelectionRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityPaymentMethodBinding
import com.quico.tech.model.Card
import com.quico.tech.viewmodel.SharedViewModel

class PaymentMethodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentMethodBinding
    private lateinit var cardSelectionRecyclerViewAdapter: CardSelectionRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpCardAdapter()
        setUpText()
        binding.apply {
            nextBtn.setOnClickListener {
                startActivity(Intent(this@PaymentMethodActivity, AddressListActivity::class.java))
            }

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }


    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.payment_method)
            selectPaymentText.text = viewModel.getLangResources().getString(R.string.select_payment_method)
            cashOnDeliveryText.text = viewModel.getLangResources().getString(R.string.cash_with_delivery)
            addNewCardText.text = viewModel.getLangResources().getString(R.string.add_new_card)
            nextBtn.text = viewModel.getLangResources().getString(R.string.next)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun setUpCardAdapter() {

        binding.apply {

            cardSelectionRecyclerViewAdapter = CardSelectionRecyclerViewAdapter()
            var cards = ArrayList<Card>()
            cards.add(Card(1))
            cards.add(Card(1))
            cards.add(Card(1))
            cards.add(Card(1))

            recyclerView.layoutManager =
                LinearLayoutManager(this@PaymentMethodActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(cardSelectionRecyclerViewAdapter)

            cardSelectionRecyclerViewAdapter.differ.submitList(cards)
        }
    }
}