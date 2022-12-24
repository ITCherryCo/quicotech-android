package com.quico.tech.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.quico.tech.R
import com.quico.tech.data.Constant.AR
import com.quico.tech.data.Constant.ITEM_ID
import com.quico.tech.databinding.ActivityCompareProductBinding
import com.quico.tech.fragment.CompareProductFragment
import com.quico.tech.viewmodel.SharedViewModel

class CompareProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompareProductBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompareProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var fragment = CompareProductFragment()
        var bundle1 = Bundle()
        bundle1.putInt(ITEM_ID, 1)
        fragment.arguments= bundle1
        fragment?.let { loadFragment1(it) }

        var fragment2 = CompareProductFragment()
        var bundle2 = Bundle()
        bundle2.putInt(ITEM_ID, 2)
        fragment2.arguments= bundle2
        fragment2?.let { loadFragment2(it) }
        setUpText()
    }
    private fun setUpText() {
        binding.apply {

            title.text = viewModel.getLangResources().getString(R.string.filters)

            backArrow.setOnClickListener {
                onBackPressed()
            }
            if (viewModel.getLanguage().equals(AR))
                backArrow.scaleX=-1f
        }
    }

    private fun loadFragment1(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.CompareFrame1, fragment)
       // transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    private fun loadFragment2(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.CompareFrame2, fragment)
      //  transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

}
