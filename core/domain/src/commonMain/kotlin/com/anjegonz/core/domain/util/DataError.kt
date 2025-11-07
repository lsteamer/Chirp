package com.anjegonz.core.domain.util

sealed interface DataError: Error {
    enum class Remote : DataError{
        BAD_REQUEST,
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        CONFLICT,
        TOO_MAY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERVICE_UNAVAILABLE,
        SERIALIZATION,
        UNKNOWN
    }

    enum class Local: DataError{
        DISK_FULL,
        FILE_NOT_FOUND,
        UNKNOWN
    }
}