package com.example.carboncompass.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityViewModel : ViewModel() {

    private val _textArray = MutableLiveData<Array<String>>().apply {
        value = arrayOf("Individual","Organization")
    }
    val textArray: LiveData<Array<String>> = _textArray
}