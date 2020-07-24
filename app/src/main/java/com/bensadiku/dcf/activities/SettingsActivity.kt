package com.bensadiku.dcf.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CutCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.*
import androidx.ui.unit.dp
import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.viewmodels.SettingsViewModel
import timber.log.Timber
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inject this activity
        (application as CatApplication).getComponent().inject(this)

        //viewmodel
        settingsViewModel =
            ViewModelProviders.of(this, viewModelFactory)[SettingsViewModel::class.java]

        setContent {
            MaterialTheme {
                InitSettings()
            }
        }
    }

    @Composable
    private fun InitSettings() {
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
                Text("Allow notifications:")
                Switch(
                    checked = notificationEnabledState.value,
                    modifier = Modifier.padding(start = 6.dp),
                    color = Color.Blue,
                    onCheckedChange = { isChecked ->
                        Timber.w(" allow notifications $isChecked")
                        settingsViewModel.hasNotificationsEnabled = isChecked
                    }
                )
            }

            Divider(thickness = 1.dp, modifier = Modifier.constrainAs(div1) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(firstRow.bottom)
            }.padding(10.dp))
            Text(
                "Show me notifications every:",
                modifier = Modifier.constrainAs(pushTimerText) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(div1.bottom)
                }.padding(top = 10.dp)
            )
            Slider(
                modifier = Modifier.constrainAs(pushTimerSeekbar) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(pushTimerText.bottom)
                },
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
            })
            Divider(thickness = 1.dp, modifier = Modifier.constrainAs(div2) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(pushTimerDateText.bottom)
            }.padding(10.dp))
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
                }.padding(top = 10.dp)
            ) {
                Text(text = "Reset everything?")
            }
        }
    }
}
