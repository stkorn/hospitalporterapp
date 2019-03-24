package com.ekstkorn.hospitalporterapp.view

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekstkorn.hospitalporterapp.DataEvent
import com.ekstkorn.hospitalporterapp.repository.DataStoreRepository
import com.ekstkorn.hospitalporterapp.room.BuildingEntity

class JobViewModel(private val dataStoreRepository: DataStoreRepository) : ViewModel() {

    private val _viewState by lazy { MutableLiveData<DataEvent<Void>>() }
    val viewState: LiveData<DataEvent<Void>> = _viewState

    lateinit var getBuildingLiveData: LiveData<DataEvent<List<BuildingEntity>>>

    @SuppressLint("CheckResult")
    fun getBuilding() : LiveData<DataEvent<List<BuildingEntity>>> {
        getBuildingLiveData = dataStoreRepository.getBuilding()
        return getBuildingLiveData
    }

    fun clearData() {
        dataStoreRepository.clearDatabase()
    }

    fun clearDispoable() {
        dataStoreRepository.clearDisposable()
    }

}