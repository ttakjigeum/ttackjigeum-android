package com.devhjs.ttackjigeum_android.core.util

sealed interface Error

sealed interface DataError : Error {
    enum class Network : DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN
    }
    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN
    }
}
