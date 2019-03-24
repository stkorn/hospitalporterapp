package com.ekstkorn.hospitalporterapp.view

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekstkorn.hospitalporterapp.DataEvent
import com.ekstkorn.hospitalporterapp.repository.DataStoreRepository

class JobViewModel(private val dataStoreRepository: DataStoreRepository) : ViewModel() {

    private val _viewState by lazy { MutableLiveData<DataEvent<Void>>() }
    val viewState: LiveData<DataEvent<Void>> = _viewState

    @SuppressLint("CheckResult")
    fun initBuilding() : LiveData<DataEvent<Void>> {
        return dataStoreRepository.initBuilding()
    }



}