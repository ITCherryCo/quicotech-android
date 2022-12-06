package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.adapter.CardRecyclerViewAdapter
import com.quico.tech.databinding.ActivityCardListBinding
import com.quico.tech.model.Card

class CardListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardListBinding
    private lateinit var cardRecyclerViewAdapter: CardRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpCardAdapter()
        binding.apply {

            addCardContainer.setOnClickListener {
                startActivity(Intent(this@CardListActivity, NewCardActivity::class.java))
            }

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }

    fun setUpCardAdapter() {

        binding.apply {

            cardRecyclerViewAdapter = CardRecyclerViewAdapter()
            var cards = ArrayList<Card>()
            cards.add(Card(1))
            cards.add(Card(1))
            cards.add(Card(1))
            cards.add(Card(1))

            recyclerView.layoutManager =
                LinearLayoutManager(this@CardListActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(cardRecyclerViewAdapter)

            cardRecyclerViewAdapter.differ.submitList(cards)
        }
    }
}