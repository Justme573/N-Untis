package com.kuskus.n_untis

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.sessionDataStore by preferencesDataStore(
    name = "untis_session"
)

class SessionStore(
    private val context: Context
) {

    companion object {
        private val SESSION_ID =
            stringPreferencesKey("session_id")

        private val SCHOOL =
            stringPreferencesKey("school")

        private val SERVER =
            stringPreferencesKey("server")

        private val PERSON_ID =
            stringPreferencesKey("person_id")

        private val PERSON_TYPE =
            stringPreferencesKey("person_type")
    }

    suspend fun save(session: SessionInfo) {

        context.sessionDataStore.edit { preferences ->

            preferences[SESSION_ID] =
                session.sessionId

            preferences[PERSON_ID] =
                session.personId.toString()

            preferences[PERSON_TYPE] =
                session.personType.toString()
        }
    }

    suspend fun saveServerInfo(
        school: String,
        server: String
    ) {

        context.sessionDataStore.edit { preferences ->

            preferences[SCHOOL] = school
            preferences[SERVER] = server
        }
    }

    suspend fun getSession(): StoredSession? {

        val preferences =
            context.sessionDataStore.data.first()

        val sessionId =
            preferences[SESSION_ID]
                ?: return null

        val school =
            preferences[SCHOOL]
                ?: return null

        val server =
            preferences[SERVER]
                ?: return null

        val personId =
            preferences[PERSON_ID]
                ?.toIntOrNull()
                ?: return null

        val personType =
            preferences[PERSON_TYPE]
                ?.toIntOrNull()
                ?: return null

        return StoredSession(
            sessionId = sessionId,
            school = school,
            server = server,
            personId = personId,
            personType = personType
        )
    }

    suspend fun clear() {

        context.sessionDataStore.edit {
            it.clear()
        }
    }
}

data class StoredSession(
    val sessionId: String,
    val school: String,
    val server: String,
    val personId: Int,
    val personType: Int
)