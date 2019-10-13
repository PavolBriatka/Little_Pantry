package com.briatka.pavol.littlepantry.ui.auth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class LoginPagerAdapter(val context: Context): PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pagerEnum = PagerEnum.values()[position]
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(pagerEnum.layoutId, container, false)
        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val pagerEnum = PagerEnum.values()[position]
        return context.getString(pagerEnum.title)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return PagerEnum.values().size
    }
}