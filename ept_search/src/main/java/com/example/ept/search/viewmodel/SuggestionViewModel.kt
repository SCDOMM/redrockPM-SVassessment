package com.example.ept.search.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class OverlayViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData= MutableLiveData<OverlayState>()
    val liveData: LiveData<OverlayState> get() = _liveData





}
sealed class OverlayState(){


}