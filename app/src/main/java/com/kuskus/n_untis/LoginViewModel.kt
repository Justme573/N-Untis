package com.kuskus.n_untis


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuskus.n_untis.WebUntisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginState(
    val loading: Boolean = false,
    val loggedIn: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    private val repository =
        WebUntisRepository()

    private val _state =
        MutableStateFlow(LoginState())

    val state: StateFlow<LoginState> =
        _state

    fun login(
        school: String,
        server: String,
        username: String,
        password: String
    ) {

        viewModelScope.launch {

            _state.value =
                LoginState(loading = true)

            try {

                repository.login(
                    school,
                    server,
                    username,
                    password
                )

                _state.value =
                    LoginState(
                        loggedIn = true
                    )

            } catch (e: Exception) {

                _state.value =
                    LoginState(
                        error =
                            e.message
                                ?: "Login fehlgeschlagen"
                    )
            }
        }
    }
}