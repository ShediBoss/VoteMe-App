package com.example.voteme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CandidatesActivity : AppCompatActivity() {

    private lateinit var etCandidateName: TextView
    private lateinit var etSex: TextView
    private lateinit var etParty: TextView
    private lateinit var etPosition: TextView

    private  var firebase = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidates)

        etCandidateName = findViewById(R.id.name)
        etSex = findViewById(R.id.gender)
        etParty = findViewById(R.id.party)
        etPosition = findViewById(R.id.position)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = firebase.collection("user").document(userId)
        ref.get().addOnSuccessListener {
            if (it != null){
                val canName = it.data?.get("name")?.toString()
                val canGender = it.data?.get("gender")?.toString()
                val canParty = it.data?.get("party")?.toString()
                val canposition = it.data?.get("position")?.toString()

                etCandidateName.text = canName
                etSex.text = canGender
                etParty.text = canParty
                etPosition.text = canposition
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show()
            }
    }
}