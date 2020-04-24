package com.example.revolut.list

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.revolut.data.Currency
import com.example.revolut.data.CurrencyResponse
import com.example.revolut.http.GithubRepository
import com.example.revolut.http.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ListViewModel(private val githubRepository: GithubRepository) :
    ViewModel() {

    var updateJob: Job? = null
    private val rates = MutableLiveData<HashMap<String, Double>>()
    private val selectedRate = MutableLiveData<Currency>()
    private val changedRate = MutableLiveData<Currency>()
    val currencyList = MediatorLiveData<ArrayList<Currency>>().apply {
        addSource(rates) { map ->
            val list = getUpdatedList(this.value, selectedRate.value, map, changedRate.value)
            list?.let {
                this.postValue(list)
            }
        }
        addSource(selectedRate) { selectedRate ->
            val list = getUpdatedList(this.value, selectedRate, rates.value, changedRate.value)
            list?.let {
                this.postValue(list)
            }
        }
        addSource(changedRate) { changedRate ->
            val list = getUpdatedList(this.value, selectedRate.value, rates.value, changedRate)
            list?.let {
                this.postValue(list)
            }
        }
    }

    private fun getUpdatedList(
        currentList: ArrayList<Currency>?,
        selectedRate: Currency?,
        rates: HashMap<String, Double>?,
        changedRate: Currency?
    ): ArrayList<Currency>? {
        if (currentList == null || selectedRate == null || rates == null) {
            return currentList
        }
        // If the changed rate is not the selected one then calculate the new value for the selected
        if (changedRate != null && selectedRate != changedRate) {
            val calculatedSelectedRate = changedRate.amount / (rates[changedRate.country] ?: 1.0)
            this.selectedRate.postValue(Currency(selectedRate.country, calculatedSelectedRate))
        }
        val list = ArrayList<Currency>()
        for (item in currentList) {
            if (item.country != selectedRate.country) {
                val amount = (rates[item.country] ?: 0.0) * selectedRate.amount
                list.add(Currency(item.country, amount))
            } else {
                val amount = selectedRate.amount
                list.add(0, Currency(item.country, amount))
            }
        }
        return list
    }

    init {
        getRates()
    }

    private fun getRates(currency: Currency = Currency("EUR")) {
        viewModelScope.launch {
            val result = githubRepository.getRates(currency.country)
            when (result.status) {
                Status.SUCCESS -> {
                    result.data?.let { response ->
                        val selected = Currency(response.baseCurrency, currency.amount)
                        updateRates(response, selected)
                        createCountryList(response, selected)
                    }
                }
                Status.ERROR -> Log.d("", "")
            }
        }
    }

    private fun updateRates(response: CurrencyResponse, selected: Currency) {
        rates.postValue(response.rates)
        selectedRate.postValue(selected)
    }

    private fun createCountryList(response: CurrencyResponse, selected: Currency) {
        if (currencyList.value != null)
            return
        val ratesArray =
            ArrayList(response.rates.map { Currency(it.key, it.value) })
        ratesArray.add(0, selected)
        currencyList.postValue(ratesArray)
    }

    fun onItemClicked(currency: Currency) {
        selectedRate.postValue(currency)
        getRates(currency)
    }

    fun amountChanged(currency: Currency) {
        changedRate.postValue(currency)
    }

    fun getUpdates() {
        updateJob = viewModelScope.launch {
            while (true) {
                selectedRate.value?.let {
                    getRates(it)
                }
                delay(1000)
            }
        }
    }

    fun pauseUpdates() {
        updateJob?.cancel()
    }
}


