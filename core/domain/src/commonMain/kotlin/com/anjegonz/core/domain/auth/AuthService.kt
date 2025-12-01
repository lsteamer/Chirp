package com.anjegonz.core.domain.auth

import com.anjegonz.core.domain.util.DataError
import com.anjegonz.core.domain.util.EmptyResult

interface AuthService {
    suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote>
}