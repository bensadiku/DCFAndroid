package com.bensadiku.dcf.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.state
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bensadiku.dcf.models.ThemeType
import com.bensadiku.dcf.util.Constants.APP_THEME_TEXT_TAG
import com.bensadiku.dcf.util.Constants.DARK_THEME_TAG
import com.bensadiku.dcf.util.Constants.DIV1_TAG
import com.bensadiku.dcf.util.Constants.DIV2_TAG
import com.bensadiku.dcf.util.Constants.DIV3_TAG
import com.bensadiku.dcf.util.Constants.ENABLE_PUSH_TEST_TAG
import com.bensadiku.dcf.util.Constants.ENABLE_PUSH_TEXT_TEST_TAG
import com.bensadiku.dcf.util.Constants.LIGHT_THEME_TAG
import com.bensadiku.dcf.util.Constants.PUSH_TIMER_SLIDER_TEST_TAG
import com.bensadiku.dcf.util.Constants.PUSH_TIMER_TEST_TAG
import com.bensadiku.dcf.util.Constants.PUSH_TIMER_TEXT_TEST_TAG
import com.bensadiku.dcf.util.Constants.RESET_EVERYTHING_BUTTON_TAG
import com.bensadiku.dcf.util.Constants.SYSTEM_THEME_TAG
import com.bensadiku.dcf.viewmodels.SettingsViewModel
import timber.log.Timber


@Preview
@Composable
fun SettingsApp(settingsViewModel: SettingsViewModel) {
    CatFactTheme {
        Surface(color = MaterialTheme.colors.background) {
            InitSettings(settingsViewModel = settingsViewModel)
        }
    }
}

@Preview
@Composable
private fun InitSettings(settingsViewModel: SettingsViewModel) {
    /// The `notifications enabled` state
    val notificationEnabledState = settingsViewModel.onNotificationsStateChange.observeAsState(
        initial = settingsViewModel.hasNotificationsEnabled
    )
    /// Seekbar (Slider) state
    val seekbarState =
        state(init = { settingsViewModel.getNotificationProgressSeekbar().toFloat() })

    /// Interval of the notifications state
    val timerTextState = settingsViewModel.timeSelected.observeAsState(initial = "")

    /// App theme state
    val appTheme =
        settingsViewModel.themeType.observeAsState(initial = settingsViewModel.savedThemeType)

    ConstraintLayout(
        modifier = Modifier.padding(30.dp).fillMaxSize()
    ) {
        val (firstRow, pushTimerText, pushTimerSeekbar, pushTimerDateText, resetEverythingBtn) = createRefs()
        val (nightModeText, nightModeRow) = createRefs()
        val (div1, div2, div3) = createRefs()

        Row(modifier = Modifier.constrainAs(firstRow) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        }) {
            Text(
                text = "Allow notifications:",
                modifier = Modifier.testTag(ENABLE_PUSH_TEXT_TEST_TAG)
            )
            Switch(
                checked = notificationEnabledState.value,
                modifier = Modifier.padding(start = 6.dp).testTag(ENABLE_PUSH_TEST_TAG),
                color = MaterialTheme.colors.primary,
                onCheckedChange = { isChecked ->
                    Timber.w(" allow notifications $isChecked")
                    settingsViewModel.hasNotificationsEnabled = isChecked
                }
            )
        }

        Divider(
            thickness = 1.dp,
            modifier = Modifier.constrainAs(div1) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(firstRow.bottom)
            }.padding(10.dp).testTag(DIV1_TAG)
        )
        Text(
            "Show me notifications every:",
            modifier = Modifier.constrainAs(pushTimerText) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(div1.bottom)
            }.padding(top = 10.dp).testTag(PUSH_TIMER_TEST_TAG)
        )
        Slider(
            modifier = Modifier.constrainAs(pushTimerSeekbar) {
                end.linkTo(parent.end)
                start.linkTo(parent.start)
                top.linkTo(pushTimerText.bottom)
            }.testTag(PUSH_TIMER_SLIDER_TEST_TAG),
            color = MaterialTheme.colors.primary,
            valueRange = 0f..24 * 4f,
            onValueChangeEnd = {
                settingsViewModel.saveNotificationTime()
            },
            onValueChange = { progress ->
                settingsViewModel.calculateAndShowTimer((progress + 1).toInt())
                seekbarState.value = progress
            },
            value = seekbarState.value
        )
        Text(timerTextState.value, modifier = Modifier.constrainAs(pushTimerDateText) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(pushTimerSeekbar.bottom)
        }.testTag(PUSH_TIMER_TEXT_TEST_TAG))
        Divider(thickness = 1.dp, modifier = Modifier.constrainAs(div2) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(pushTimerDateText.bottom)
        }.padding(10.dp).testTag(DIV2_TAG))
        Text(text = "App Theme", modifier = Modifier.constrainAs(nightModeText) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(div2.bottom)
        }.padding(top = 10.dp, bottom = 10.dp).testTag(APP_THEME_TEXT_TAG))
        Row(modifier = Modifier.constrainAs(nightModeRow) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(nightModeText.bottom)
        }) {
            RadioButton(
                modifier = Modifier.testTag(LIGHT_THEME_TAG),
                selected = appTheme.value == ThemeType.LIGHT,
                onClick = { settingsViewModel.savedThemeType = ThemeType.LIGHT }
            )
            Text(text = "Light")

            RadioButton(
                modifier = Modifier.testTag(DARK_THEME_TAG),
                selected = appTheme.value == ThemeType.DARK,
                onClick = { settingsViewModel.savedThemeType = ThemeType.DARK }
            )
            Text(text = "Dark")

            RadioButton(
                modifier = Modifier.testTag(SYSTEM_THEME_TAG),
                selected = appTheme.value == ThemeType.SYSTEM,
                onClick = { settingsViewModel.savedThemeType = ThemeType.SYSTEM }
            )
            Text(text = "System")
        }
        Divider(thickness = 1.dp, modifier = Modifier.constrainAs(div3) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(nightModeRow.bottom)
        }.padding(10.dp).testTag(DIV3_TAG))
        Button(
            onClick = {
                settingsViewModel.reset()
                //FIXME(BEN): Hack, don't modify state that doesn't belong to the view, this should automatically be updated by the Seekbar(Slider) itself
                seekbarState.value =
                    settingsViewModel.getNotificationProgressSeekbar().toFloat()
                settingsViewModel.savedThemeType = ThemeType.DARK
            },
            shape = CutCornerShape(2.dp),
            modifier = Modifier.constrainAs(resetEverythingBtn) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(div3.bottom)
            }.padding(top = 10.dp).testTag(RESET_EVERYTHING_BUTTON_TAG)
        ) {
            Text(text = "Reset everything?", color = MaterialTheme.colors.onPrimary)
        }
    }
}