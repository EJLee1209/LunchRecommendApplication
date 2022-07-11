package com.dldmswo1209.lunchrecommend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val sharedPreferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE)
        val user_uid = sharedPreferences.getString("user_uid","")
        when(user_uid){
            ""->{
                Handler().postDelayed({
                    startActivity(Intent(this, LoginActivitiy::class.java))
                }, DURATION) // splash화면을 띄우고 3초 후에 화면 전환
            }
            else->{
                Handler().postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                }, DURATION)

            }
        }


    }
    companion object {
        private const val DURATION : Long = 3000
    }
}