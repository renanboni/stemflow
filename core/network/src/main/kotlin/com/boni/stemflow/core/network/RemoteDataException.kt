package com.boni.stemflow.core.network

sealed class RemoteDataException(message: String) : RuntimeException(message) {
    class NotFound(message: String) : RemoteDataException(message)
}
