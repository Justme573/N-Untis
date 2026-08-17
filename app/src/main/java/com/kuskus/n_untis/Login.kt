package com.kuskus.n_untis

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Intent
import android.widget.Spinner
import android.widget.ArrayAdapter

class Login : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var statusText: TextView
    private val repository = WebUntisRepository()
    private lateinit var sessionStore: SessionStore


    private lateinit var schoolSpinner: Spinner

    private val schools = listOf(
        School(
            name = "Gemeinschaftsschule Faldera",
            school = "gemsch-faldera",
            server = "gemsch-faldera.webuntis.com"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_page)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        statusText = findViewById(R.id.statusText)
        schoolSpinner = findViewById(R.id.schoolSpinner)
        sessionStore = SessionStore(this)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            schools.map { it.name }
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        schoolSpinner.adapter = adapter

        loginButton.setOnClickListener {
            login()
        }

        checkSession()
    }

    private fun login() {

        val username =
            usernameInput.text.toString()

        val password =
            passwordInput.text.toString()

        val selectedSchool =
            schools[schoolSpinner.selectedItemPosition]

        if (username.isBlank() || password.isBlank()) {

            statusText.text =
                "Bitte Benutzername und Passwort eingeben."

            return
        }

        loginButton.isEnabled = false

        statusText.text =
            "Anmeldung läuft..."

        lifecycleScope.launch {

            try {

                val session = withContext(Dispatchers.IO) {

                    repository.login(
                        school = selectedSchool.school,
                        server = selectedSchool.server,
                        username = username,
                        password = password
                    )
                }

                withContext(Dispatchers.IO) {

                    sessionStore.save(session)

                    sessionStore.saveServerInfo(
                        school = selectedSchool.school,
                        server = selectedSchool.server
                    )
                }

                startActivity(
                    Intent(
                        this@Login,
                        Timetable::class.java
                    )
                )

                finish()
            } catch (e: Exception) {

                e.printStackTrace()

                statusText.text =
                    "Login fehlgeschlagen:\n" +
                            "${e.javaClass.simpleName}\n" +
                            "${e.message ?: "Keine Fehlermeldung"}"

            } finally {

                loginButton.isEnabled = true
            }
        }
    }
    private fun checkSession() {

        lifecycleScope.launch {

            val storedSession =
                withContext(Dispatchers.IO) {
                    sessionStore.getSession()
                }

            if (storedSession != null) {

                try {

                    repository.restoreSession(
                        storedSession
                    )

                    startActivity(
                        Intent(
                            this@Login,
                            Timetable::class.java
                        )
                    )

                    finish()
                } catch (e: Exception) {

                    statusText.text =
                        "Session ungültig."

                    withContext(Dispatchers.IO) {
                        sessionStore.clear()
                    }
                }
            }
        }
    }
}