package com.ekstkorn.hospitalporterapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ekstkorn.hospitalporterapp.DataEvent
import com.ekstkorn.hospitalporterapp.ViewState
import com.ekstkorn.hospitalporterapp.model.Building
import com.ekstkorn.hospitalporterapp.module.WebServiceApi
import com.ekstkorn.hospitalporterapp.room.BuildingDao
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class DataStoreRepository(private val api: WebServiceApi, private val buildingDao: BuildingDao) {

    val compositeDisposable by lazy { CompositeDisposable() }

    private val initBuilding by lazy { MutableLiveData<DataEvent<Void>>() }

    fun initBuilding() : LiveData<DataEvent<Void>> {
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
                            list?.let { initBuilding.value = DataEvent(ViewState.SUCCESS, null) }
                        },
                        onError = {
                            it.printStackTrace()
                        }
                )
                .addTo(compositeDisposable)
    }

    private fun callBuilding() {
        Log.i("app", "call api building")
//        api.getAllBuilding()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeBy(
//                        onSuccess = {
//
//                        },
//                        onError = {
//
//                        }
//                )
//                .addTo(compositeDisposable)
    }

}
