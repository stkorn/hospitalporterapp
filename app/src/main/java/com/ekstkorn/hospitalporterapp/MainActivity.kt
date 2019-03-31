package com.ekstkorn.hospitalporterapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekstkorn.hospitalporterapp.view.JobViewModel
import com.ekstkorn.hospitalporterapp.view.OnClose
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), OnClose {
    override fun finishJob() {
        jobViewModel.getJobHistoryList()
        jobViewModel.getJobStatusAvailable()
    }

    override fun successCreateJob() {
        jobViewModel.getJobHistoryList()
        jobViewModel.getJobStatusAvailable()
    }

    private val jobViewModel by viewModel<JobViewModel>()

    private var jobAdapter = JobAdapter(emptyList())

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
            if (jobViewModel.getDataRepository().jobStatus == JobStatus.AVAILABLE) {
                createJob()
            } else {
                jobViewModel.getDataRepository().jobList?.let {
                    val ft = supportFragmentManager.beginTransaction()
                    val prev = supportFragmentManager.findFragmentByTag("dialog")
                    if (prev != null) {
                        ft.remove(prev)
                    }
                    ft.addToBackStack(null)
                    val dialogFragment = FinishTaskDialog()
                    val job = it[0]
                    val bundle = Bundle()
                    bundle.putString("jobId", job.jobId)
                    bundle.putString("name", job.patientName)
                    bundle.putString("buildingId", job.jobBuildingName)
                    dialogFragment.arguments = bundle
                    dialogFragment.show(ft, "dialog")
                }

            }
        }

        listViewJob.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = jobAdapter
        }

        imageViewLogout.setOnClickListener {
            jobViewModel.logout.observe(this, EventObserver {
                if (it) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })
            jobViewModel.logout()
        }

    }

    private fun createJob() {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = AddPatientDialog()
        dialogFragment.show(ft, "dialog")
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

        //Get user profile
        val userId = intent.getStringExtra(USERID_EXTRA)
        jobViewModel.getUserProfile(userId).observe(this, Observer {
            when (it.viewState) {

                ViewState.LOADING -> {}
                ViewState.SUCCESS -> {
                    it?.data?.let { profile ->
                        textViewName.text = profile.profileName
                        textViewID.text = profile.userId
                        textViewContact.text = profile.mobile
                    }
                }
                ViewState.ERROR -> {}
            }
        })

        jobViewModel.getJobStatusAvailable().observe(this, Observer {
            when (it.viewState) {

                ViewState.LOADING -> {}
                ViewState.SUCCESS -> {
                    it?.data?.let { view ->
                        labelStatus.setTextColor(ContextCompat.getColor(this, view.textColor))
                        textViewStatus.setTextColor(ContextCompat.getColor(this, view.textColor))
                        textViewPatientName.setTextColor(ContextCompat.getColor(this, view.textColor))
                        textViewStatus.text = if (view.status == JobStatus.AVAILABLE) {
                            getString(R.string.available_status)
                        } else {
                            getString(R.string.busy_status)
                        }
                        if (view.status == JobStatus.BUSY) {
                            textViewPeriodTime.text = view.time
                            textViewPeriodTime.visibility = View.VISIBLE
                        } else {
                            textViewPeriodTime.visibility = View.INVISIBLE
                        }

                        if (view.status == JobStatus.AVAILABLE) {
                            imageButtonAdd.setImageResource(R.drawable.ic_add_circle_green)
                            textViewPatientName.text = getString(R.string.text_button_create_job)
                        } else {
                            imageButtonAdd.setImageResource(R.drawable.ic_swap_vertical_circle)
                            textViewPatientName.text = getString(R.string.text_button_complete_job)
                        }
                    }
                }
                ViewState.ERROR -> {}
            }
        })

        jobViewModel.getJobHistoryList().observe(this, Observer {
            when (it.viewState) {

                ViewState.LOADING -> {}
                ViewState.SUCCESS -> {
                    it?.data?.let { list ->
                        jobAdapter.jobList = list.list
                        jobAdapter.notifyDataSetChanged()
                    }
                }
                ViewState.ERROR -> {}
            }
        })

        jobViewModel.refreshJob.observe(this, Observer {
            if (it) {
                jobViewModel.getJobHistoryList()
            }
        })

    }
}
