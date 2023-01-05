package com.quico.tech.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.quico.tech.R
import com.quico.tech.databinding.ActivityHomeBinding
import com.quico.tech.fragment.*
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel


class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private var fragmentHome: HomeFragment? = null
    private var fragmentSearch: SearchFragment? = null
    private var fragmentService: ServiceFragment? = null
    private var fragmentWishlist: WishlistFragment? = null
    private var fragmentProfile: ProfileFragment? = null
    var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Common.hideSystemUIBeloR(this);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    if (fragmentWishlist == null) fragmentWishlist = WishlistFragment()
                    fragment = fragmentWishlist
                }
                else if (position == 4) { //search page selected
                    if (fragmentProfile == null) fragmentProfile = ProfileFragment()
                    fragment = fragmentProfile
                }
                fragment?.let { loadFragment(it) }
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

    override fun onBackPressed() {
        binding.apply {
            if(fragment==fragmentHome){
                bubbleNavigation.setCurrentActiveItem(0)
            }else if(fragment==fragmentProfile){
                bubbleNavigation.setCurrentActiveItem(4)
            }else if(fragment==fragmentSearch){
                bubbleNavigation.setCurrentActiveItem(1)
            }else if(fragment==fragmentWishlist){
                bubbleNavigation.setCurrentActiveItem(3)
            }else if(fragment==fragmentService){
                bubbleNavigation.setCurrentActiveItem(2)
            }
        }
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