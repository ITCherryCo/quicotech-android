package com.quico.tech.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.AR
import com.quico.tech.data.Constant.ITEM_ID
import com.quico.tech.databinding.ActivityCompareProductBinding
import com.quico.tech.fragment.CompareProductFragment
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class CompareProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompareProductBinding
    private val viewModel: SharedViewModel by viewModels()
    private var item_id_1:Int=0
    private var item_id_2:Int=2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompareProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        item_id_1 = intent.extras!!.getInt(ITEM_ID)

        setUpText()
        initStatusBar()
        loadFragments()
    }


    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
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

    private fun loadFragments(){
        var fragment = CompareProductFragment()
        var bundle1 = Bundle()
        bundle1.putInt(ITEM_ID, item_id_1)
        fragment.arguments= bundle1
        fragment?.let { loadFragment1(it) }

        var fragment2 = CompareProductFragment()
        var bundle2 = Bundle()
        bundle2.putInt(ITEM_ID, item_id_2)
        fragment2.arguments= bundle2
        fragment2?.let { loadFragment2(it) }
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
