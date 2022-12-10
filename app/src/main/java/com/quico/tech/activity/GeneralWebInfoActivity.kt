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
        setUpText()

       // onRefresh()
      //  monitorInfo()
       // viewModel.generalWebInfo()
    }


    private fun setUpText(){
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.terms_and_conditions)
            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun monitorInfo() {

        lifecycleScope.launch {
        viewModel.general_web_info.collect{ response->
            when (response) {
                is Resource.Success -> {
                    binding.swipeRefreshLayout.setRefreshing(false)
                    binding.swipeRefreshLayout.setEnabled(false)
                    binding.errorRel.setVisibility(View.GONE)
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
                        setErrorRel(false)
                    }
                }

                is Resource.Connection -> {
                    Log.d("WEB_RESPONSE", "ERROR ")
                    setErrorRel(true)
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
            webView.setVisibility(View.GONE)
            errorRel.setVisibility(View.GONE)
            swipeRefreshLayout.setRefreshing(true)
        }
    }


    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                webView.setVisibility(View.GONE)
                errorRel.setVisibility(View.GONE)
                viewModel.generalWebInfo()
            })
        }
    }

    fun setErrorRel(connection: Boolean) {

        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            webView.setVisibility(View.GONE)
            errorRel.setVisibility(View.VISIBLE)
            errorImage.setImageResource(android.R.color.transparent)

            when(connection){
                true-> {
                    //errorImage.setImageResource(R.drawable.connection)
                    errorText.setText(
                        viewModel.getLangResources().getString(R.string.check_connection)
                    )
                }
                else -> {
                    //errorImage.setImageResource(R.drawable.connection)
                    errorText.setText(
                        viewModel.getLangResources().getString(R.string.error_msg)
                    )
                }
            }
            swipeRefreshLayout.setEnabled(true)
        }
    }
}