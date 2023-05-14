package com.example.travelplanner.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.travelplanner.R
import com.example.travelplanner.databinding.ActivityDashboardBinding
import com.example.travelplanner.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : DrawerBaseActivity() {

    lateinit var activityProfileActivity: ActivityProfileBinding

    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileActivity = ActivityProfileBinding.inflate(layoutInflater)
        allocationActivityTitle("Profile")
        setContentView(activityProfileActivity.root)

        fStore = Firebase.firestore
        auth = Firebase.auth
        currentUserId = auth.currentUser?.uid.toString()

        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)

        fStore.collection("users").document(currentUserId).get()
            .addOnSuccessListener { result ->
                val firstName = result.data?.get("first_name").toString()
                val lastName = result.data?.get("last_name").toString()
                profileName.text = "$firstName $lastName"
                val email = result.data?.get("email").toString()
                profileEmail.text = email
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Could not get user details", Toast.LENGTH_LONG).show()

            }
    }


}