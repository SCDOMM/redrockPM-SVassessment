package com.example.ept.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InitialViewModel : ViewModel() {
    private var _liveData= MutableLiveData<InitialState>()
    val liveData: LiveData<InitialState> get() = _liveData

}
sealed class InitialState(){

}