package com.example.smartfeeder

import android.app.Application
import android.util.Log
import com.example.smartfeeder.utils.FirebaseHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyApplication : Application() {
    companion object {
        private const val TAG = "MyApplication"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyApplication onCreate() started")

        // Initialize Firebase
        initializeFirebase()

        // Initialize app components after Firebase is ready
        initializeAppComponents()
    }

    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")

            // Enable offline persistence for Firebase Realtime Database
            enableDatabasePersistence()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase: ${e.message}", e)
        }
    }

    private fun enableDatabasePersistence() {
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            Log.d(TAG, "Firebase Realtime Database persistence enabled")
        } catch (e: IllegalStateException) {
            Log.w(TAG, "Persistence already enabled or database already in use", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable persistence: ${e.message}", e)
        }
    }

    private fun initializeAppComponents() {
        // Initialize components in background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Ensure user is authenticated before proceeding
                ensureUserAuthentication()

                // Initialize device status after authentication
                initializeDeviceStatus()

                // Test database connectivity
                testDatabaseConnection()

            } catch (e: Exception) {
                Log.e(TAG, "Error during app initialization: ${e.message}", e)
            }
        }
    }

    private suspend fun ensureUserAuthentication() {
        try {
            val auth = FirebaseAuth.getInstance()

            if (auth.currentUser == null) {
                Log.d(TAG, "No authenticated user found, signing in anonymously...")
                auth.signInAnonymously().await()
                Log.d(TAG, "Anonymous authentication successful - User ID: ${auth.currentUser?.uid}")
            } else {
                Log.d(TAG, "User already authenticated - User ID: ${auth.currentUser?.uid}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Authentication failed: ${e.message}", e)
            throw e
        }
    }

    private suspend fun initializeDeviceStatus() {
        try {
            Log.d(TAG, "Initializing default device status...")

            val result = FirebaseHelper.updateDeviceStatus(
                deviceId = "feeder_001",
                isOnline = false,
                lastActive = System.currentTimeMillis()
            )

            result.fold(
                onSuccess = {
                    Log.d(TAG, "Device status initialized successfully for feeder_001")
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to initialize device status: ${error.message}", error)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during device status initialization: ${e.message}", e)
        }
    }

    private suspend fun testDatabaseConnection() {
        try {
            Log.d(TAG, "Testing Realtime Database connection...")

            val result = FirebaseHelper.testRealtimeDatabaseWrite()

            result.fold(
                onSuccess = {
                    Log.d(TAG, "Database connection test successful")
                },
                onFailure = { error ->
                    Log.e(TAG, "Database connection test failed: ${error.message}", error)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during database connection test: ${e.message}", e)
        }
    }
}