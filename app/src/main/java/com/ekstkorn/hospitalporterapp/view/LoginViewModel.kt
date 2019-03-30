package com.ekstkorn.hospitalporterapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekstkorn.hospitalporterapp.DataEvent
import com.ekstkorn.hospitalporterapp.Event
import com.ekstkorn.hospitalporterapp.ViewState
import com.ekstkorn.hospitalporterapp.repository.DataStoreRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val dataStoreRepository: DataStoreRepository) : ViewModel() {

    val compositeDisposable = CompositeDisposable()

    private val _viewState by lazy { MutableLiveData<DataEvent<Void>>() }
    val viewState: LiveData<DataEvent<Void>> = _viewState

    private val _loginSuccess by lazy { MutableLiveData<Event<String>>() }
    val loginSucces: LiveData<Event<String>> = _loginSuccess

    fun login(user: String, pass: String) {
        dataStoreRepository.authen(user, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _viewState.postValue(DataEvent(ViewState.LOADING)) }
                .subscribeBy(
                        onSuccess = { res ->
                            res?.let { _loginSuccess.postValue(Event(it.userId)) }
                        },
                        onError = {
                            _viewState.postValue(DataEvent(ViewState.ERROR))
                        }
                )
                .addTo(compositeDisposable)
    }

    fun clearData() {
        dataStoreRepository.clearDatabase()
    }
}