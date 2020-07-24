package com.bensadiku.dcf.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.lifecycle.LiveData
import androidx.ui.foundation.Text
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.ui.core.Alignment.Companion.CenterHorizontally
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Icon
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.Button
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Settings
import androidx.ui.unit.dp
import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.util.Constants.NOTIFICATION_BODY_EXTRA_KEY
import com.bensadiku.dcf.util.PushNotification
import com.bensadiku.dcf.viewmodels.MainViewModel
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //dagger
        (application as CatApplication).getComponent().inject(this)

        //viewmodel
        mainViewModel = ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]

        //compose
        setContent {
            MaterialTheme {
                CatFact(mainViewModel.catFact)
            }
        }
    }

    @Composable
    fun CatFact(fact: LiveData<String>) {
        val state by fact.observeAsState(initial = "")
        if (state.isEmpty()) {
            LiveDataLoadingComponent()
        } else {
            LiveDataComponentFact(state)
        }
    }

    @Composable
    fun LiveDataComponentFact(catFact: String) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (settingsRef, catFactRef, requestAnotherRef) = createRefs()
            IconButton(onClick = {
                startActivity(
                    Intent(
                        CatApplication.instance?.applicationContext,
                        SettingsActivity::class.java
                    )
                )
            }, modifier = Modifier.constrainAs(settingsRef) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            }.padding(16.dp)) {
                Icon(asset = Icons.Filled.Settings, tint = Color.Blue)
            }
            Text(text = catFact, modifier = Modifier.constrainAs(catFactRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }.padding(20.dp))
            Button(
                onClick = { mainViewModel.getFact() },
                modifier = Modifier.constrainAs(requestAnotherRef) {
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
    fun LiveDataLoadingComponent() {
        Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
            CircularProgressIndicator(modifier = Modifier.wrapContentWidth(CenterHorizontally))
        }
    }

    override fun onStart() {
        super.onStart()
        handleIntent(intent)
    }

    /**
     * Check if there's extra's passed in the intent, typically from the notification
     * If there are extras, show that fact in the view
     * If not = user just opened the app, load a fact from the API
     */
    private fun handleIntent(intent: Intent?) {
        PushNotification.hide(this)
        if (intent?.extras == null) {
            Timber.d("Intent is null, fetching from API")
            mainViewModel.getFact()
        } else {
            intent.extras?.let {
                val fact = it[NOTIFICATION_BODY_EXTRA_KEY] as? String
                if (!fact.isNullOrEmpty()) {
                    Timber.d("Intent is not null, loading it")
                    mainViewModel.setFact(fact)
                } else {
                    Timber.d("Intent is null, fetching from API")
                    mainViewModel.getFact()
                }
            }
        }
    }
}
