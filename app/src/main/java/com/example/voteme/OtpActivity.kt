package com.example.voteme

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.voteme.databinding.ActivityOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

//    View Binding
    private lateinit var binding: ActivityOtpBinding

//    if code sending failed, will used to resend
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    //    Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    private var TAG = "MAIN_TAG"

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val Il = findViewById<ConstraintLayout>(R.id.bg_layout)
        val animDrawable = Il.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        
        //init, set up progress Dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //verification state change callbacks, verification completed, verification failed, code sent etc
        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant Verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play Services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted: ")
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                //  This callback is invoked if an invalid request for validation was made,
                //  for instance if the phone number format is not valid
                progressDialog.dismiss()
                Log.d(TAG, "onVerificationFailed: ${e.message} ")
                Toast.makeText(this@OtpActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                //  The SMS verification code has been sent to the provided phone number, we
                //  now need to ask user to enter the code and then construct a credential
                //  by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent: $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()
                Toast.makeText(this@OtpActivity, "Verification code sent...", Toast.LENGTH_SHORT).show()
                binding.VerifyHint.text = "Please type the verification code we sent to ${binding.PhoneNo.text.toString().trim()}"
            }

        }

        //PhoneContinue Button Click: input phone number, validate, start phone authentication/login
        binding.continuePhone.setOnClickListener {
            //Input Phone Number
            val phone = binding.PhoneNo.text.toString().trim()
            //Validate phone number
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@OtpActivity, "Please enter phone number", Toast.LENGTH_SHORT).show()
            }
            else {
                startPhoneNumberVerification(phone)
            }
        }

        //resend Code click: (if code wasn't received) resend verification code/ OTP
        binding.resendCode.setOnClickListener {
            //Input Phone Number
            val phone = binding.PhoneNo.text.toString().trim()
            //Validate phone number
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@OtpActivity, "Please enter phone number", Toast.LENGTH_SHORT).show()
            }
            else {
                resendVerificationCode(phone, forceResendingToken!!)
            }
        }

        //codeSubmitBtn click: input verification code, validate, verify phone number with verification code
        binding.submitBtn.setOnClickListener {
            //input Verification code
            val code = binding.verifyInput.text.toString().trim()
            if (TextUtils.isEmpty(code)){
                Toast.makeText(this@OtpActivity, "Please enter verification code", Toast.LENGTH_SHORT).show()

            }
            else{
                verifyPhoneNumberWithCode(mVerificationId, code)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun startPhoneNumberVerification(phone: String){
        Log.d(TAG, "startPhoneNumberVerification: $phone ")
        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()

        val options =
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone).setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBacks!!)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun resendVerificationCode(phone: String, token: PhoneAuthProvider.ForceResendingToken){
        progressDialog.setMessage("Resending Code...")
        progressDialog.show()

        Log.d(TAG, "resendVerificationCode: $phone ")

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone).setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBacks!!)
                .setForceResendingToken(token)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String?){
        Log.d(TAG, "verifyPhoneNumberWithCode: $verificationId $code")
        progressDialog.setMessage("Verifying Code...")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId.toString(), code.toString())
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredential:")
        progressDialog.setMessage("Logging In")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                //Login Success
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(this, "Logged In as $phone", Toast.LENGTH_SHORT).show()

                //start profile activity
                startActivity(Intent(this,HomeActivity::class.java))

            }
            .addOnFailureListener { e->
                //Login Failed
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}