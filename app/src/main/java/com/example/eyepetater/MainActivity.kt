package com.example.eyepetater

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ept.hot.fragment.HotFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HotFragment())
                .commit()
        }
    }
}
