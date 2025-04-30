package com.example.billsplitting.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.billsplitting.ui.viewmodel.BillViewModel

class BillViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
