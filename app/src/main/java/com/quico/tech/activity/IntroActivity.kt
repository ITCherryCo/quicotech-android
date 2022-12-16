package com.quico.tech.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.quico.tech.R
import com.quico.tech.adapter.IntroPageAdapter
import com.quico.tech.data.PrefManager
import com.quico.tech.utils.Common
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class IntroActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private var myViewPagerAdapter: IntroPageAdapter? = null
    private var dotsLayout: LinearLayout? = null
    private lateinit var layouts: IntArray
    private lateinit var txtSkip: TextView
    private var prefManager: PrefManager? = null
    private var dots: WormDotsIndicator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Checking for first time launch
        prefManager = PrefManager(this);
//        if (!prefManager!!.isFirstTimeLaunch) {
//            launchHomeScreen();
//            finish();
//         }

        //Remove Statusbar
        Common.removeStatusBarColor(this);
        Common.hideSystemUIBeloR(this);

        setContentView(R.layout.activity_intro)
        init();
    }

    private fun init(){
        viewPager = findViewById (R.id.view_pager);
        dotsLayout = findViewById (R.id.layoutDots);
        txtSkip =  findViewById (R.id.txt_skip);
        dots= findViewById(R.id.dots);

        // layouts of all intro sliders
        layouts = intArrayOf(R.layout.intro_first_slide, R.layout.intro_second_slide)

        //add adapter to viewPager
        myViewPagerAdapter = IntroPageAdapter(layouts, this)
        viewPager.setAdapter(myViewPagerAdapter)
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        // adding bottom dots
        dots?.attachTo(viewPager);

        //add listener to skip btn
        txtSkip.setOnClickListener(View.OnClickListener { launchHomeScreen() })
    }

    private fun removeStatusBarColor() {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun hideSystemUIBeloR() {
        val decorView: View = window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = newUiOptions
    }

    private fun launchHomeScreen() {
        prefManager?.isFirstTimeLaunch = false
        startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
        finish()
    }

    //  viewpager change listener
    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == 1) {
                // last page. make button text to GOT IT
                txtSkip.visibility = View.VISIBLE
            } else {
                // still pages are left
                txtSkip.visibility = View.GONE
            }
        }
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }
}