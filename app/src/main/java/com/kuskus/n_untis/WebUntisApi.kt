package com.kuskus.n_untis

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.UUID

class WebUntisApi(
    private val school: String,
    private val server: String
) {

    private val client = OkHttpClient()
    private val gson = Gson()

    private var sessionId: String? = null

    private val url =
        "https://$server/WebUntis/jsonrpc.do?school=$school"

    suspend fun login(
        username: String,
        password: String
    ): SessionInfo {

        val params = AuthenticateParams(
            user = username,
            password = password
        )

        val request = JsonRpcRequest(
            id = UUID.randomUUID().toString(),
            method = "authenticate",
            params = params
        )

        val response = post(request)
        println("LOGIN RESPONSE:")
        println(response)
        val result =
            gson.fromJson(
                response,
                AuthenticateResponse::class.java
            )

        if (result.error != null) {
            throw IOException(
                result.error.message
                    ?: "Login fehlgeschlagen"
            )
        }

        val session =
            result.result
                ?: throw IOException(
                    "Keine Session erhalten"
                )

        sessionId = session.sessionId

        return session
    }
    fun restoreSession(sessionId: String) {
        this.sessionId = sessionId
    }

    suspend fun getTimetable(
        session: SessionInfo,
        startDate: String,
        endDate: String
    ): List<Lesson> {

        val currentSession =
            sessionId
                ?: throw IOException(
                    "Nicht eingeloggt"
                )

        val params = TimetableParams(
            options = TimetableOptions(
                id = System.currentTimeMillis().toInt(),
                element = TimetableElement(
                    id = session.personId,
                    type = session.personType
                ),
                startDate = startDate,
                endDate = endDate
            )
        )

        val request = JsonRpcRequest(
            id = UUID.randomUUID().toString(),
            method = "getTimetable",
            params = params
        )

        val response =
            post(
                request,
                currentSession
            )
        println("TIMETABLE RESPONSE:")
        println(response)
        val result =
            gson.fromJson(
                response,
                TimetableResponse::class.java
            )

        if (result.error != null) {
            throw IOException(
                result.error.message
                    ?: "Stundenplan konnte nicht geladen werden"
            )
        }

        return result.result ?: emptyList()
    }
    suspend fun getStatusData(): String {

        if (sessionId == null) {
            throw IOException("Nicht eingeloggt")
        }

        val request = JsonRpcRequest(
            id = UUID.randomUUID().toString(),
            method = "getStatusData",
            params = emptyMap<String, Any>()
        )

        return post(request)
    }

    suspend fun logout() {

        val currentSession =
            sessionId ?: return

        val request = JsonRpcRequest(
            id = UUID.randomUUID().toString(),
            method = "logout",
            params = emptyMap<String, Any>()
        )

        try {
            post(
                request,
                currentSession
            )
        } finally {
            sessionId = null
        }
    }

    private suspend fun post(
        requestData: JsonRpcRequest,
        session: String? = sessionId
    ): String {

        val json =
            gson.toJson(requestData)

        val body =
            json.toRequestBody(
                "application/json".toMediaType()
            )

        val builder =
            Request.Builder()
                .url(url)
                .post(body)
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .addHeader(
                    "Accept",
                    "application/json"
                )

        if (session != null) {
            builder.addHeader(
                "Cookie",
                "JSESSIONID=$session"
            )
        }

        return client
            .newCall(builder.build())
            .execute()
            .use { response ->

                if (!response.isSuccessful) {
                    throw IOException(
                        "HTTP ${response.code}"
                    )
                }

                response.body?.string()
                    ?: throw IOException(
                        "Leere Antwort"
                    )
            }
    }
    suspend fun getSubjects(): String {

        if (sessionId == null) {
            throw IOException("Nicht eingeloggt")
        }

        val request = JsonRpcRequest(
            id = UUID.randomUUID().toString(),
            method = "getSubjects",
            params = emptyMap<String, Any>()
        )

        return post(request)
    }
}