package com.briatka.pavol.littlepantry.ui.auth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter
import com.briatka.pavol.littlepantry.R
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable

class LoginPagerAdapter(val context: Context): PagerAdapter() {

    var onButtonClicked: ((View) -> Unit)? = null
    private var disposables = CompositeDisposable()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pagerEnum = PagerEnum.values()[position]
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(pagerEnum.layoutId, container, false)

        when (layout.id) {
            R.id.cl_register -> {
                val button = layout.findViewById<Button>(R.id.btn_register_email_password)
                button.clicks()
                    .subscribe {
                        onButtonClicked?.invoke(button)
                    }.let { disposables.add(it) }
            }
            R.id.cl_login -> {
                val button = layout.findViewById<Button>(R.id.btn_login_email_password)
                button.clicks()
                    .subscribe {
                        onButtonClicked?.invoke(button)
                    }.let { disposables.add(it) }
            }
        }
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