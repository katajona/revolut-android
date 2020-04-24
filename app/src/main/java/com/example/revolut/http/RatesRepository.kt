package com.example.revolut.http

import com.example.revolut.data.CurrencyResponse

interface GithubRepository {
    suspend fun getRates(): Resource<CurrencyResponse>
}

class GithubRepositoryImpl(
    private val responseHandler: ResponseHandler,
    private val api: RatesApi
) : GithubRepository {

    override suspend fun getRates(): Resource<CurrencyResponse> {
        return responseHandler.wrapResponse {
            api.getRates()
        }
    }
}