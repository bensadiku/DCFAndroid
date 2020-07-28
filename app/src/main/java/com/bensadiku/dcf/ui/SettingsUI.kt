package com.bensadiku.dcf.ui

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.core.testTag
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CutCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.ConstraintLayout
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.livedata.observeAsState
import androidx.ui.material.*
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.bensadiku.dcf.util.Constants.DIV1_TAG
import com.bensadiku.dcf.util.Constants.DIV2_TAG
import com.bensadiku.dcf.util.Constants.ENABLE_PUSH_TEST_TAG
import com.bensadiku.dcf.util.Constants.ENABLE_PUSH_TEXT_TEST_TAG
import com.bensadiku.dcf.util.Constants.PUSH_TIMER_SLIDER_TEST_TAG
import com.bensadiku.dcf.util.Constants.PUSH_TIMER_TEST_TAG
import com.bensadiku.dcf.util.Constants.PUSH_TIMER_TEXT_TEST_TAG
import com.bensadiku.dcf.util.Constants.RESET_EVERYTHING_BUTTON_TAG
import com.bensadiku.dcf.viewmodels.SettingsViewModel
import timber.log.Timber


@Preview
@Composable
fun SettingsApp(settingsViewModel: SettingsViewModel) {
    MaterialTheme {
        InitSettings(settingsViewModel = settingsViewModel)
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

    ConstraintLayout(
        modifier = Modifier.padding(30.dp).fillMaxSize()
    ) {
        val (firstRow, div1, pushTimerText, pushTimerSeekbar, pushTimerDateText, div2, resetEverythingBtn) = createRefs()

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
                color = Color.Blue,
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
            color = Color.Blue,
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
        Button(
            onClick = {
                settingsViewModel.reset()
                //FIXME(BEN): Hack, don't modify state that doesn't belong to the view, this should automatically be updated by the Seekbar(Slider) itself
                seekbarState.value =
                    settingsViewModel.getNotificationProgressSeekbar().toFloat()
            },
            shape = CutCornerShape(2.dp),
            modifier = Modifier.constrainAs(resetEverythingBtn) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(div2.bottom)
            }.padding(top = 10.dp).testTag(RESET_EVERYTHING_BUTTON_TAG)
        ) {
            Text(text = "Reset everything?")
        }
    }
}