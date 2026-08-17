package com.kuskus.n_untis

class WebUntisRepository {

    private var api: WebUntisApi? = null
    private var session: SessionInfo? = null

    suspend fun login(
        school: String,
        server: String,
        username: String,
        password: String
    ): SessionInfo {

        val newApi = WebUntisApi(
            school = school,
            server = server
        )

        val newSession =
            newApi.login(
                username,
                password
            )

        api = newApi
        session = newSession

        return newSession
    }

    fun restoreSession(
        storedSession: StoredSession
    ) {

        val newApi = WebUntisApi(
            school = storedSession.school,
            server = storedSession.server
        )

        newApi.restoreSession(
            storedSession.sessionId
        )

        api = newApi

        session = SessionInfo(
            sessionId = storedSession.sessionId,
            personType = storedSession.personType,
            personId = storedSession.personId,
            klasseId = null
        )
    }

    suspend fun timetable(
        startDate: String,
        endDate: String
    ): List<Lesson> {

        val currentApi =
            api ?: error("Nicht eingeloggt")

        val currentSession =
            session ?: error("Keine Session")

        return currentApi.getTimetable(
            currentSession,
            startDate,
            endDate
        )
    }

    suspend fun logout() {

        api?.logout()

        api = null
        session = null
    }
    suspend fun getStatusData(): String {

        val currentApi =
            api ?: error("Nicht eingeloggt")

        return currentApi.getStatusData()
    }
    suspend fun getSubjects(): String {

        val currentApi =
            api ?: error("Nicht eingeloggt")

        return currentApi.getSubjects()
    }
}