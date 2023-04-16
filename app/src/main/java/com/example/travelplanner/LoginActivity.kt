package com.example.travelplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    //auth is used to authenticate users for firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val registerText: TextView = findViewById(R.id.register_now)
        registerText.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val loginButton: Button = findViewById(R.id.signin_btn)
        loginButton.setOnClickListener{
            performLogin()
        }
    }

    private fun performLogin() {
        val email: EditText = findViewById(R.id.email_login)
        val password: EditText = findViewById(R.id.password_login)

        //if the input fields are empty notify user
        if(email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val emailInput = email.text.toString()
        val passwordInput = password.text.toString()

        //attempt to sign in users with provided details
        auth.signInWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, go to TripsActivity to display trips

                    val intent = Intent(this, TripsActivity::class.java)
                    startActivity(intent)


                    Toast.makeText(baseContext, "Welcome ", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener{
                Toast.makeText(baseContext, "Authentication failed ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }



    }


}//end class