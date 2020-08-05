package com.bensadiku.dcf.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.ui.MainApp
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
            MainApp(mainViewModel)
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
