package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.CardRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCardListBinding
import com.quico.tech.model.Card
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardListBinding
    private lateinit var cardRecyclerViewAdapter: CardRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setUpCardAdapter()
        onRefresh()
        setUpText()
        binding.apply {

            addCardContainer.setOnClickListener {
                startActivity(Intent(this@CardListActivity, NewCardActivity::class.java))
            }

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setUpText(){
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.my_cards)
            safeInfo.text = viewModel.getLangResources().getString(R.string.safe_information)
            addNewCardText.text = viewModel.getLangResources().getString(R.string.add_new_card)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

   private fun setUpCardAdapter() {
        binding.apply {
            cardRecyclerViewAdapter = CardRecyclerViewAdapter()
            stopShimmer()
            recyclerView.visibility = View.VISIBLE

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


   private fun stopShimmer(){
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    private fun setLoading() {
        binding.apply {
            ErrorContainer.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            recyclerView.visibility = View.GONE

            lifecycleScope.launch {
                delay(3000)
                setUpCardAdapter()
            }
        }
    }

    private  fun onRefresh() {
        setLoading()
        //viewModel.getAddresses(1)
    }

    private fun setUpErrorForm(error_type: String) {
        binding.apply {

            stopShimmer()
            ErrorContainer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            when(error_type){
                Constant.CONNECTION ->errorText.setText(viewModel.getLangResources().getString(R.string.check_connection))
                Constant.NO_CARDS ->errorText.setText(viewModel.getLangResources().getString(R.string.no_cards))
                Constant.ERROR ->errorText.setText(viewModel.getLangResources().getString(R.string.error_msg))
            }
        }
    }
}