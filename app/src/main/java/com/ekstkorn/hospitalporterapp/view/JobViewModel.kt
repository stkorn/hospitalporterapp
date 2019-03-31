package com.ekstkorn.hospitalporterapp.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekstkorn.hospitalporterapp.*
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
import okhttp3.ResponseBody
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

    val _createJob by lazy { MutableLiveData<DataEvent<Boolean>>() }
    val createJob: LiveData<DataEvent<Boolean>> = _createJob

    val _finishJob by lazy { MutableLiveData<DataEvent<Boolean>>() }
    val finishJob: LiveData<DataEvent<Boolean>> = _finishJob

    val refreshJob by lazy { MutableLiveData<Boolean>() }

    val logout by lazy { MutableLiveData<Event<Boolean>>() }

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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onSuccess = { res ->
                            res[0].let {
                                _jobStatus.postValue(DataEvent(ViewState.SUCCESS, JobStatusView(
                                        status = if (it.isAvailable == "Y") {
                                            dataStoreRepository.jobStatus = JobStatus.AVAILABLE
                                            JobStatus.AVAILABLE
                                        } else {
                                            dataStoreRepository.jobStatus = JobStatus.BUSY
                                            JobStatus.BUSY
                                        },
                                        time = "${it.remainingTime} นาที",
                                        textColor = if (it.isAvailable == "Y") {
                                            R.color.appGreen
                                        } else {
                                            R.color.orange_one
                                        })))
                            }
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onSuccess = {
                            dataStoreRepository.jobList = it
                            _jobList.postValue(DataEvent(ViewState.SUCCESS, JobListView(it.map { res ->
                                JobView(time = res.startDateTime,
                                        name = res.patientName,
                                        building = res.jobBuildingName,
                                        jobStatus = res.status)
                            })))
                        },
                        onError = {
                            _jobList.postValue(DataEvent(ViewState.ERROR, null, ""))
                        }
                )
                .addTo(disposable)

        return jobList
    }

    fun createJob(buildingId: String, jobStatus: JobStatus, pateintName: String) {
        dataStoreRepository.createJob(buildingId = buildingId, job = jobStatus, name = pateintName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onSuccess = {
                            _createJob.postValue(DataEvent(ViewState.SUCCESS, true))
                        },
                        onError = {
                            _createJob.postValue(DataEvent(ViewState.ERROR, false))
                        }
                )
                .addTo(disposable)
    }

    fun finishJob(): LiveData<DataEvent<Boolean>> {
        dataStoreRepository.finishJob()!!
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onSuccess = {
                            _finishJob.postValue(DataEvent(ViewState.SUCCESS, true))
                        },
                        onError = {
                            _finishJob.postValue(DataEvent(ViewState.ERROR, false))
                        }
                )
                .addTo(disposable)

        return finishJob
    }

    fun logout() {
        dataStoreRepository.logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onSuccess = {
                            logout.postValue(Event(true))
                        },
                        onError = {
                            logout.postValue(Event(false))
                        }
                )
                .addTo(disposable)
    }

    fun clearData() {
        dataStoreRepository.clearDatabase()
    }

    fun clearDispoable() {
        dataStoreRepository.clearDisposable()
        disposable.clear()
    }

}