package com.example.revolut

import com.example.revolut.data.CurrencyResponse
import com.example.revolut.http.RatesApi
import com.example.revolut.http.RatesRepositoryImpl
import com.example.revolut.http.ResponseHandler
import com.example.revolut.http.Status
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.net.SocketTimeoutException


@RunWith(MockitoJUnitRunner::class)
class RateRepositoryTest {
    @Mock
    lateinit var ratesApi: RatesApi

    private lateinit var ratesRepositoryImpl: RatesRepositoryImpl

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        ratesRepositoryImpl = RatesRepositoryImpl(
            ResponseHandler(),
            ratesApi
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getSearchResults() = testDispatcher.runBlockingTest {
        val name = "EUR"
        val response = currencyResponse()
        given(ratesApi.getRates(name)).willReturn(response)

        val result = ratesRepositoryImpl.getRates(name)
        assertThat(result.data).isEqualTo(response)
    }

    @Test
    fun getSearchResultsError() = testDispatcher.runBlockingTest {
        val name = "name"
        given(ratesApi.getRates(name)).willAnswer {
            throw SocketTimeoutException("")
        }

        val result = ratesRepositoryImpl.getRates(name)
        assertThat(result.status).isEqualTo(Status.ERROR)
    }
}
