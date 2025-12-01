package com.anjegonz.core.data.auth

import com.anjegonz.core.data.dto.requests.RegisterRequest
import com.anjegonz.core.data.networking.post
import com.anjegonz.core.domain.auth.AuthService
import com.anjegonz.core.domain.util.DataError
import com.anjegonz.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class KtorAuthService(
    private val httpClient: HttpClient
): AuthService{
    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/register",
            body = RegisterRequest(
                email = email,
                username = username,
                password = password
            )
        )
    }
}