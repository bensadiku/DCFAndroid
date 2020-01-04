package com.bensadiku.dcf.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bensadiku.dcf.databinding.ActivityMainBinding
import com.bensadiku.dcf.util.Constants
import com.bensadiku.dcf.util.PushNotification
import com.bensadiku.dcf.viewmodels.MainViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewbinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //viewmodel
        mainViewModel = ViewModelProviders.of(this)[MainViewModel::class.java]


        binding.anotherFactView.setOnClickListener {
            mainViewModel.getAndStartFactCounter()
        }

        mainViewModel.catFact.observe(this, Observer { catFact ->
            binding.catFactView.text = catFact
        })

        mainViewModel.hasResolvedSuccessfully.observe(this, Observer { isSuccessful ->

            if (isSuccessful) {
                binding.failedFactView.visibility = View.VISIBLE
                binding.catFactView.visibility = View.GONE
            } else {
                binding.failedFactView.visibility = View.GONE
                binding.catFactView.visibility = View.VISIBLE
            }
        })

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
            mainViewModel.getAndStartFactCounter()
        } else {
            intent.extras?.let {
                val bodyKey = Constants.NOTIFICATION_BODY_EXTRA_KEY
                val fact = it.get(bodyKey) as String
                if (fact.isNotEmpty()) {
                    Timber.d("Intent is not null, loading it")
                    binding.catFactView.text = fact
                } else {
                    Timber.d("Intent is null, fetching from API")
                    mainViewModel.getAndStartFactCounter()
                }
            }
        }
    }
}
