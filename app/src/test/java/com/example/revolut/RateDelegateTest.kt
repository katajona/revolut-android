package com.example.revolut

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.revolut.http.RatesRepository
import com.example.revolut.http.Resource
import com.example.revolut.list.delegate.RateDelegateImpl
import com.google.common.truth.Truth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RateDelegateTest {
    @Mock
    lateinit var ratesRepository: RatesRepository

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var rateDelegate: RateDelegateImpl

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        rateDelegate = RateDelegateImpl(ratesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getUpdates() = testDispatcher.runBlockingTest {
        BDDMockito.given(ratesRepository.getRates("EUR")).willReturn(
            Resource.success(
                currencyResponse()
            )
        )
        rateDelegate.getRatesSelected()
        assertThat(rateDelegate.currencyList.value).isEqualTo(currencyList())
    }

    @Test
    fun setSelectedRate() {
        rateDelegate.updateRates(currencyResponse())
        rateDelegate.currencyList.postValue(currencyList())
        var data = LiveDataTestUtil.getValue(rateDelegate.currencyList)
        assertThat(data).isEqualTo(currencyList())

        rateDelegate.setSelectedRate(chf)
        data = LiveDataTestUtil.getValue(rateDelegate.currencyList)
        assertThat(data).isEqualTo(chfFirstList())
    }

    @Test
    fun setChangedRate() {
        rateDelegate.updateRates(currencyResponse())
        rateDelegate.currencyList.postValue(currencyList())
        var data = LiveDataTestUtil.getValue(rateDelegate.currencyList)
        assertThat(data).isEqualTo(currencyList())

        rateDelegate.setChangedRate(hrkIncreased2)
        data = LiveDataTestUtil.getValue(rateDelegate.currencyList)
        assertThat(data).isEqualTo(hrkIncreased2List())
    }

    @Test
    fun setChangedRateSelected() {
        rateDelegate.setSelectedRate(euro)
        rateDelegate.updateRates(currencyResponse())
        rateDelegate.currencyList.postValue(currencyList())
        var data = LiveDataTestUtil.getValue(rateDelegate.currencyList)
        assertThat(data).isEqualTo(currencyList())

        rateDelegate.setChangedRate(euroIncreased2)
        data = LiveDataTestUtil.getValue(rateDelegate.currencyList)
        assertThat(data).isEqualTo(euroIncreased2List())
    }

    @Test
    fun setChangedRateAfterRateChange() {
        rateDelegate.updateRates(currencyResponse())
        rateDelegate.currencyList.postValue(currencyList())
        var data = LiveDataTestUtil.getValue(rateDelegate.currencyList)
        assertThat(data).isEqualTo(currencyList())

        rateDelegate.setChangedRate(hrk)
        data = LiveDataTestUtil.getValue(rateDelegate.currencyList)
        assertThat(data).isEqualTo(currencyList())

        rateDelegate.updateRates(currencyResponseNewRate())
        data = LiveDataTestUtil.getValue(rateDelegate.currencyList)
        assertThat(data).isEqualTo(hrkNerRateList())
    }
}
