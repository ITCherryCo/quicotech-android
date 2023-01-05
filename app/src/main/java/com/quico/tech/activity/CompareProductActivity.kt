package com.quico.tech.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.AR
import com.quico.tech.data.Constant.FRAGMENT_POSITION
import com.quico.tech.data.Constant.ITEM_ID
import com.quico.tech.databinding.ActivityCompareProductBinding
import com.quico.tech.fragment.CompareProductFragment
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel
import com.quico.tech.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch

class CompareProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompareProductBinding
    private val viewModel: SharedViewModel by viewModels()
    private var item_id:Int=0
    private var item_id_2222:Int=2
    private lateinit var viewModel2: SharedViewModel

    companion object{
         val _item_id_1: MutableStateFlow<Int> = MutableStateFlow(0)
        val item_id_1: StateFlow<Int> get() = _item_id_1

        val _item_id_2: MutableStateFlow<Int> = MutableStateFlow(0)
        val item_id_2: StateFlow<Int> get() = _item_id_2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompareProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        item_id = intent.extras!!.getInt(ITEM_ID)
        lifecycleScope.launch {
            _item_id_1.emit(item_id)
            _item_id_2.emit(0)
        }

      //  val factory = ViewModelFactory()
       // viewModel2 = ViewModelProvider(this, factory).get(SharedViewModel::class.java)

        setUpText()
        initStatusBar()
        observeItemsIDs()
        //loadFragments()
    }

    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }
    private fun setUpText() {
        binding.apply {

            title.text = viewModel.getLangResources().getString(R.string.compare_product)

            backArrow.setOnClickListener {
                onBackPressed()
            }
            if (viewModel.getLanguage().equals(AR))
                backArrow.scaleX=-1f
        }
    }

    private fun observeItemsIDs(){
        lifecycleScope.launch {
            item_id_1.collect { item_id_1 ->
                Log.d("FRAGMENT_ITEM_ID", "item_id_1: $item_id_1")
                 setUpFragment1(item_id_1)
            }
        }

        lifecycleScope.launch {
            item_id_2.collect { item_id_2 ->
                Log.d("FRAGMENT_ITEM_ID", "item_id_1: ${Companion.item_id_2}")
                 setUpFragment2(item_id_2)
            }
        }
    }

    private fun setUpFragment1(item_id_1:Int){
        var fragment = CompareProductFragment()
        var bundle1 = Bundle()
        bundle1.putInt(ITEM_ID, item_id_1)
        bundle1.putInt(FRAGMENT_POSITION, 1)
        fragment.arguments= bundle1
        fragment?.let { loadFragment1(it) }
    }

    private fun setUpFragment2(item_id_2:Int){
        var fragment2 = CompareProductFragment()
        var bundle2 = Bundle()
        bundle2.putInt(ITEM_ID, item_id_2)
        bundle2.putInt(FRAGMENT_POSITION, 2)
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
