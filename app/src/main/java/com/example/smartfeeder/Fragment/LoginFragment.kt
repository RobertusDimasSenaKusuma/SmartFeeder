package com.example.smartfeeder

import android.content.Intent
import android.content.ContentValues.TAG
import android.util.Log
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginFragment : Fragment() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgotPassword: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Initialize views
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword)

        // Set click listener for login button
        btnLogin.setOnClickListener {
            // Validate inputs
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                // Perform login operation
                performLogin(email, password)
            }
        }

        // Set click listener for forgot password
        tvForgotPassword.setOnClickListener {
            // Handle forgot password
            Toast.makeText(context, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun validateInputs(email: String, password: String): Boolean {
        // Simple validation
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    // Navigate to main activity after successful login
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}