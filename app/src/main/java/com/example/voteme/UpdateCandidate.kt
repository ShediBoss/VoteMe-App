package com.example.voteme

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.voteme.databinding.ActivityAdminHomeBinding
import com.example.voteme.databinding.ActivityUpdateCandidateBinding
import com.example.voteme.databinding.ActivityUpdatevoterBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpdateCandidate : AppCompatActivity() {

    private var firebase = Firebase.firestore
    var mRef: DatabaseReference? = null
    var mDatabase: FirebaseDatabase? = null
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var votes: TextView
    private lateinit var userList: ArrayList<User>
    private lateinit var dialog : Dialog
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var binding: ActivityUpdateCandidateBinding
    private lateinit var mStorage: FirebaseStorage
    private lateinit var etCandidateName: TextInputEditText
    private lateinit var etSex: TextInputEditText
    private lateinit var etParty: TextInputEditText
    private lateinit var etPosition: TextInputEditText
    private lateinit var btnSubmit: AppCompatButton
    //    private lateinit var progressBar: ProgressBar
    private lateinit var btnImage: CardView
    private val Gallery_Code = 1
    var imageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCandidateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        etCandidateName = findViewById(R.id.candidateName)
        etSex = findViewById(R.id.sex)
        etParty = findViewById(R.id.party)
        etPosition = findViewById(R.id.position)
        btnSubmit = findViewById(R.id.submitBtn)
        btnImage = findViewById(R.id.addImageCard)
        votes = findViewById(R.id.totalVotes)

        mDatabase = FirebaseDatabase.getInstance()
        mRef = mDatabase!!.getReference().child("Candidate")
        mStorage = FirebaseStorage.getInstance()

//        btnImage.setOnClickListener {
//            showProgressBar()
//            uploadProfilePic()
//        }

//        val votesCount = intent.getStringExtra("votescount")
//        votes.text = ("Total Votes: $votesCount")

        val canName = intent.getStringExtra("canName")

        userList = arrayListOf()

        firebase.collection("userVotes").get().addOnCompleteListener {

            val result : StringBuffer = StringBuffer()

            if (it.isSuccessful){
                for (document in it.result!!){
                    result.append(document.data.getValue("candidateName")).append(" ")
                        .append(document.data.getValue("votesCount")).append("\n\n")
                }
                votes.setText(result)
            }
        }

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
            startActivity(Intent(this,MainActivity::class.java))
        }

        binding.totalVotes.setOnClickListener {
            Toast.makeText(this,"Number of votes = 100", Toast.LENGTH_SHORT).show()
        }

        btnSubmit.setOnClickListener {
            showProgressBar()

            val canName = etCandidateName.text.toString().trim()
            val canSex = etSex.text.toString().trim()
            val canParty = etParty.text.toString().trim()
            val canPosition = etPosition.text.toString().trim()

            val userMap = hashMapOf(
                "image" to "https://media.premiumtimesng.com/wp-content/files/2021/10/Peter-Obi.jpg",
                "name" to canName,
                "gender" to canSex,
                "party" to canParty,
                "position" to canPosition,
            )

            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            firebase.collection("user").document(userId).set(userMap)
                .addOnSuccessListener {
                    hideProgressBar()
                    Toast.makeText(this, "Successfully Updated!", Toast.LENGTH_SHORT).show()
                    etCandidateName.text?.clear()
                    etSex.text?.clear()
                    etParty.text?.clear()
                    etPosition.text?.clear()

//                    uploadProfilePic()

                }
                .addOnFailureListener {
                    hideProgressBar()
                    Toast.makeText(
                        this,
                        "Data not submitted, make sure you are connected to the internet...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            startActivity(Intent(this,CandidateActivity::class.java))

        }

    }

    private fun showProgressBar(){
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }

    private fun checkUser() {
        //get Current User
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            //logged out
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        else {
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }
    }

}