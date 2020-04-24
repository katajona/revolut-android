package com.example.revolut.http

import com.example.revolut.data.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApi {

    @GET("/api/android/latest")
    suspend fun getRates(@Query("base") name: String?="EUR"): CurrencyResponse
}
