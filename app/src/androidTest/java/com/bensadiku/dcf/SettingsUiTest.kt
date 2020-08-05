package com.bensadiku.dcf

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.ui.test.*
import com.bensadiku.dcf.util.Constants.DARK_THEME_TAG
import com.bensadiku.dcf.util.Constants.DIV1_TAG
import com.bensadiku.dcf.util.Constants.DIV2_TAG
import com.bensadiku.dcf.util.Constants.ENABLE_PUSH_TEST_TAG
import com.bensadiku.dcf.util.Constants.ENABLE_PUSH_TEXT_TEST_TAG
import com.bensadiku.dcf.util.Constants.LIGHT_THEME_TAG
import com.bensadiku.dcf.util.Constants.PUSH_TIMER_SLIDER_TEST_TAG
import com.bensadiku.dcf.util.Constants.PUSH_TIMER_TEST_TAG
import com.bensadiku.dcf.util.Constants.PUSH_TIMER_TEXT_TEST_TAG
import com.bensadiku.dcf.util.Constants.RESET_EVERYTHING_BUTTON_TAG
import com.bensadiku.dcf.util.Constants.SYSTEM_THEME_TAG
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class SettingsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    @Before
    fun setUp() {
        composeTestRule.launchSettingsApp()
    }

    @Test
    fun package_name() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.bensadiku.dcf", appContext.packageName)
    }

    @Test
    fun visibility() {
        onNodeWithTag(ENABLE_PUSH_TEST_TAG).assertIsDisplayed()
        onNodeWithTag(ENABLE_PUSH_TEXT_TEST_TAG).assertIsDisplayed()
        onNodeWithTag(DIV1_TAG).assertIsDisplayed()
        onNodeWithTag(PUSH_TIMER_TEST_TAG).assertIsDisplayed()
        onNodeWithTag(PUSH_TIMER_SLIDER_TEST_TAG).assertIsDisplayed()
        onNodeWithTag(PUSH_TIMER_TEXT_TEST_TAG).assertIsDisplayed()
        onNodeWithTag(DIV2_TAG).assertIsDisplayed()
        onNodeWithTag(RESET_EVERYTHING_BUTTON_TAG).assertIsDisplayed()
    }


    @Test
    fun reset_everything() {
        onNodeWithTag(RESET_EVERYTHING_BUTTON_TAG).performClick()
        onNodeWithTag(ENABLE_PUSH_TEST_TAG).assertIsEnabled()
        onNodeWithTag(PUSH_TIMER_TEXT_TEST_TAG).assertTextEquals("12 hours.")
        onNodeWithTag(PUSH_TIMER_SLIDER_TEST_TAG).assertValueEquals("50 percent")
        onNodeWithTag(DARK_THEME_TAG).assertIsSelected()
        onNodeWithTag(SYSTEM_THEME_TAG).assertIsNotSelected()
        onNodeWithTag(LIGHT_THEME_TAG).assertIsNotSelected()
    }
}