package com.example.revolut

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.revolut.data.Currency
import com.example.revolut.list.ListViewModel
import com.example.revolut.list.delegate.RateDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
import org.mockito.BDDMockito.then
import org.mockito.BDDMockito.times
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ListViewModelTest {
    @Mock
    lateinit var rateDelegate: RateDelegate

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var listViewModel: ListViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    private val currency = Currency("EUR")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        listViewModel = ListViewModel(rateDelegate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun clickItem() {
        listViewModel.onItemClicked(currency)
        then(rateDelegate).should().setSelectedRate(currency)
    }

    @Test
    fun amountChanged() {
        listViewModel.amountChanged(currency)
        then(rateDelegate).should().setChangedRate(currency)
    }

    @Test
    fun getUpdates() = testDispatcher.runBlockingTest {
        listViewModel.getUpdates()
        delay(5000)
        listViewModel.pauseUpdates()
        then(rateDelegate).should(times(5)).getRatesSelected()
    }
}
