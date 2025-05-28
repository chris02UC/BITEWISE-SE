package com.example.bitewise.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class AuthVM : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    var loggedInEmail by mutableStateOf<String?>(null)
        private set

    var authError by mutableStateOf<String?>(null)
        private set

    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || password.length < 4) {
            authError = "Please enter valid email and password (min 4 characters)"
            onResult(false)
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loggedInEmail = email
                    authError = null
                    onResult(true)
                } else {
                    authError = task.exception?.message ?: "Registration failed"
                    onResult(false)
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loggedInEmail = email
                    authError = null
                    onResult(true)
                } else {
                    authError = task.exception?.message ?: "Login failed"
                    onResult(false)
                }
            }
    }

    fun logout() {
        auth.signOut()
        loggedInEmail = null
    }
}
