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
import androidx.ui.core.tag
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
import com.bensadiku.dcf.util.Constants
import com.bensadiku.dcf.util.PushNotification
import com.bensadiku.dcf.viewmodels.MainViewModel
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private val requestTag = "requestTag"
    private val settingsTag = "settingsTag"
    private val factTag = "factTag"
    private val constraintSet by lazy {
        ConstraintSet {
            val showFactConstraint = tag(factTag).apply {
                left constrainTo parent.left
                top constrainTo parent.top
                right constrainTo parent.right
                bottom constrainTo parent.bottom
            }
            tag(requestTag).apply {
                left constrainTo parent.left
                right constrainTo parent.right
                top constrainTo showFactConstraint.bottom
                top.margin = 50.dp
            }
            tag(settingsTag).apply {
                top constrainTo parent.top
                right constrainTo parent.right
            }
        }
    }

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
        ConstraintLayout(constraintSet = constraintSet, modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = {
                startActivity(Intent(this, SettingsActivity::class.java))
            }, modifier = Modifier.tag(settingsTag).padding(16.dp)) {
                Icon(asset = Icons.Filled.Settings, tint = Color.Blue)
            }
            Text(text = catFact, modifier = Modifier.tag(factTag).padding(20.dp))
            Button(
                onClick = { mainViewModel.getFact() },
                modifier = Modifier.tag(requestTag),
                text = { Text(text = "Request another") })
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
    private fun handleIntent(intent: Intent) {
        PushNotification.hide(this)
        if (intent.extras == null) {
            Timber.d("Intent is null, fetching from API")
            mainViewModel.getFact()
        } else {
            intent.extras?.let {
                val bodyKey = Constants.NOTIFICATION_BODY_EXTRA_KEY
                val fact = it.get(bodyKey) as String
                if (fact.isNotEmpty()) {
                    Timber.d("Intent is not null, loading it")
                    LiveDataComponentFact(fact)
                } else {
                    Timber.d("Intent is null, fetching from API")
                    mainViewModel.getFact()
                }
            }
        }
    }
}
