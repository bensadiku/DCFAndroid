package com.bensadiku.dcf.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.core.tag
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CutCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.ConstraintLayout
import androidx.ui.layout.ConstraintSet
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.livedata.observeAsState
import androidx.ui.material.*
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.viewmodels.SettingsViewModel
import timber.log.Timber
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel
    private val notificationTag = "showNotificationTextTag"
    private val notificationSwitchTag = "showNotificationSwitchTag"
    private val div1Tag = "divider1Tag"
    private val notificationTimerTag = "showNotificationTimerTextTag"
    private val notificationTimerSeekbarTag = "showNotificationTimerSeekbarTag"
    private val notificationTimeTextTag = "showNotificationTimeTextTag"
    private val div2Tag = "divider2Tag"
    private val resetEverythingBtnTag = "resetEverythingBtnTag"

    ///TODO:(BEN) tidy this
    private val constraintSet by lazy {
        ConstraintSet {
            val showNotificationText = tag(notificationTag).apply {
                top constrainTo parent.top
                left constrainTo parent.left
            }
            tag(notificationSwitchTag).apply {
                right constrainTo parent.right
                top constrainTo parent.top
            }
            val div1 = tag(div1Tag).apply {
                right constrainTo parent.right
                left constrainTo parent.left
                top constrainTo showNotificationText.bottom
            }
            val showNotificationTimerText = tag(notificationTimerTag).apply {
                right constrainTo parent.right
                left constrainTo parent.left
                top constrainTo div1.bottom
            }
            val notificationTimerSeekbar = tag(notificationTimerSeekbarTag).apply {
                right constrainTo parent.right
                left constrainTo parent.left
                top constrainTo showNotificationTimerText.bottom
            }
            val notificationTimeText = tag(notificationTimeTextTag).apply {
                right constrainTo parent.right
                left constrainTo parent.left
                top constrainTo notificationTimerSeekbar.bottom
            }
            val div2 = tag(div2Tag).apply {
                right constrainTo parent.right
                left constrainTo parent.left
                top constrainTo notificationTimeText.bottom
            }
            tag(resetEverythingBtnTag).apply {
                right constrainTo parent.right
                left constrainTo parent.left
                top constrainTo div2.bottom
            }
        }
    }

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

    @Preview
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
            constraintSet = constraintSet,
            modifier = Modifier.padding(30.dp).fillMaxSize()
        ) {
            Text("Allow notifications:", modifier = Modifier.tag(notificationTag))
            Switch(
                checked = notificationEnabledState.value,
                modifier = Modifier.tag(notificationSwitchTag),
                color = Color.Blue,
                onCheckedChange = { isChecked ->
                    Timber.w(" allow notifications $isChecked")
                    settingsViewModel.hasNotificationsEnabled = isChecked
                }
            )
            Divider(thickness = 1.dp, modifier = Modifier.tag(div1Tag).padding(10.dp))
            Text(
                "Show me notifications every:",
                modifier = Modifier.tag(notificationTimerTag).padding(top = 10.dp)
            )
            Slider(
                modifier = Modifier.tag(notificationTimerSeekbarTag),
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
            Text(timerTextState.value, modifier = Modifier.tag(notificationTimeTextTag))
            Divider(thickness = 1.dp, modifier = Modifier.tag(div2Tag).padding(10.dp))
            Button(
                onClick = {
                    settingsViewModel.reset()
                    //FIXME(BEN): Hack, don't modify state that doesn't belong to the view, this should automatically be updated by the Seekbar(Slider) itself
                    seekbarState.value =
                        settingsViewModel.getNotificationProgressSeekbar().toFloat()
                },
                shape = CutCornerShape(2.dp),
                modifier = Modifier.tag(resetEverythingBtnTag).padding(top = 10.dp)
            ) {
                Text(text = "Reset everything?")
            }
        }
    }
}
