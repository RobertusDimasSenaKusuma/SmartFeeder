package com.example.smartfeeder

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,

    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Initialize views
        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnRegister = view.findViewById(R.id.btnRegister)

        // Set click listener for register button
        btnRegister.setOnClickListener {
            // Validate inputs
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                // Perform registration operation
                performRegistration(name, email, password)
            }
        }

        return view
    }

    private fun validateInputs(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Simple validation
        if (name.isEmpty()) {
            etName.error = "Name is required"
            etName.requestFocus()
            return false
        }

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

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Confirm password is required"
            etConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun performRegistration(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()

                }
            }
    }
}