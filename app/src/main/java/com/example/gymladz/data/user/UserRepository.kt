package com.example.gymladz.data.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repository for managing user authentication with DataStore
 */
class UserRepository(private val context: Context) {
    
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val CURRENT_USERNAME = stringPreferencesKey("current_username")
        private val USERS_KEY_PREFIX = "user_"
    }
    
    /**
     * Check if user is currently logged in
     */
    val isLoggedIn: Flow<Boolean> = context.userDataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }
    
    /**
     * Get current logged-in username
     */
    val currentUsername: Flow<String?> = context.userDataStore.data.map { preferences ->
        preferences[CURRENT_USERNAME]
    }
    
    /**
     * Register a new user
     * @return true if registration successful, false if username already exists
     */
    suspend fun register(username: String, password: String): Boolean {
        // Check if username already exists
        val userKey = stringPreferencesKey("${USERS_KEY_PREFIX}${username}")
        val existingPassword = context.userDataStore.data.first()[userKey]
        
        if (existingPassword != null) {
            return false // Username already exists
        }
        
        // Save new user
        context.userDataStore.edit { preferences ->
            preferences[userKey] = password
        }
        
        return true
    }
    
    /**
     * Login user
     * @return true if login successful, false if credentials invalid
     */
    suspend fun login(username: String, password: String): Boolean {
        val userKey = stringPreferencesKey("${USERS_KEY_PREFIX}${username}")
        val storedPassword = context.userDataStore.data.first()[userKey]
        
        if (storedPassword != null && storedPassword == password) {
            // Set logged in state
            context.userDataStore.edit { preferences ->
                preferences[IS_LOGGED_IN] = true
                preferences[CURRENT_USERNAME] = username
            }
            return true
        }
        
        return false
    }
    
    /**
     * Logout current user
     */
    suspend fun logout() {
        context.userDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(CURRENT_USERNAME)
        }
    }
    
    /**
     * Get current username synchronously
     */
    suspend fun getCurrentUsername(): String? {
        return context.userDataStore.data.first()[CURRENT_USERNAME]
    }
}
