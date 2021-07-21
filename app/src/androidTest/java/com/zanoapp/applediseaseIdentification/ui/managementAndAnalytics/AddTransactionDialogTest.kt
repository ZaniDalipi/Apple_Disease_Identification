package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.zanoapp.applediseaseIdentification.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class AddTransactionDialogTest {

    @Test
    fun test_dialog_input_testing() {

        //Inputs to data
        val givenInput =
            arrayListOf("Apples", 2345, 12.5, "no way", "MyName", "Ins ", "20 JAN 2021")

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

        onView(withId(R.id.transactionTypeDropDownList))
            .perform(click())
            .check(matches(isDisplayed()))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.datePickerEditText))
            .perform(click())
            .check(matches(isDisplayed()))
            .perform(closeSoftKeyboard())
            .perform(typeText(givenInput[6].toString()))
            .perform(click())

        onView(withId(R.id.saveTransactionButton))
            .perform(click())
    }
}