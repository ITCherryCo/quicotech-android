package com.quico.tech.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.quico.tech.activity.IntroActivity

class IntroPageAdapter(layoutsList: IntArray, introActivityContext: IntroActivity) : PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private val layouts: IntArray
    private var context: Context

    init {
        layouts = layoutsList
        context = introActivityContext
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val view: View? = layoutInflater?.inflate(layouts.get(position), container, false)
        container.addView(view)
        return view!!
    }

    override fun getCount(): Int {
        return layouts.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}