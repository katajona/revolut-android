package com.example.revolut

import com.example.revolut.data.Currency
import com.example.revolut.data.CurrencyResponse

const val increase = 2.0
val euro = Currency("EUR", 1.0)
val chf = Currency("CHF", 1.4)
val hrk = Currency("HRK", 1.9)
val euroIncreased = Currency(euro.country, euro.amount * increase)
val chfIncreased = Currency(chf.country, chf.amount * increase)
val hrkIncreased = Currency(hrk.country, hrk.amount * increase)

fun currencyResponse() = CurrencyResponse(
    rates = hashMapOf(Pair(chf.country, chf.amount), Pair(hrk.country, hrk.amount)),
    baseCurrency = euro.country
)

fun currencyList() = arrayListOf(
    euro, chf, hrk
)

fun chfFirstList() = arrayListOf(
    chf, euro, hrk
)

fun hrkIncreasedList() = arrayListOf(
    euroIncreased, chfIncreased, hrkIncreased
)