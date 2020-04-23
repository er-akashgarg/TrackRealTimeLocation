package com.akash.trackrealtimelocation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.akash.trackrealtimelocation.R


/**
 * @author Akash Garg
 * */

class SplashScreenActivity : BaseActivity() {

    private val SPLASH_TIME_OUT: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }, SPLASH_TIME_OUT)
    }
}
