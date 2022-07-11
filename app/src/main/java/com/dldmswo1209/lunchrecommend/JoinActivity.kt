package com.dldmswo1209.lunchrecommend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dldmswo1209.lunchrecommend.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth

class JoinActivity : AppCompatActivity() {
    var mBinding : ActivityJoinBinding? = null
    val binding get() = mBinding!!
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        onClickButton()
    }
    private fun onClickButton(){
        binding.joinButton.setOnClickListener {
            auth.createUserWithEmailAndPassword(binding.joinEmail.text.toString(), binding.joinPassword.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }
}