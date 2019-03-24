package com.ekstkorn.hospitalporterapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ekstkorn.hospitalporterapp.view.JobViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val jobViewModel by viewModel<JobViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialData()

        jobViewModel.apply {
            viewState.observe(this@MainActivity, Observer {
                when (it.viewState) {

                    ViewState.LOADING -> {}
                    ViewState.SUCCESS -> Toast.makeText(this@MainActivity, "Load building success", Toast.LENGTH_SHORT).show()
                    ViewState.ERROR -> {}
                }
            })
        }

        imageButtonAdd.setOnClickListener {
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            val dialogFragment = AddPatientDialog()
            dialogFragment.show(ft, "dialog")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        jobViewModel.clearData()
        jobViewModel.clearDispoable()
        jobViewModel.viewState.removeObservers(this)
        jobViewModel.getBuildingLiveData.removeObservers(this)
    }

    private fun initialData() {
        jobViewModel.getBuilding().observe(this, Observer {
            it?.viewState?.let { state ->
                when (state) {
                    ViewState.LOADING -> { Log.i("app", "Loading building") }
                    ViewState.SUCCESS -> { Log.i("app", "Save building success") }
                    ViewState.ERROR -> { Log.i("app", "Error save building") }
                }
            }
        })
    }
}
