package com.bensadiku.dcf

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithTag
import com.bensadiku.dcf.repository.CatFactApiRepository
import com.bensadiku.dcf.util.Constants.FACT_TEXT_TEST_TAG
import com.bensadiku.dcf.util.Constants.MAIN_LOADING_TEST_TAG
import com.bensadiku.dcf.util.Constants.REQUEST_FACT_TEST_TAG
import com.bensadiku.dcf.util.Constants.SETTINGS_BUTTON_TEST_TAG
import com.bensadiku.dcf.viewmodels.MainViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MainUiTest {
    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    /// TODO(BEN): Dagger inject
    var catFactApiRepository: CatFactApiRepository = CatFactApiRepository()
    /// TODO(BEN): Dagger inject
    var mainViewModel: MainViewModel = MainViewModel(catFactApiRepository = catFactApiRepository)

    @Before
    fun setUp() {
        composeTestRule.launchMainApp(mainViewModel)
    }

    @Test
    fun visibility() {
        onNodeWithTag(MAIN_LOADING_TEST_TAG).assertIsDisplayed()
        onNodeWithTag(FACT_TEXT_TEST_TAG).assertDoesNotExist()
        onNodeWithTag(REQUEST_FACT_TEST_TAG).assertDoesNotExist()
        onNodeWithTag(SETTINGS_BUTTON_TEST_TAG).assertDoesNotExist()
        mainViewModel.setFact("test")
        onNodeWithTag(MAIN_LOADING_TEST_TAG).assertDoesNotExist()
        onNodeWithTag(FACT_TEXT_TEST_TAG).assertIsDisplayed()
        onNodeWithTag(REQUEST_FACT_TEST_TAG).assertIsDisplayed()
        onNodeWithTag(SETTINGS_BUTTON_TEST_TAG).assertIsDisplayed()
    }
}