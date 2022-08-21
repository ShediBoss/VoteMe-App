package com.example.voteme

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.voteme.databinding.ActivityAdminLoginBinding
import com.example.voteme.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class AdminLogin : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val Il = findViewById<ConstraintLayout>(R.id.bg_layout)
        val animDrawable = Il.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        val fadeout = AnimationUtils.loadAnimation(this,R.anim.zoom_in3)
        val logincard = findViewById<CardView>(R.id.loginpage)

        logincard.startAnimation(fadeout)

        binding.regnow.setOnClickListener {
            val intent = Intent(this, AdminRegistration::class.java)
            startActivity(intent)
        }
        firebaseAuth = FirebaseAuth.getInstance()
        binding.loginbtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        val intent = Intent(this, AdminOtpActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Empty Fields are not Allowed", Toast.LENGTH_SHORT).show()
            }


        }

    }
}