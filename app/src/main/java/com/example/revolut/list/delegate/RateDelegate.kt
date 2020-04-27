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

private const val DEFAULT_CURRENCY = "EUR"

class RateDelegateImpl(private val ratesRepository: RatesRepository) : RateDelegate {

    private val rates = MutableLiveData<HashMap<String, Double>>()
    private val changedRate = MutableLiveData<Currency>()
    private val selectedRate = MediatorLiveData<Currency>().apply {
        value = (Currency(DEFAULT_CURRENCY))
        addSource(changedRate) { changedRate ->
            updateSelectedCurrencyAmount(changedRate, rates.value)
        }
        addSource(rates) { rates ->
            updateSelectedCurrencyAmount(changedRate.value, rates)
        }
    }
    override val currencyList = MediatorLiveData<ArrayList<Currency>>().apply {
        addSource(selectedRate) { selectedRate ->
            updateList(this.value, selectedRate, rates.value)
        }
    }

    override suspend fun getRatesSelected() {
        selectedRate.value?.let {
            val result = ratesRepository.getRates(it.name)
            when (result.status) {
                Status.SUCCESS -> {
                    result.data?.let { response ->
                        createCurrencyList(response)
                        updateRates(response)
                    }
                }
                Status.ERROR -> Log.d("network", " error getRatesSelected")
            }
        }
    }

    override fun setSelectedRate(rate: Currency) {
        selectedRate.postValue(rate)
    }

    override fun setChangedRate(rate: Currency) {
        changedRate.postValue(rate)
    }

    internal fun updateRates(response: CurrencyResponse) {
        rates.postValue(response.rates)
    }

    private fun createCurrencyList(response: CurrencyResponse) {
        if (currencyList.value != null)
            return
        val ratesArray = ArrayList(response.rates.map { Currency(it.key, it.value) })
        ratesArray.add(0, selectedRate.value)
        currencyList.postValue(ratesArray)
    }

    private fun updateSelectedCurrencyAmount(
        changedRate: Currency?,
        rates: HashMap<String, Double>?
    ) {
        if (rates == null)
            return
        val selected = selectedRate.value
        if (selected != null && changedRate != null && selected != changedRate) {
            val calculatedSelectedRate = changedRate.amount / (rates[changedRate.name] ?: 1.0)
            selectedRate.postValue(Currency(selected.name, calculatedSelectedRate))
        } else {
            selectedRate.postValue(selected)
        }
    }

    private fun updateList(
        currentList: ArrayList<Currency>?,
        selectedRate: Currency?,
        rates: HashMap<String, Double>?
    ) {
        if (currentList == null || selectedRate == null || rates == null) {
            return
        }
        //If selected currency is still in the rates then it is not up to date -> only move it to the top
        val list = if (rates[selectedRate.name] != null) {
            moveSelectedFirst(currentList, selectedRate)
        } else {
            listWithMultipliedValues(currentList, selectedRate, rates)
        }
        currencyList.postValue(list)
    }

    private fun listWithMultipliedValues(
        currentList: ArrayList<Currency>,
        selectedRate: Currency,
        rates: HashMap<String, Double>
    ): ArrayList<Currency> {
        val list = ArrayList<Currency>()
        for (item in currentList) {
            if (item.name != selectedRate.name) {
                val amount = rates[item.name]?.times(selectedRate.amount) ?: 0.0
                list.add(Currency(item.name, amount))
            } else {
                list.add(0, selectedRate)
            }
        }
        return list
    }

    private fun moveSelectedFirst(
        currentList: ArrayList<Currency>,
        selectedRate: Currency
    ): ArrayList<Currency> {
        val list = ArrayList<Currency>()
        for (item in currentList) {
            if (item.name != selectedRate.name) {
                list.add(item)
            } else {
                list.add(0, item)
            }
        }
        return list
    }
}