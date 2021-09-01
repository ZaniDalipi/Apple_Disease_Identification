package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.zanoapp.applediseaseIdentification.R
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Math.random

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class AddTransactionDialogTest {

    @Test
    fun test_dialog_input_testing() {


        for (i in 0 until 100) {
            val position = (0..1).random()
            val myIncrementalNumber = (1..101).random()
            val year = 201
            val months = arrayListOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT","NOV", "DEC")
            val productTypes = arrayListOf("APPLES", "BANANAS", "OLIVE", "CORN", "PEACH", "CHERRY", "TOMATOES",
                "STRAWBERRIES", "BLUEBERRIES", "OIL")

            //Inputs to data
            val givenInput =
                arrayListOf(
                    productTypes[(0..9).random()].plus(myIncrementalNumber),
                    700 + (0..100_000).random(),
                    0.40 + random(),
                    "Transaction$myIncrementalNumber",
                    "ANY",
                    "Incomes",
                    "${(1..31).random()}/${(1..12).random()}/20${(10..22).random()}"
                )

            val launchFragment =
                launchFragmentInContainer<AddTransactionDialog>(themeResId = R.style.Theme_MyApp)
                    .moveToState(Lifecycle.State.RESUMED)

            /*add text to all fields  */
            onView(withId(R.id.productTypeEditText))
                .perform(click())
                .check(matches(isDisplayed()))
                .perform(typeText(givenInput[0].toString()))

            onView(withId(R.id.massEditText))
                .perform(click())
                .check(matches(isDisplayed()))
                .perform(typeText(givenInput[1].toString()))

            onView(withId(R.id.priceEditText))
                .perform(click())
                .check(matches(isDisplayed()))
                .perform(typeText(givenInput[2].toString()), closeSoftKeyboard())

            onView(withId(R.id.descriptionEditText))
                .perform(click())
                .check(matches(isDisplayed()))
                .perform(typeText(givenInput[3].toString()), closeSoftKeyboard())

            onView(withId(R.id.clientNameEditText))
                .perform(click())
                .check(matches(isDisplayed()))
                .perform(typeText(givenInput[4].toString()), closeSoftKeyboard())

            onView(withId(R.id.transactionTypeInputLayout))
                .perform(click())
                .check(matches(isDisplayed()))
                .perform(closeSoftKeyboard())

            onView(withId(R.id.datePickerEditText))
                .perform(click())
                .check(matches(isDisplayed()))
                .perform(closeSoftKeyboard())
                .perform(replaceText(givenInput[6].toString()))
                .perform(closeSoftKeyboard())

            onView(withId(R.id.saveTransactionButton))
                .perform(click())
        }
    }
}