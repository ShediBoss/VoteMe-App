package com.example.voteme

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    private lateinit var votersbtn : AppCompatButton
    private lateinit var adminbtn : AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        votersbtn = findViewById(R.id.voters)
        votersbtn.setOnClickListener {
            startActivity(Intent(this,VotersRegistration::class.java))
        }

        adminbtn = findViewById(R.id.admin)
        adminbtn.setOnClickListener {
            startActivity(Intent(this,AdminRegistration::class.java))
        }

    }
}