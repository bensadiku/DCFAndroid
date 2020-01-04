package com.bensadiku.dcf.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bensadiku.dcf.databinding.ActivityMainBinding
import com.bensadiku.dcf.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewbinding
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //viewmodel
        val model = ViewModelProviders.of(this)[MainViewModel::class.java]
        model.getAndStartFactCounter()


        binding.anotherFactView.setOnClickListener {
            model.getAndStartFactCounter()
        }

        model.catFact.observe(this, Observer { catFact ->
            binding.catFactView.text = catFact
        })

        model.hasResolvedSuccessfully.observe(this, Observer { isSuccessful ->

            if (isSuccessful) {
                binding.failedFactView.visibility = View.VISIBLE
                binding.catFactView.visibility = View.GONE
            } else {
                binding.failedFactView.visibility = View.GONE
                binding.catFactView.visibility = View.VISIBLE
            }
        })

    }
}
