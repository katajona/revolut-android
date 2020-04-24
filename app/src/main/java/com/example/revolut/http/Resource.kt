package com.example.revolut.http

import androidx.annotation.StringRes


enum class Status {
    SUCCESS, ERROR
}

data class Resource<out T>(val status: Status, val data: T?, @StringRes val message: Int?) {

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(msg: Int, data: T?): Resource<T> {
            return Resource(
                Status.ERROR,
                data,
                msg
            )
        }
    }
}