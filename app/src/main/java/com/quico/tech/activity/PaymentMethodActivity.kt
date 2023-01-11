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
import com.quico.tech.adapter.CardSelectionRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityPaymentMethodBinding
import com.quico.tech.model.Card
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentMethodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentMethodBinding
    private lateinit var cardSelectionRecyclerViewAdapter: CardSelectionRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLoading()
        initStatusBar()
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

    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }
    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.payment_method)
            selectPaymentText.text = viewModel.getLangResources().getString(R.string.select_payment_method)
            cashOnDeliveryText.text = viewModel.getLangResources().getString(R.string.cash_on_delivery)
            addNewCardText.text = viewModel.getLangResources().getString(R.string.add_new_card)
            nextBtn.text = viewModel.getLangResources().getString(R.string.next)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun setUpCardAdapter() {

        binding.apply {
            stopShimmer()
            recyclerView.visibility = View.VISIBLE

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

    private fun stopShimmer() {
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    fun setLoading() {
        binding.apply {
            recyclerView.visibility = View.GONE
            paymentErrorContainer.root.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()

            lifecycleScope.launch {
                delay(3000)
                setUpCardAdapter()
            }
        }
    }

    fun onRefresh() {
        binding.apply {
                setLoading() // later we will remove it because the observable will call it
                // viewModel.Cards(1, orders_type) // get User id
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {

            recyclerView.visibility = View.GONE
            stopShimmer()

            paymentErrorContainer.apply {
                root.visibility = View.VISIBLE
                errorImage.setImageResource(android.R.color.transparent)
                errorMsg1.visibility = View.GONE

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.check_connection)
                        )
                    }
                    Constant.NO_CARDS -> {
                        errorMsg2.text = viewModel.getLangResources().getString(R.string.no_cards)
                        errorImage.setImageResource(R.drawable.empty_item)
                    }

                    Constant.ERROR -> {
                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.error_msg)
                        )
                    }
                }
            }
        }
    }
}