package com.ekstkorn.hospitalporterapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ekstkorn.hospitalporterapp.DataEvent
import com.ekstkorn.hospitalporterapp.JobStatus
import com.ekstkorn.hospitalporterapp.ViewState
import com.ekstkorn.hospitalporterapp.model.*
import com.ekstkorn.hospitalporterapp.module.WebServiceApi
import com.ekstkorn.hospitalporterapp.room.BuildingDao
import com.ekstkorn.hospitalporterapp.room.BuildingEntity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class DataStoreRepository(private val api: WebServiceApi, private val buildingDao: BuildingDao) {

    private val compositeDisposable by lazy { CompositeDisposable() }

    val initBuilding by lazy { MutableLiveData<DataEvent<List<BuildingEntity>>>() }

    var userId: String = ""
    var username: String = ""
    var jobStatus: JobStatus = JobStatus.AVAILABLE
    var jobId: String? = null
    var jobList: List<JobListResponse>? = null

    fun authen(user: String, pass: String) : Single<AuthResponse> {
        username = user
        val request = AuthenRequest(userName = user, password = pass)
        return api.authenUser(request)
    }

    fun getBuilding() : LiveData<DataEvent<List<BuildingEntity>>> {
        buildingDao.getBuildingCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            if (it != 0) {
                                getAllBuilding()
                            } else {
                                callBuilding()
                            }
                        }
                )
                .addTo(compositeDisposable)
        return initBuilding
    }

    private fun getAllBuilding() {
        buildingDao.getAllBuilding()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { list ->
                            list?.let { initBuilding.value = DataEvent(ViewState.SUCCESS, list) }
                        },
                        onError = {
                            it.printStackTrace()
                        }
                )
                .addTo(compositeDisposable)
    }

    private fun callBuilding() {
        Log.i("app", "call api building")
        api.getAllBuilding()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { res ->
                            Log.i("app", "Call building success add to database")
                            res?.let { list ->
                                list.map {
                                    BuildingEntity(it.buildingId, it.buildingName)
                                }.let {
                                    cacheBuildingList(it)
                                    initBuilding.value = DataEvent(ViewState.SUCCESS, it)
                                }
                            }
                        },
                        onError = {
                            initBuilding.value = DataEvent(ViewState.ERROR)
                        }
                )
                .addTo(compositeDisposable)
    }

    private fun cacheBuildingList(buildingList: List<BuildingEntity>) {
        Completable.fromAction { buildingDao.insertBuilding(buildingList) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onComplete = {
                            Log.i("app", "Insert building success")
                        },
                        onError = {
                            Log.i("app", "Error insert building")
                        }
                )
                .addTo(compositeDisposable)
    }

    fun clearDatabase() {
        Log.i("app", "Delete building success")
        Completable.fromAction { buildingDao.deleteAll() }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeBy(
                        onComplete = {
                        },
                        onError = {
                            Log.i("app", "Error delete building")
                        }
                )
                .addTo(compositeDisposable)
    }

    fun getUserProfile(userId: String): Single<UserProfileResponse> {
        return api.getUserProfile(userId)
    }

    fun clearDisposable() {
        compositeDisposable.clear()
    }

    fun getJobStatus(): Single<List<JobStatusResponse>> {
        return api.getJobStatus(userId)
    }

    fun getJobList(fromData: String, toDate: String): Single<List<JobListResponse>> {
        return api.getJobList(userId, fromData, toDate)
    }

    fun createJob(buildingId: String, job: JobStatus, name: String): Single<ResponseBody> {
        return api.createJob(CreateJobRequest(buildingId = buildingId, jobstatus = job.status,
                patientName = name, userId = userId))
    }

    fun finishJob(): Single<ResponseBody>? {
            return jobList?.let {
                 api.finishJob(CompleteJobRequest(
                        buildingId = it[0].jobBuildingId,
                        jobId = it[0].jobId,
                        jobstatus = JobStatus.COMPLETE.status,
                        patientName = it[0].patientName,
                        userId = userId
                ))
            }
    }

    fun logout(): Single<ResponseBody> {
        return api.logout(LogoutRequest(username))
    }

}
