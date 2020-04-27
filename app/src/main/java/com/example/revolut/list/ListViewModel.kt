package com.example.revolut.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.revolut.OpenForTest
import com.example.revolut.data.Currency
import com.example.revolut.list.delegate.RateDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal const val SECOND = 1000L

@OpenForTest
class ListViewModel(rateDelegate: RateDelegate) : ViewModel(),
    RateDelegate by rateDelegate {

    var updateJob: Job? = null

    fun onItemClicked(currency: Currency) {
        setSelectedRate(currency)
    }

    fun amountChanged(currency: Currency) {
        setChangedRate(currency)
    }

    fun getUpdates() {
        updateJob = viewModelScope.launch {
            while (true) {
                getRatesSelected()
                delay(SECOND)
            }
        }
    }

    fun pauseUpdates() {
        updateJob?.cancel()
    }
}


