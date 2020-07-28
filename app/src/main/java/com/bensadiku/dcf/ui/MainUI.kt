package com.bensadiku.dcf.ui

import android.content.Intent
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.testTag
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.ConstraintLayout
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.layout.wrapContentWidth
import androidx.ui.livedata.observeAsState
import androidx.ui.material.Button
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Settings
import androidx.ui.unit.dp
import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.activities.SettingsActivity
import com.bensadiku.dcf.util.Constants.FACT_TEXT_TEST_TAG
import com.bensadiku.dcf.util.Constants.MAIN_LOADING_TEST_TAG
import com.bensadiku.dcf.util.Constants.REQUEST_FACT_TEST_TAG
import com.bensadiku.dcf.util.Constants.SETTINGS_BUTTON_TEST_TAG
import com.bensadiku.dcf.viewmodels.MainViewModel

@Composable
fun MainApp(mainViewModel: MainViewModel) {
    MaterialTheme {
        CatFact(mainViewModel)
    }
}

@Composable
fun CatFact(mainViewModel: MainViewModel) {
    val state by mainViewModel.catFact.observeAsState(initial = "")
    if (state.isEmpty()) {
        LiveDataLoadingComponent()
    } else {
        LiveDataComponentFact(state, mainViewModel)
    }
}

@Composable
private fun LiveDataComponentFact(catFact: String, mainViewModel: MainViewModel) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (settingsRef, catFactRef, requestAnotherRef) = createRefs()
        IconButton(onClick = {
            val ctx = CatApplication.instance?.applicationContext ?: return@IconButton
            ctx.startActivity(
                Intent(ctx, SettingsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
        }, modifier = Modifier.constrainAs(settingsRef) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
        }.padding(16.dp).testTag(SETTINGS_BUTTON_TEST_TAG)) {
            Icon(asset = Icons.Filled.Settings, tint = Color.Blue)
        }
        Text(
            text = catFact,
            modifier = Modifier.testTag(FACT_TEXT_TEST_TAG).constrainAs(catFactRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }.padding(20.dp)
        )
        Button(
            onClick = { mainViewModel.getFact() },
            modifier = Modifier.testTag(REQUEST_FACT_TEST_TAG).constrainAs(requestAnotherRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(catFactRef.bottom, margin = 50.dp)
            },
        ) {
            Text(text = "Request another")
        }
    }
}


@Composable
private fun LiveDataLoadingComponent() {
    Box(
        modifier = Modifier.testTag(MAIN_LOADING_TEST_TAG).fillMaxSize(),
        gravity = ContentGravity.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.wrapContentWidth (Alignment.CenterHorizontally)
        )
    }
}