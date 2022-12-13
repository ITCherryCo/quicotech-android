package com.quico.tech.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.quico.tech.R
import com.quico.tech.databinding.ActivityHomeBinding
import com.quico.tech.fragment.HomeFragment
import com.quico.tech.fragment.SearchFragment
import com.quico.tech.utils.Common

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private var actionBar: ActionBar? = null
    private var fragmentHome: HomeFragment? = null
    private var fragmentSearch: SearchFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Common.hideSystemUIBeloR(this);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar();

        binding.apply {

            var fragment: Fragment? = null
            if (fragmentHome == null) fragmentHome = HomeFragment()
                   fragment = fragmentHome

            if (fragment != null) loadFragment(fragment)

//            bottomBar.onItemSelected = {
//                if (it==0){ //home page selected
//                    if (fragmentHome == null) fragmentHome = HomeFragment()
//                    fragment = fragmentHome
//                }else if(it==1){ //search page selected
//                    if (fragmentSearch == null) fragmentSearch = SearchFragment()
//                    fragment = fragmentSearch
//                }
//                if (fragment != null) loadFragment(fragment)
//            }
//
//            bottomBar.onItemReselected = {
//            }
        }
    }

    fun initToolbar(){
        binding.apply {
            toolbarInclude.toolbar.title = getString(R.string.app_name)
            Common.changeOverflowMenuIconColor(toolbarInclude.toolbar, resources.getColor(R.color.color_primary_purple))
            Common.setSystemBarColor(this@HomeActivity, R.color.white)
            Common.setSystemBarLight(this@HomeActivity)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val item_id = item.itemId
//        if (item_id == android.R.id.home) {
//            finish()
//        } else if (item_id == R.id.action_search) {
//            //ActivitySearch.navigate(this, null)
//        }
        return super.onOptionsItemSelected(item)
    }
}