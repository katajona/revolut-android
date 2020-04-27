package com.example.revolut

import com.example.revolut.data.Currency
import com.example.revolut.data.CurrencyResponse

const val increase2 = 2.0
val euro = Currency("EUR", 1.0)
val chf = Currency("CHF", 1.4)
val hrk = Currency("HRK", 1.9)
val euroDecreased2 = Currency(euro.country, euro.amount / increase2)
val chfDecreased2 = Currency(chf.country, chf.amount / increase2)
val euroIncreased2 = Currency(euro.country, euro.amount * increase2)
val chfIncreased2 = Currency(chf.country, chf.amount * increase2)
val hrkIncreased2 = Currency(hrk.country, hrk.amount * increase2)

fun currencyResponse() = CurrencyResponse(
    rates = hashMapOf(Pair(chf.country, chf.amount), Pair(hrk.country, hrk.amount)),
    baseCurrency = euro.country
)

fun currencyResponseNewRate() = CurrencyResponse(
    rates = hashMapOf(Pair(chf.country, chf.amount), Pair(hrk.country, hrkIncreased2.amount)),
    baseCurrency = euro.country
)

fun currencyList() = arrayListOf(
    euro, chf, hrk
)

fun chfFirstList() = arrayListOf(
    chf, euro, hrk
)

fun euroIncreased2List() = arrayListOf(
    euroIncreased2, chfIncreased2, hrkIncreased2
)

fun hrkIncreased2List() = arrayListOf(
    euroIncreased2, chfIncreased2, hrkIncreased2
)

fun hrkNerRateList() = arrayListOf(
    euroDecreased2, chfDecreased2, hrk
)