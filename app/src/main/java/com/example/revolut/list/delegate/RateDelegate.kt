package com.example.revolut.list.delegate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.revolut.data.Currency
import com.example.revolut.data.CurrencyResponse
import com.example.revolut.http.RatesRepository
import com.example.revolut.http.Status


interface RateDelegate {
    val currencyList: LiveData<ArrayList<Currency>>
    fun setSelectedRate(rate: Currency)
    fun setChangedRate(rate: Currency)
    suspend fun getRatesSelected()
}

class RateDelegateImpl(private val ratesRepository: RatesRepository) : RateDelegate {

    private val rates = MutableLiveData<HashMap<String, Double>>()
    private val selectedRate = MutableLiveData<Currency>().apply { value = (Currency("EUR")) }
    private val changedRate = MutableLiveData<Currency>()

    override val currencyList = MediatorLiveData<ArrayList<Currency>>().apply {
        addSource(rates) { map ->
            updateList(this.value, selectedRate.value, map, changedRate.value)
        }
        addSource(selectedRate) { selectedRate ->
            updateList(this.value, selectedRate, rates.value, changedRate.value)
        }
        addSource(changedRate) { changedRate ->
            updateList(this.value, selectedRate.value, rates.value, changedRate)
        }
    }

    override suspend fun getRatesSelected() {
        selectedRate.value?.let {
            val result = ratesRepository.getRates(it.country)
            when (result.status) {
                Status.SUCCESS -> {
                    result.data?.let { response ->
                        updateRates(response)
                        createCountryList(response)
                    }
                }
                Status.ERROR -> Log.d("", "")
            }
        }
    }

    private fun updateRates(response: CurrencyResponse) {
        rates.postValue(response.rates)
    }

    private fun createCountryList(response: CurrencyResponse) {
        if (currencyList.value != null)
            return
        val ratesArray =
            ArrayList(response.rates
                .map {
                    Currency(
                        it.key,
                        it.value
                    )
                })
        ratesArray.add(0, selectedRate.value)
        currencyList.postValue(ratesArray)
    }

    private fun updateList(
        currentList: ArrayList<Currency>?,
        selectedRate: Currency?,
        rates: HashMap<String, Double>?,
        changedRate: Currency?
    ) {
        if (currentList == null || selectedRate == null || rates == null) {
            return
        }
        // If the changed rate is not the selected one then calculate the new value for the selected
        if (changedRate != null && selectedRate != changedRate) {
            val calculatedSelectedRate = changedRate.amount / (rates[changedRate.country] ?: 1.0)
            this.selectedRate.postValue(Currency(selectedRate.country, calculatedSelectedRate))
        }
        val list = ArrayList<Currency>()
        for (item in currentList) {
            if (item.country != selectedRate.country) {
                val amount = rates[item.country]?.times(selectedRate.amount) ?: item.amount
                list.add(Currency(item.country, amount))
            } else {
                list.add(0, Currency(item.country, selectedRate.amount))
            }
        }
        currencyList.postValue(list)
    }

    override fun setSelectedRate(rate: Currency) {
        // should immediately change the value so the next network call can happen with this
        selectedRate.value = rate
    }

    override fun setChangedRate(rate: Currency) {
        changedRate.postValue(rate)
    }
}