package com.example.revolut.data

data class CurrencyResponse(val baseCurrency: String, val rates: HashMap<String, Double>)