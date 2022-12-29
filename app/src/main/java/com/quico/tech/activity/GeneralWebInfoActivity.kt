package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityGeneralWebInfoBinding
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class GeneralWebInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGeneralWebInfoBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneralWebInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            backArrow.setOnClickListener {
                onBackPressed()
            }
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = WebViewClient()
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
            title.text = viewModel.getLangResources().getString(R.string.terms_and_conditions)
            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun monitorInfo() {

        lifecycleScope.launch {
            viewModel.general_web_info.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.swipeRefreshLayout.setRefreshing(false)
                        binding.swipeRefreshLayout.setEnabled(false)
                        binding.webErrorContainer.root.visibility=View.GONE
                        binding.webView.setVisibility(View.VISIBLE)

                        response.data?.let { webResponse ->

                            binding.apply {
                                webView.setVisibility(View.VISIBLE)
                                webView.loadData(webResponse.data, "text/html", "UTF-8")
                                webView.setWebViewClient(object : WebViewClient() {
                                    override fun onPageFinished(view: WebView, url: String) {
                                        swipeRefreshLayout.setRefreshing(false)
                                        swipeRefreshLayout.setEnabled(false)
                                    }
                                })
                            }
                        }
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.d("WEB_RESPONSE", "ERROR " + message)
                            setUpErrorForm(false)
                        }
                    }

                    is Resource.Connection -> {
                        Log.d("WEB_RESPONSE", "ERROR ")
                        setUpErrorForm(true)
                    }

                    is Resource.Loading -> {
                        setLoading()
                        Log.d("WEB_RESPONSE", "LOADING")
                    }
                }
            }
        }
    }


    fun setLoading() {
        binding.apply {
            webView.visibility = View.GONE
            webErrorContainer.root.visibility = View.GONE
            swipeRefreshLayout.setRefreshing(true)
        }
    }


    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                webView.visibility = View.GONE
                webErrorContainer.root.visibility = View.GONE
                viewModel.generalWebInfo()
            })
        }
    }



    fun setUpErrorForm(connection: Boolean) {

        binding.apply {
            webErrorContainer.apply {
                root.visibility = View.VISIBLE
                tryAgain.visibility = View.GONE
                errorImage.visibility = View.GONE
                errorBtn.visibility = View.GONE
                when (connection) {

                    true -> {
                        errorMsg1.setText(
                            viewModel.getLangResources().getString(R.string.connection)
                        )

                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.check_connection)
                        )
                    }
                    else -> {
                        //errorImage.setImageResource(R.drawable.connection)
                        errorMsg1.setText(
                            viewModel.getLangResources().getString(R.string.error)
                        )

                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.error_msg)
                        )
                    }
                }
                swipeRefreshLayout.setEnabled(true)
            }
        }
    }
}