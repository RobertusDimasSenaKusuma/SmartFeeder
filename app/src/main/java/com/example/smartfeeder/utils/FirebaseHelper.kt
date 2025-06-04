package com.example.smartfeeder.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.smartfeeder.models.ScheduleModel
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FirebaseHelper {
    private val realtimeDb = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private const val SCHEDULES_PATH = "schedules"
    private const val DEVICE_STATUS_PATH = "device_status"
    private const val FEEDING_COMMANDS_PATH = "feeding_commands"
    private const val TAG = "FirebaseHelper"

    // Test Realtime Database connection
    suspend fun testRealtimeDatabaseWrite(): Result<Unit> {
        return try {
            val testData = mapOf("test" to "connection_test", "timestamp" to System.currentTimeMillis())
            realtimeDb.getReference("test").setValue(testData).await()
            Log.d(TAG, "Test write to Realtime Database successful at /test")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Test write failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun saveSchedule(schedule: ScheduleModel): Result<String> {
        return try {
            Log.d(TAG, "Saving schedule")

            val scheduleId = schedule.id.takeIf { it.isNotEmpty() } ?: ScheduleModel.generateId()
            val scheduleWithId = ScheduleModel(
                id = scheduleId,
                namaJadwal = schedule.namaJadwal,
                jenisMakanan = schedule.jenisMakanan,
                waktu = schedule.waktu,
                userId = "", // Set kosong atau bisa dihapus dari model
                timestamp = System.currentTimeMillis(),
                createdAt = schedule.createdAt
            )

            // Save to Realtime Database langsung ke schedules root
            realtimeDb.getReference("$SCHEDULES_PATH/$scheduleId")
                .setValue(scheduleWithId.toMap())
                .await()

            Log.d(TAG, "Schedule saved successfully: $scheduleId")
            Result.success("Jadwal berhasil disimpan")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save schedule", e)
            Result.failure(e)
        }
    }

    suspend fun getAllSchedules(): Result<List<ScheduleModel>> {
        return try {
            Log.d(TAG, "Fetching all schedules")

            suspendCancellableCoroutine { continuation ->
                val schedulesRef = realtimeDb.getReference(SCHEDULES_PATH)

                schedulesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val schedules = mutableListOf<ScheduleModel>()

                            for (childSnapshot in snapshot.children) {
                                val scheduleMap = childSnapshot.value as? Map<String, Any>
                                if (scheduleMap != null) {
                                    val schedule = ScheduleModel.fromMap(scheduleMap)
                                    schedules.add(schedule)
                                }
                            }

                            Log.d(TAG, "Fetched ${schedules.size} schedules")
                            continuation.resume(Result.success(schedules))
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing schedules", e)
                            continuation.resume(Result.failure(e))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Failed to get schedules: ${error.message}", error.toException())
                        continuation.resume(Result.failure(error.toException()))
                    }
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get schedules", e)
            Result.failure(e)
        }
    }

    suspend fun deleteSchedule(scheduleId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting schedule: $scheduleId")

            realtimeDb.getReference("$SCHEDULES_PATH/$scheduleId")
                .removeValue()
                .await()

            Log.d(TAG, "Schedule deleted successfully: $scheduleId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete schedule: $scheduleId", e)
            Result.failure(e)
        }
    }

    suspend fun updateScheduleStatus(scheduleId: String, isActive: Boolean, status: String): Result<Unit> {
        return try {
            Log.d(TAG, "Updating schedule status: $scheduleId, isActive: $isActive, status: $status")

            val updates = mapOf(
                "isActive" to isActive,
                "status" to status,
                "timestamp" to System.currentTimeMillis()
            )

            realtimeDb.getReference("$SCHEDULES_PATH/$scheduleId")
                .updateChildren(updates)
                .await()

            Log.d(TAG, "Schedule status updated successfully: $scheduleId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update schedule status: $scheduleId", e)
            Result.failure(e)
        }
    }

    suspend fun updateDeviceStatus(
        deviceId: String,
        isOnline: Boolean,
        lastActive: Long = System.currentTimeMillis()
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Updating device status for device: $deviceId, isOnline: $isOnline")

            val statusData = mapOf(
                "isOnline" to isOnline,
                "lastActive" to lastActive
            )

            realtimeDb.getReference("$DEVICE_STATUS_PATH/$deviceId")
                .setValue(statusData)
                .await()

            Log.d(TAG, "Device status updated successfully: $deviceId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update device status: $deviceId, error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun triggerFeeding(
        deviceId: String,
        scheduleId: String,
        portion: Int
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Triggering feeding for device: $deviceId, schedule: $scheduleId, portion: $portion")

            val commandData = mapOf(
                "command" to "FEED_NOW",
                "scheduleId" to scheduleId,
                "portion" to portion,
                "timestamp" to System.currentTimeMillis(),
                "status" to "PENDING"
            )

            realtimeDb.getReference("$FEEDING_COMMANDS_PATH/$deviceId")
                .setValue(commandData)
                .await()

            Log.d(TAG, "Feeding command sent successfully: $deviceId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send feeding command: $deviceId, error: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun listenToFeedingResponse(
        deviceId: String,
        onResponse: (status: String, message: String) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener {
        Log.d(TAG, "Setting up feeding response listener for device: $deviceId")
        val responseRef = realtimeDb.getReference("$FEEDING_COMMANDS_PATH/$deviceId/status")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(String::class.java) ?: "UNKNOWN"
                Log.d(TAG, "Feeding response received for $deviceId: $status")
                when (status) {
                    "DONE" -> onResponse("DONE", "Feeding completed successfully")
                    "FAILED" -> onResponse("FAILED", "Feeding failed")
                    else -> onResponse(status, "Unknown status received")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Feeding listener cancelled for $deviceId: ${error.message}", error.toException())
                onError("Listener cancelled: ${error.message}")
            }
        }

        responseRef.addValueEventListener(listener)
        return listener
    }

    fun removeFeedingListener(deviceId: String, listener: ValueEventListener) {
        try {
            Log.d(TAG, "Removing feeding listener for device: $deviceId")
            realtimeDb.getReference("$FEEDING_COMMANDS_PATH/$deviceId")
                .removeEventListener(listener)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove feeding listener: $deviceId", e)
        }
    }
}