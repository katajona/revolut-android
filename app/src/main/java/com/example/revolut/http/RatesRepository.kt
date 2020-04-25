package com.example.revolut.http

import com.example.revolut.data.CurrencyResponse

interface RatesRepository {
    suspend fun getRates(country: String): Resource<CurrencyResponse>
}

const val ADDRESS = "https://hiring.revolut.codes/"

class RatesRepositoryImpl(
    private val responseHandler: ResponseHandler,
    private val api: RatesApi
) : RatesRepository {

    override suspend fun getRates(country: String): Resource<CurrencyResponse> {
        return responseHandler.wrapResponse {
            api.getRates(country)
        }
    }
}