package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.CardRecyclerViewAdapter
import com.quico.tech.adapter.CardSelectionRecyclerViewAdapter
import com.quico.tech.databinding.ActivityCartBinding
import com.quico.tech.databinding.ActivityPaymentMethodBinding
import com.quico.tech.model.Card

class PaymentMethodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentMethodBinding
    private lateinit var cardSelectionRecyclerViewAdapter: CardSelectionRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpCardAdapter()

        binding.apply {
            nextBtn.setOnClickListener {
                startActivity(Intent(this@PaymentMethodActivity, AddressListActivity::class.java))
            }

            backArrow.setOnClickListener {
                onBackPressed()
            }
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