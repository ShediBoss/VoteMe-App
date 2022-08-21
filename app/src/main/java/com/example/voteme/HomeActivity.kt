package com.example.voteme

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voteme.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity(), ItemClickListener2 {

    private var cancellationSignal: CancellationSignal? = null

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    votes = true
                    while (canVote==true){
                        votes = true
                        if (votes==true){
                            votesCount++
                            canVote = false

                            notifyUser("Vote have been counted, no other vote can be done")
                        }else{
                            votes = false
                            canVote = true
                        }
                    }

                    val counter = votesCount
                    val userName = FirebaseAuth.getInstance().currentUser!!.displayName.toString()

                    val userMap = hashMapOf(
                        "votesCount" to counter,
                        "candidateName" to userName
                    )

                    val userId = FirebaseAuth.getInstance().currentUser!!.uid

                    firebase.collection("userVotes").document(userId).set(userMap)
                        .addOnSuccessListener {
//                            Toast.makeText(this, "vote submitted!", Toast.LENGTH_SHORT)
                            print("vote submitted!")
                        }
//                    val output = Intent()
//                        output.putExtra("votescount", votesCount.toString())
//                    setResult(Activity.RESULT_OK, output)
//                    finish()

                }
            }

    //view binding
    private lateinit var binding: ActivityHomeBinding

    var canVote = true
    var votes = false
    var votesCount = 0
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: ArrayList<User2>
    private var firebase = Firebase.firestore
    private lateinit var myAdapter: MyAdapter2

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val Il = findViewById<ConstraintLayout>(R.id.bg_layout)
        val animDrawable = Il.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userList = arrayListOf()
        firebase = FirebaseFirestore.getInstance()
        myAdapter = MyAdapter2(userList,this,this)

//        checkUser()

        firebase.collection("user").get().addOnSuccessListener {
            if (!it.isEmpty){
                for (data in it.documents){
                    val user: User2? = data.toObject(User2::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                recyclerView.adapter = myAdapter
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

        private fun checkBiometricSupport(): Boolean {

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isKeyguardSecure) {
            notifyUser("Fingerprint Authentication has not been enabled in settings")
            return false
        }

        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_BIOMETRIC)!= PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint Authentication Permission is not enabled")
            return false
        }

        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }

    private fun getCancellationSignal() : CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun notifyUser(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun vote(user: User2, position: Int) {
        val biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Vote Candidate")
            .setSubtitle("Fingerprint is required")
            .setDescription("This app uses fingerprint authentication to secure vote")
            .setNegativeButton("Cancel",this.mainExecutor,
                DialogInterface.OnClickListener { dialogInterface, which ->
                notifyUser("Authentication cancelled")
            }).build()

        biometricPrompt.authenticate(getCancellationSignal(),mainExecutor,authenticationCallback)

        checkBiometricSupport()
    }


    }









