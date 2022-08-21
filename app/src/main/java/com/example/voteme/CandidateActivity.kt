package com.example.voteme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CandidateActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private var firebase = Firebase.firestore
    private lateinit var myAdapter: MyAdapter
    private lateinit var votes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userList = arrayListOf()
        firebase = FirebaseFirestore.getInstance()
        myAdapter = MyAdapter(userList, this, this)

        val canName = intent.getStringExtra("canName")
//
//        myAdapter.onItemClick = {
//            startActivity(Intent(this, AdminRegistration::class.java))
//        }

//        firebase.collection("user").get().addOnSuccessListener(object : ValueEventListener,
//            OnSuccessListener<QuerySnapshot> {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()){
//                    for (dataSnapshot in snapshot.children) {
//                        val data = dataSnapshot.getValue(User::class.java)
//                        userList.add(data!!)
//                    }
//                    recyclerView.adapter = myAdapter
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@CandidateActivity, error.toString(), Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onSuccess(p0: QuerySnapshot?) {
//                if (p0!!.isEmpty){
//                    for (data in p0.documents) {
//                        val user: User? = data.toObject(User::class.java)
//                    if (user != null) {
//                        userList.add(user)
//                    }
//                }
//                    recyclerView.adapter = myAdapter
//                }
//            }
//
//        })



        firebase.collection("user").get().addOnSuccessListener {
            if (!it.isEmpty){
                for (data in it.documents){
                    val user: User? = data.toObject(User::class.java)
                    userList.add(user!!)
//                    if (user != null) {
//                        userList.add(user)
//                    }
                }
                recyclerView.adapter = myAdapter
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun edit(user: User, position: Int) {
        startActivity(Intent(this,UpdateCandidate::class.java))
    }
}