package com.quico.tech.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener
import com.quico.tech.R
import com.quico.tech.databinding.ActivityHomeBinding
import com.quico.tech.fragment.HomeFragment
import com.quico.tech.fragment.ProfileFragment
import com.quico.tech.fragment.SearchFragment
import com.quico.tech.fragment.ServiceFragment
import com.quico.tech.utils.Common

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private var actionBar: ActionBar? = null
    private var fragmentHome: HomeFragment? = null
    private var fragmentSearch: SearchFragment? = null
    private var fragmentService: ServiceFragment? = null
    private var fragmentProfile: ProfileFragment? = null
    var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Common.hideSystemUIBeloR(this);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar();

        binding.apply {

            if (fragmentHome == null && fragment==null) {
                fragmentHome = HomeFragment()
                fragment = fragmentHome
                fragment?.let { loadFragment(it) }
            }


            bubbleNavigation.setNavigationChangeListener { view, position ->

                if (position == 0) { //home page selected
                    if (fragmentHome == null) fragmentHome = HomeFragment()
                    fragment = fragmentHome
                } else if (position == 1) {
                    if (fragmentSearch == null) fragmentSearch = SearchFragment()
                    fragment = fragmentSearch
                } else if (position == 2) {
                    if (fragmentService == null) fragmentService = ServiceFragment()
                    fragment = fragmentService
                } else if (position == 3) { //search page selected

                }
                else if (position == 4) { //search page selected
                    if (fragmentProfile == null) fragmentProfile = ProfileFragment()
                    fragment = fragmentProfile
                }
                fragment?.let { loadFragment(it) }
            }

        }
    }

    fun initToolbar(){
        binding.apply {
            toolbarInclude.toolbar.title = getString(R.string.app_name)
            Common.changeOverflowMenuIconColor(toolbarInclude.toolbar, resources.getColor(R.color.color_primary_purple))
            Common.setSystemBarColor(this@HomeActivity, R.color.white)
            Common.setSystemBarLight(this@HomeActivity)
            toolbarInclude.bagIcon.setOnClickListener {
                startActivity(Intent(this@HomeActivity, CartActivity::class.java))
            }
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