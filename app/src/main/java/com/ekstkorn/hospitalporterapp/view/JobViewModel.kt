package com.ekstkorn.hospitalporterapp.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekstkorn.hospitalporterapp.DataEvent
import com.ekstkorn.hospitalporterapp.JobStatus
import com.ekstkorn.hospitalporterapp.R
import com.ekstkorn.hospitalporterapp.ViewState
import com.ekstkorn.hospitalporterapp.repository.DataStoreRepository
import com.ekstkorn.hospitalporterapp.room.BuildingEntity
import com.ekstkorn.hospitalporterapp.view.model.JobListView
import com.ekstkorn.hospitalporterapp.view.model.JobStatusView
import com.ekstkorn.hospitalporterapp.view.model.JobView
import com.ekstkorn.hospitalporterapp.view.model.UserProfile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class JobViewModel(private val dataStoreRepository: DataStoreRepository) : ViewModel() {

    val disposable by lazy { CompositeDisposable() }

    private val _viewState by lazy { MutableLiveData<DataEvent<Void>>() }
    val viewState: LiveData<DataEvent<Void>> = _viewState

    lateinit var getBuildingLiveData: LiveData<DataEvent<List<BuildingEntity>>>

    private val _userProfile by lazy { MutableLiveData<DataEvent<UserProfile>>() }
    val userProfile: LiveData<DataEvent<UserProfile>> = _userProfile

    private val _jobStatus by lazy { MutableLiveData<DataEvent<JobStatusView>>() }
    val jobStatus: LiveData<DataEvent<JobStatusView>> = _jobStatus

    private val _jobList by lazy { MutableLiveData<DataEvent<JobListView>>() }
    val jobList: LiveData<DataEvent<JobListView>> = _jobList

    fun getDataRepository() = dataStoreRepository

    @SuppressLint("CheckResult")
    fun getBuilding() : LiveData<DataEvent<List<BuildingEntity>>> {
        getBuildingLiveData = dataStoreRepository.getBuilding()
        return getBuildingLiveData
    }

    fun getUserProfile(userId: String) : LiveData<DataEvent<UserProfile>> {
        dataStoreRepository.userId = userId
        dataStoreRepository.getUserProfile(userId)
                .doOnSubscribe { _userProfile.postValue(DataEvent(ViewState.LOADING)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            _userProfile.postValue(DataEvent(ViewState.SUCCESS, UserProfile(
                                    imageProfileUrl = it.urlProfile,
                                    profileName = "${it.firstName} ${it.lastName}",
                                    mobile = "โทร. ${it.mobile}",
                                    userId = it.userId
                            )))

                        },
                        onError = {
                            _userProfile.postValue(DataEvent(ViewState.ERROR, null, "ไม่สามารถดึงข้อมูลได้ กรุณา Login ใหม่อีกครั้ง"))
                        }
                )
                .addTo(disposable)

        return userProfile
    }

    fun getJobStatusAvailable(): LiveData<DataEvent<JobStatusView>> {
        dataStoreRepository.getJobStatus()
                .doOnSubscribe { _jobStatus.postValue(DataEvent(ViewState.LOADING)) }
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            _jobStatus.postValue(DataEvent(ViewState.SUCCESS, JobStatusView(
                                    status = if (it.isAvailable == "Y") {
                                        dataStoreRepository.jobStatus = JobStatus.AVAILABLE
                                        JobStatus.AVAILABLE
                                    } else {
                                        dataStoreRepository.jobStatus = JobStatus.BUSY
                                        JobStatus.BUSY
                                    },
                                    time = it.remainingTime,
                                    textColor = if (it.isAvailable == "Y") {
                                        R.color.textGreen
                                    } else {
                                        R.color.orange_one
                                    })))
                        },
                        onError = {
                            _jobStatus.postValue(DataEvent(ViewState.ERROR, null, ""))
                        }
                )
                .addTo(disposable)

        return jobStatus
    }

    fun getJobHistoryList(): LiveData<DataEvent<JobListView>> {
        val dateFormat = SimpleDateFormat("YYYYMMdd", Locale.US)
        val date = Calendar.getInstance(Locale.US)
        val toDate = dateFormat.format(date.time)
        date.add(Calendar.DATE, -5)
        val startDate = dateFormat.format(date.time)
        Log.i("day", "start date: $startDate")
        Log.i("day", "to date: $toDate")
        dataStoreRepository.getJobList(fromData = startDate, toDate = toDate)
                .doOnSubscribe { _jobList.postValue(DataEvent(ViewState.LOADING)) }
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            _jobList.postValue(DataEvent(ViewState.SUCCESS, JobListView(it.jobList.map { res ->
                                JobView(time = res.startDateTime,
                                        name = res.patientName,
                                        building = res.jobBuildingName)
                            })))
                        },
                        onError = {
                            _jobList.postValue(DataEvent(ViewState.ERROR, null, ""))
                        }
                )
                .addTo(disposable)

        return jobList
    }

    fun clearData() {
        dataStoreRepository.clearDatabase()
    }

    fun clearDispoable() {
        dataStoreRepository.clearDisposable()
        disposable.clear()
    }

}