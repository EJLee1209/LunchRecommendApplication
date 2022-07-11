package com.dldmswo1209.lunchrecommend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dldmswo1209.lunchrecommend.databinding.ActivityLoginActivitiyBinding
import com.dldmswo1209.lunchrecommend.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivitiy : AppCompatActivity() {
    var mBinding : ActivityLoginActivitiyBinding? = null
    val binding get() = mBinding!!
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginActivitiyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        onclickButton()
    }
    private fun onclickButton(){
        binding.joinText.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            auth.signInWithEmailAndPassword(binding.loginEmail.text.toString(), binding.loginPassword.text.toString())
                .addOnSuccessListener {
                    val sharedPreferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("user_uid", auth.currentUser?.uid.toString())
                    editor.apply()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "로그인 실패, 아이디와 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}