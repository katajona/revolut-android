package com.example.revolut

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.testingapp.testing.ui.BaseRobot


abstract class ExtendedBaseRobot : BaseRobot() {
    fun editTextInsideRecyclerView(recyclerView: Int, position: Int, element: Int, text: String) {
        onView(ViewMatchers.withId(recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                typeText(text)
            )
        )
    }
}
