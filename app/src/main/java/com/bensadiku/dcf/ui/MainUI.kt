package com.bensadiku.dcf.ui

import android.content.Intent
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.activities.SettingsActivity
import com.bensadiku.dcf.util.Constants.FACT_TEXT_TEST_TAG
import com.bensadiku.dcf.util.Constants.MAIN_LOADING_TEST_TAG
import com.bensadiku.dcf.util.Constants.REQUEST_FACT_TEST_TAG
import com.bensadiku.dcf.util.Constants.SETTINGS_BUTTON_TEST_TAG
import com.bensadiku.dcf.viewmodels.MainViewModel

@Composable
fun MainApp(mainViewModel: MainViewModel) {
    CatFactTheme {
        Surface(color = MaterialTheme.colors.background) {
            CatFact(mainViewModel)
        }
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

@Preview
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
            Icon(asset = Icons.Filled.Settings, tint = MaterialTheme.colors.primary)
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
            Text(text = "Request another", color = MaterialTheme.colors.onPrimary)
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