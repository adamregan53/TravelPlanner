package com.example.travelplanner

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterUser : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginText: TextView
    private lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        auth = Firebase.auth
        fStore = Firebase.firestore

        loginText = findViewById(R.id.login_now)
        loginText.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val registerButton: Button = findViewById(R.id.signup_btn)
        registerButton.setOnClickListener{
            performSignUp()
        }

    }

    private fun performSignUp() {
        val firstName = findViewById<EditText>(R.id.fname_register)
        val lastName = findViewById<EditText>(R.id.lname_register)
        val email = findViewById<EditText>(R.id.email_register)
        val password = findViewById<EditText>(R.id.password_register)

        if(firstName.text.isEmpty() || lastName.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val firstNameInput = firstName.text.toString().trim()
        val lastNameInput = lastName.text.toString().trim()
        val emailInput = email.text.toString().trim()
        val passwordInput = password.text.toString().trim()



        auth.createUserWithEmailAndPassword(emailInput,passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TripsActivity::class.java)

                    //add user to firestore database
                    val userId = auth.currentUser?.uid
                    val emailString = auth.currentUser?.email.toString()
                    val documentReference: DocumentReference = fStore.collection("users").document(
                        userId.toString()
                    )
                    val user = hashMapOf(
                        "first_name" to firstNameInput,
                        "last_name" to lastNameInput,
                        "email" to emailInput
                    )
                    documentReference.set(user).addOnSuccessListener {
                        Log.d(TAG, "onSuccess: user profile created for $userId")
                        Toast.makeText(baseContext, "User profile create for $emailString", Toast.LENGTH_SHORT).show()
                    }


                    //start main activity to show map on login success
                    startActivity(intent)


                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Error Occured ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }


}