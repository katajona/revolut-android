package com.example.revolut

import com.example.revolut.data.Currency
import com.example.revolut.data.CurrencyResponse

const val increase2 = 2.0
val euro = Currency("EUR", 1.0)
val chf = Currency("CHF", 1.4)
val hrk = Currency("HRK", 1.9)
val euroDecreased2 = Currency(euro.name, euro.amount / increase2)
val chfDecreased2 = Currency(chf.name, chf.amount / increase2)
val euroIncreased2 = Currency(euro.name, euro.amount * increase2)
val chfIncreased2 = Currency(chf.name, chf.amount * increase2)
val hrkIncreased2 = Currency(hrk.name, hrk.amount * increase2)

fun currencyResponse() = CurrencyResponse(
    rates = hashMapOf(Pair(chf.name, chf.amount), Pair(hrk.name, hrk.amount)),
    baseCurrency = euro.name
)

fun currencyResponseNewRate() = CurrencyResponse(
    rates = hashMapOf(Pair(chf.name, chf.amount), Pair(hrk.name, hrkIncreased2.amount)),
    baseCurrency = euro.name
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