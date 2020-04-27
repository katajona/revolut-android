package com.example.revolut

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Locale

class FormatterTest {
    private fun setLocalDE(){
        Locale.setDefault(Locale("de", "DE"))
    }
    private fun setLocalEN(){
        Locale.setDefault(Locale("en", "EN"))
    }

    @Test
    fun doubleWithZeros() {
        val amount = 2.0
        val expected = "2"
        assertThat(amount.toFormattedString()).isEqualTo(expected)
    }

    @Test
    fun doubleWithOneZeroEn() {
        setLocalEN()
        val amount = 2.2
        val expected = "2.2"
        assertThat(amount.toFormattedString()).isEqualTo(expected)
    }

    @Test
    fun doubleWithOneZeroDE() {
        setLocalDE()
        val amount = 2.2
        val expected = "2,2"
        assertThat(amount.toFormattedString()).isEqualTo(expected)
    }

    @Test
    fun doubleShortEn() {
        setLocalEN()
        val amount = 2.24
        val expected = "2.24"
        assertThat(amount.toFormattedString()).isEqualTo(expected)
    }

    @Test
    fun doubleShortDE() {
        setLocalDE()
        val amount = 2.24
        val expected = "2,24"
        assertThat(amount.toFormattedString()).isEqualTo(expected)
    }

    @Test
    fun doubleLongEn() {
        setLocalEN()
        val amount = 2000.24
        val expected = "2,000.24"
        assertThat(amount.toFormattedString()).isEqualTo(expected)
    }

    @Test
    fun doubleLongDE() {
        setLocalDE()
        val amount = 2000.24
        val expected = "2.000,24"
        assertThat(amount.toFormattedString()).isEqualTo(expected)
    }

    @Test
    fun stringToDoubleEN() {
        setLocalEN()
        val expected = 2000.24
        val amount = "2,000.24"
        assertThat(amount.toFormattedDouble()).isEqualTo(expected)
    }

    @Test
    fun stringToDoubleDE() {
        setLocalDE()
        val expected = 2000.24
        val amount = "2.000,24"
        assertThat(amount.toFormattedDouble()).isEqualTo(expected)
    }

    @Test
    fun stringToDoubleAvoidingSeparatorEN() {
        setLocalEN()
        val expected = 2000.24
        val amount = "200,0.24"
        assertThat(amount.toFormattedDouble()).isEqualTo(expected)
    }

    @Test
    fun stringToDoubleAvoidingSeparatorDE() {
        setLocalDE()
        val expected = 2000.24
        val amount = "200.0,24"
        assertThat(amount.toFormattedDouble()).isEqualTo(expected)
    }

    @Test
    fun stringToDoubleError() {
        val expected = 0.0
        val amount = ""
        assertThat(amount.toFormattedDouble()).isEqualTo(expected)
    }

    @Test
    fun stringToDoubleErrorChar() {
        val expected = 0.0
        val amount = "drf"
        assertThat(amount.toFormattedDouble()).isEqualTo(expected)
    }
}