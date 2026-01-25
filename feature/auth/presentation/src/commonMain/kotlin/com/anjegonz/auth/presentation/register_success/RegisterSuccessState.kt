package com.anjegonz.auth.presentation.register_success

data class RegisterSuccessState(
    val registerEmail: String = "test@test.com",
    val isResendingVerificationEmail: Boolean = false
)