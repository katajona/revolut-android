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
import kotlinx.coroutines.launch

class ListViewModel(private val githubRepository: GithubRepository) :
    ViewModel() {

    val loading = MutableLiveData<Boolean>()
    private val rates = MutableLiveData<HashMap<String, Double>>()
    private val selectedRate = MutableLiveData<Currency>()
    private val changedRate = MutableLiveData<Currency>()
    val country = MediatorLiveData<ArrayList<Currency>>().apply {
        addSource(rates) { map ->
            val list = getUpdatedList(this.value, selectedRate.value, map)
            list?.let {
                this.postValue(list)
            }
        }
        addSource(selectedRate) { selectedRate ->
            val list = getUpdatedList(this.value, selectedRate, rates.value)
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
        changedRate: Currency? = null
    ): ArrayList<Currency>? {
        if (currentList == null || selectedRate == null || rates == null) {
            return currentList
        }
        changedRate?.let {
            val calculatedSelectedRate = changedRate.amount / (rates[changedRate.country] ?: 1.0)
            this.selectedRate.postValue(Currency(selectedRate.country, calculatedSelectedRate))
        }
        val list = ArrayList<Currency>()
        for (item in currentList) {
            val amount =
                if (item.country != selectedRate.country) {
                    (rates[item.country] ?: 0.0) * selectedRate.amount
                } else {
                    selectedRate.amount
                }
            list.add(Currency(item.country, amount))
        }
        return list
    }

    init {
        getRepositories()
    }

    private fun getRepositories() {
        loading.postValue(true)
        viewModelScope.launch {
            val result = githubRepository.getRates()
            when (result.status) {
                Status.SUCCESS -> {
                    result.data?.let { response ->
                        val selected = Currency(response.baseCurrency)
                        updateRates(response, selected)
                        createCountryList(response, selected)
                    }
                }
                Status.ERROR -> Log.d("", "")
            }
            loading.postValue(false)
        }
    }

    private fun updateRates(response: CurrencyResponse, selected: Currency) {
        rates.postValue(response.rates)
        selectedRate.postValue(selected)
    }

    private fun createCountryList(response: CurrencyResponse, selected: Currency) {
        val ratesArray =
            ArrayList(response.rates.map { Currency(it.key, it.value) })
        ratesArray.add(0, selected)
        country.postValue(ratesArray)
    }

    fun onItemClicked(currency: Currency) {
        country.value?.let {
            val list = ArrayList(it)
            list.apply {
                remove(currency)
                add(0, currency)
            }
            country.postValue(list)
        }
    }

    fun amountChanged(currency: Currency) {
        changedRate.postValue(currency)
    }
}


