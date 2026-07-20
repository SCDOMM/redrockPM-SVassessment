package com.example.ept.notify.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ept.notify.R
import com.example.ept.notify.adapter.NotifyAdapter
import com.example.ept.notify.fragment.FragmentInterface
import com.example.ept.notify.fragment.InfoFragment
import com.example.ept.notify.fragment.InteractFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class NotifyActivity : AppCompatActivity() {
    private lateinit var vp2NotifyDefault: ViewPager2
    private lateinit var tlNotifyDefault: TabLayout
    private var fragmentList = mutableListOf<FragmentInterface>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify)
        vp2NotifyDefault = findViewById(R.id.vp2_notify_default)
        tlNotifyDefault = findViewById(R.id.tl_notify_default)
        initEvent()
        initTabs()
    }

    fun initEvent() {
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return InteractFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return InfoFragment()
            }
        })
        val adapter = NotifyAdapter(fragmentList, this)
        vp2NotifyDefault.adapter = adapter
    }

    fun initTabs() {
        TabLayoutMediator(tlNotifyDefault, vp2NotifyDefault) { p0, p1 ->
            if (p1 == 0) {
                p0.text = "互动"
            } else if (p1 == 1) {
                p0.text = "消息"
            }
        }.attach()
    }
}