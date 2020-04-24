package com.example.revolut.http

import com.example.revolut.R
import retrofit2.HttpException
import java.net.SocketTimeoutException

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1)
}

open class ResponseHandler {
    suspend fun <T : Any> wrapResponse(operation: suspend () -> T): Resource<T> {
        return try {
            this.handleSuccess(operation())
        } catch (e: Exception) {
            this.handleException(e)
        }
    }

    private fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    private fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(
                getErrorMessage(e.code()),
                null
            )
            is SocketTimeoutException -> Resource.error(
                getErrorMessage(ErrorCodes.SocketTimeOut.code),
                null
            )
            else -> Resource.error(
                getErrorMessage(Int.MAX_VALUE),
                null
            )
        }
    }

    private fun getErrorMessage(code: Int): Int {
        return when (code) {
            ErrorCodes.SocketTimeOut.code -> R.string.timeout
            401 -> R.string.unauthorised
            404 -> R.string.not_found
            else -> R.string.unknown
        }
    }
}