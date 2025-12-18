package com.example.gymladz.data.workout

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

/**
 * Repository for managing workout data using DataStore
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "workout_data")

class WorkoutRepository(private val context: Context) {
    
    companion object {
        private val WORKOUT_SESSIONS_KEY = stringPreferencesKey("workout_sessions")
        private val WORKOUT_PLANS_KEY = stringPreferencesKey("workout_plans")
    }
    
    // Save workout session
    suspend fun saveWorkoutSession(session: WorkoutSession) {
        context.dataStore.edit { preferences ->
            val currentSessions = preferences[WORKOUT_SESSIONS_KEY] ?: "[]"
            val sessionsArray = JSONArray(currentSessions)
            sessionsArray.put(sessionToJson(session))
            preferences[WORKOUT_SESSIONS_KEY] = sessionsArray.toString()
        }
    }
    
    // Get all workout sessions
    fun getWorkoutSessions(): Flow<List<WorkoutSession>> {
        return context.dataStore.data.map { preferences ->
            val sessionsJson = preferences[WORKOUT_SESSIONS_KEY] ?: "[]"
            val sessionsArray = JSONArray(sessionsJson)
            List(sessionsArray.length()) { index ->
                jsonToSession(sessionsArray.getJSONObject(index))
            }
        }
    }
    
    // Get workout history
    fun getWorkoutHistory(): Flow<WorkoutHistory> {
        return getWorkoutSessions().map { sessions ->
            WorkoutHistory.fromSessions(sessions)
        }
    }
    
    // Get sessions for specific exercise
    fun getExerciseSessions(exerciseId: Int): Flow<List<WorkoutSession>> {
        return getWorkoutSessions().map { sessions ->
            sessions.filter { it.exerciseId == exerciseId }
                .sortedByDescending { it.timestamp }
        }
    }
    
    // Get personal best for exercise
    fun getPersonalBest(exerciseId: Int, exerciseName: String): Flow<PersonalBest?> {
        return getExerciseSessions(exerciseId).map { sessions ->
            if (sessions.isEmpty()) return@map null
            
            val best = sessions.maxByOrNull { it.completedReps }
            best?.let {
                PersonalBest(
                    exerciseId = exerciseId,
                    exerciseName = exerciseName,
                    maxReps = it.completedReps,
                    date = it.timestamp
                )
            }
        }
    }
    
    // Save workout plan
    suspend fun saveWorkoutPlan(plan: WorkoutPlan) {
        context.dataStore.edit { preferences ->
            val currentPlans = preferences[WORKOUT_PLANS_KEY] ?: "[]"
            val plansArray = JSONArray(currentPlans)
            plansArray.put(planToJson(plan))
            preferences[WORKOUT_PLANS_KEY] = plansArray.toString()
        }
    }
    
    // Get all workout plans
    fun getWorkoutPlans(): Flow<List<WorkoutPlan>> {
        return context.dataStore.data.map { preferences ->
            val plansJson = preferences[WORKOUT_PLANS_KEY] ?: "[]"
            val plansArray = JSONArray(plansJson)
            List(plansArray.length()) { index ->
                jsonToPlan(plansArray.getJSONObject(index))
            }
        }
    }
    
    // Delete workout plan
    suspend fun deleteWorkoutPlan(planId: String) {
        context.dataStore.edit { preferences ->
            val currentPlans = preferences[WORKOUT_PLANS_KEY] ?: "[]"
            val plansArray = JSONArray(currentPlans)
            val newPlansArray = JSONArray()
            
            for (i in 0 until plansArray.length()) {
                val plan = plansArray.getJSONObject(i)
                if (plan.getString("id") != planId) {
                    newPlansArray.put(plan)
                }
            }
            
            preferences[WORKOUT_PLANS_KEY] = newPlansArray.toString()
        }
    }
    
    // JSON conversion helpers
    private fun sessionToJson(session: WorkoutSession): JSONObject {
        return JSONObject().apply {
            put("id", session.id)
            put("exerciseId", session.exerciseId)
            put("exerciseName", session.exerciseName)
            put("goalReps", session.goalReps)
            put("completedReps", session.completedReps)
            put("duration", session.duration)
            put("caloriesBurned", session.caloriesBurned)
            put("formQuality", session.formQuality)
            put("timestamp", session.timestamp)
            put("isCompleted", session.isCompleted)
        }
    }
    
    private fun jsonToSession(json: JSONObject): WorkoutSession {
        return WorkoutSession(
            id = json.getString("id"),
            exerciseId = json.getInt("exerciseId"),
            exerciseName = json.getString("exerciseName"),
            goalReps = json.getInt("goalReps"),
            completedReps = json.getInt("completedReps"),
            duration = json.getLong("duration"),
            caloriesBurned = json.getInt("caloriesBurned"),
            formQuality = json.getDouble("formQuality").toFloat(),
            timestamp = json.getLong("timestamp"),
            isCompleted = json.getBoolean("isCompleted")
        )
    }
    
    private fun planToJson(plan: WorkoutPlan): JSONObject {
        return JSONObject().apply {
            put("id", plan.id)
            put("name", plan.name)
            put("createdAt", plan.createdAt)
            put("lastUsed", plan.lastUsed)
            
            val exercisesArray = JSONArray()
            plan.exercises.forEach { exercise ->
                exercisesArray.put(JSONObject().apply {
                    put("exerciseId", exercise.exerciseId)
                    put("exerciseName", exercise.exerciseName)
                    put("sets", exercise.sets)
                    put("reps", exercise.reps)
                    put("restTime", exercise.restTime)
                })
            }
            put("exercises", exercisesArray)
        }
    }
    
    private fun jsonToPlan(json: JSONObject): WorkoutPlan {
        val exercisesArray = json.getJSONArray("exercises")
        val exercises = List(exercisesArray.length()) { index ->
            val exerciseJson = exercisesArray.getJSONObject(index)
            PlannedExercise(
                exerciseId = exerciseJson.getInt("exerciseId"),
                exerciseName = exerciseJson.getString("exerciseName"),
                sets = exerciseJson.getInt("sets"),
                reps = exerciseJson.getInt("reps"),
                restTime = exerciseJson.getInt("restTime")
            )
        }
        
        return WorkoutPlan(
            id = json.getString("id"),
            name = json.getString("name"),
            exercises = exercises,
            createdAt = json.getLong("createdAt"),
            lastUsed = if (json.has("lastUsed") && !json.isNull("lastUsed")) 
                json.getLong("lastUsed") else null
        )
    }
}
