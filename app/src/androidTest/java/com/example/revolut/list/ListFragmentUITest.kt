package com.example.revolut.list

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.revolut.currencyList
import com.example.testingapp.testing.ui.with
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListFragmentUITest {
    private val list = currencyList()

    @Test
    fun testListVisible() {
        with<ListFragmentUIRobot> {
            setUpList(list)
            checkListVisible(list)
            checkTextVisible(list)
            checkEditTextVisible(list)
        }
    }

    @Test
    fun testClickItem() {
        with<ListFragmentUIRobot> {
            val position = 1
            setUpList(list)
            clickItem(position)
            verifyItemClickedCalled(position)
        }
    }

    @Test
    fun testEditItem() {
        with<ListFragmentUIRobot> {
            val position = 1
            val amount = "2"
            setUpList(list)
            editItem(position, amount)
            verifyAmountChangedCalled(position)
        }
    }
}