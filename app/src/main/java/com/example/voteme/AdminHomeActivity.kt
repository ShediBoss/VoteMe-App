package com.example.voteme

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.voteme.databinding.ActivityAdminHomeBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_admin_home.*
import java.util.*
import kotlin.collections.ArrayList

class AdminHomeActivity : AppCompatActivity() {

    private var firebase = Firebase.firestore
    var mRef: DatabaseReference? = null
    var mDatabase: FirebaseDatabase? = null
    private val TAG = "FirebaseStorageManager"
    private val mStorageRef = FirebaseStorage.getInstance().reference

    private lateinit var userList: ArrayList<User>
    private lateinit var dialog : Dialog
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedImg: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var mStorage: FirebaseStorage
    private lateinit var etCandidateName: TextInputEditText
    private lateinit var etSex: TextInputEditText
    private lateinit var etParty: TextInputEditText
    private lateinit var etPosition: TextInputEditText
    private lateinit var btnSubmit: AppCompatButton
//    private lateinit var progressBar: ProgressBar
    private lateinit var btnImage: CardView
    private lateinit var profileimage: ImageView
    private lateinit var mProgressDialog: ProgressDialog

//    fun uploadImage(mContext: Context, imageURI: Uri){
//        mProgressDialog = ProgressDialog(mContext)
//        mProgressDialog.setMessage("Please wait while image is uploading...")
//        mProgressDialog.show()
//        val uploadTask = mStorageRef.child("${System.currentTimeMillis()}.png").putFile(imageURI)
//        uploadTask.addOnSuccessListener{
//            Log.e(TAG, "Image Upload Successful")
//            val downloadURLTask = mStorageRef.child("${System.currentTimeMillis()}.png").downloadUrl
//            downloadURLTask.addOnSuccessListener {
//                Log.e(TAG, "IMAGE PATH : $it")
//                mProgressDialog.dismiss()
//            }.addOnFailureListener {
//                mProgressDialog.dismiss()
//            }
//        }.addOnFailureListener {
//            Log.e(TAG, "Image upload failed ${it.printStackTrace()}")
//            mProgressDialog.dismiss()
//        }
//    }

    companion object{
        val IMAGE_REQUEST_CODE = 100
    }

//    private val Gallery_Code = 1
//    var imageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        etCandidateName = findViewById(R.id.candidateName)
        etSex = findViewById(R.id.sex)
        etParty = findViewById(R.id.party)
        etPosition = findViewById(R.id.position)
        btnSubmit = findViewById(R.id.submitBtn)
        btnImage = findViewById(R.id.addImageCard)
        profileimage = findViewById(R.id.rugerBg)

        mDatabase = FirebaseDatabase.getInstance()
//        mRef = mDatabase!!.getReference().child("Candidate")
        mStorage = FirebaseStorage.getInstance()

//        btnImage.setOnClickListener {
//            showProgressBar()
//            uploadProfilePic()
//        }


        userList = arrayListOf()

        btnImage.setOnClickListener {
            pickImageGallery()
            /* Press Alt + Enter*/
        }

        binding.skip.setOnClickListener {
            startActivity(Intent(this,CandidateActivity::class.java))
        }

        btnSubmit.setOnClickListener {
            showProgressBar()

            val canName = etCandidateName.text.toString().trim()
            val canSex = etSex.text.toString().trim()
            val canParty = etParty.text.toString().trim()
            val canPosition = etPosition.text.toString().trim()
            val imgUrl = selectedImg.toString()

            val userMap = hashMapOf(
                "image" to imgUrl,
                "name" to canName,
                "gender" to canSex,
                "party" to canParty,
                "position" to canPosition,
            )

            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            val reference = mStorage.reference.child("Candidates").child(Date().time.toString())
            reference.putFile(selectedImg).addOnCompleteListener{
                if (it.isSuccessful){
                    reference.downloadUrl.addOnSuccessListener {
                        selectedImg.toString()
                    }
                }
            }

            firebase.collection("user").document(userId).set(userMap)
                .addOnSuccessListener {
                    hideProgressBar()
                    Toast.makeText(this, "Successfully submitted!", Toast.LENGTH_SHORT).show()
                    etCandidateName.text?.clear()
                    etSex.text?.clear()
                    etParty.text?.clear()
                    etPosition.text?.clear()
//                    uploadPic()
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

//    private fun uploadPic() {
//        val reference = mStorage.reference.child("Candidates").child(Date().time.toString())
//        reference.putFile(selectedImg).addOnCompleteListener{
//            if (it.isSuccessful){
//                reference.downloadUrl.addOnSuccessListener { task ->
//                    uploadInfo(task.toString())
//                }
//            }
//        }
//    }

    private fun uploadInfo(imgUrl: String) {
        val userMap = hashMapOf(
            "image" to  imgUrl
        )

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        firebase.collection("user").document(userId).set(userMap)
            .addOnSuccessListener {
                finish()
            }
    }


//    private fun uploadProfilePic() {
//
//        imageUri = Uri.parse("android.resource://$packageName/${R.drawable.voteme_logo}")
//        storageReference = FirebaseStorage.getInstance().getReference("Candidates/"+auth.uid)
//        storageReference.putFile(imageUri).addOnSuccessListener {
//            hideProgressBar()
//            Toast.makeText(this, "Profile uploaded successfully", Toast.LENGTH_SHORT).show()
//
//        }
//            .addOnFailureListener {
//                hideProgressBar()
//                Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
//            }
//    }

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

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            if (data != null){
                selectedImg = data.data!!
                profileimage.setImageURI(selectedImg)
            }
//            uploadImage(this, data?.data!!)
        }
    }
}