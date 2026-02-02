package com.anjegonz.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chirp.feature.auth.presentation.generated.resources.Res
import chirp.feature.auth.presentation.generated.resources.error_account_exists
import chirp.feature.auth.presentation.generated.resources.error_invalid_email
import chirp.feature.auth.presentation.generated.resources.error_invalid_password
import chirp.feature.auth.presentation.generated.resources.error_invalid_username
import chirp.feature.auth.presentation.generated.resources.password_hint
import com.anjegonz.core.domain.auth.AuthService
import com.anjegonz.core.domain.util.DataError
import com.anjegonz.core.domain.util.onFailure
import com.anjegonz.core.domain.util.onSuccess
import com.anjegonz.core.domain.validation.PasswordValidator
import com.anjegonz.core.presentation.util.UiText
import com.anjegonz.core.presentation.util.toUiText
import com.anjegonz.domain.EmailValidator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(RegisterState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )

    private val isEmailValidFlow = snapshotFlow { state.value.emailTextState.text.toString() }
        .map { email -> EmailValidator.validate(email) }
        .distinctUntilChanged()

    private val isUsernameValidFlow = snapshotFlow { state.value.usernameTextState.text.toString() }
        .map { username -> username.length in 3..20 }
        .distinctUntilChanged()

    private val isPasswordValidFlow = snapshotFlow { state.value.passwordTextState.text.toString() }
        .map { password -> PasswordValidator.validate(password).isValidPassword }
        .distinctUntilChanged()
        .onEach {
            highlightPasswordHint()
        }

    private val isRegisteringFlow = state
        .map { it.isRegistering }
        .distinctUntilChanged()


    private fun observeValidationStates() {
        combine(
            isEmailValidFlow,
            isUsernameValidFlow,
            isPasswordValidFlow,
            isRegisteringFlow
        ) { isEmailValid, isUsernameValid, isPasswordValid, isRegistering ->
            val allValid = isEmailValid && isUsernameValid && isPasswordValid
            _state.update {
                it.copy(
                    isEmailValid = isEmailValid,
                    isUsernameValid = isUsernameValid,
                    isPasswordValid = isPasswordValid,
                    canRegister = !isRegistering && allValid
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnLoginClick -> Unit
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }

            RegisterAction.OnInputPasswordTextFocusGain -> {
                highlightPasswordHint()
            }

            else -> {

            }
        }
    }

    private fun register() {
        if (!validateFormInputs()) {
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRegistering = true
                )
            }

            val email = state.value.emailTextState.text.toString()
            val username = state.value.usernameTextState.text.toString()
            val password = state.value.passwordTextState.text.toString()

            authService
                .register(
                    email = email,
                    username = username,
                    password = password
                )
                .onSuccess {

                    _state.update {
                        it.copy(
                            isRegistering = false
                        )
                    }
                    eventChannel.send(RegisterEvent.Success(email))
                }
                .onFailure { error ->
                    val registrationError = when (error) {
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_account_exists)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            registrationError = registrationError,
                            isRegistering = false
                        )
                    }
                }
        }
    }

    private fun clearAllTextFieldErrors() {
        _state.update {
            it.copy(
                emailError = null,
                usernameError = null,
                passwordError = null,
                registrationError = null
            )
        }
    }

    private fun highlightPasswordHint() {
        val currentState = state.value
        if (currentState.passwordTextState.text.toString().isEmpty()) {
            return
        }

        val passwordHintHighlighted = if (!currentState.isPasswordValid) {
            UiText.Resource(Res.string.password_hint)
        } else {
            null
        }
        _state.update {
            it.copy(
                passwordError = passwordHintHighlighted
            )
        }
    }

    private fun validateFormInputs(): Boolean {

        clearAllTextFieldErrors()
        val currentState = state.value
        val isEmailValid = currentState.isEmailValid
        val isPasswordValid = currentState.isPasswordValid
        val isUserNameValid = currentState.isUsernameValid

        val emailError = if (!isEmailValid) {
            UiText.Resource(Res.string.error_invalid_email)
        } else null
        val passwordError = if (!isPasswordValid) {
            UiText.Resource(Res.string.error_invalid_password)
        } else null
        val usernameError = if (!isUserNameValid) {
            UiText.Resource(Res.string.error_invalid_username)
        } else null

        _state.update {
            it.copy(
                emailError = emailError,
                usernameError = usernameError,
                passwordError = passwordError
            )
        }

        return isEmailValid && isUserNameValid && isPasswordValid
    }

}