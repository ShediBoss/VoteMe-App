package com.example.voteme

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.voteme.databinding.ActivityAdminRegistrationBinding
import com.example.voteme.databinding.ActivityVotersRegistrationBinding
import com.google.firebase.auth.FirebaseAuth

class AdminRegistration : AppCompatActivity() {

    private lateinit var binding: ActivityAdminRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var backbtn : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminRegistrationBinding.inflate(layoutInflater)

        setContentView(binding.root)

        backbtn = findViewById(R.id.backCard)
        backbtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        val bg = findViewById<ConstraintLayout>(R.id.register_layout)
        val animDrawable = bg.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        val fadeout = AnimationUtils.loadAnimation(this,R.anim.zoom_in3)
        val registercard = findViewById<CardView>(R.id.register_page)

        registercard.startAnimation(fadeout)



        firebaseAuth = FirebaseAuth.getInstance()
        binding.login.setOnClickListener {
            val intent = Intent(this, AdminLogin::class.java)
            startActivity(intent)
        }
        binding.regnow.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val phone_no = binding.phoneNo.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && phone_no.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        val intent = Intent(this, AdminLogin::class.java)
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