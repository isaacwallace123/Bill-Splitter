package com.example.billsplitting.ui.viewmodel

import Bill
import UserPayment
import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.billsplitting.functionality.scheduleBillReminder
import kotlinx.coroutines.launch

class BillViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).billDao()

    var bills by mutableStateOf<List<Bill>>(emptyList())
        private set

    init {
        Log.d("BillViewModel", "Initialized successfully")
        loadBills()
    }

    fun loadBills() {
        viewModelScope.launch {
            bills = dao.getAllBills()
        }
    }

    fun addBillWithPayments(bill: Bill, payments: List<UserPayment>) {
        viewModelScope.launch {
            val billId = dao.insertBill(bill).toInt()

            val updatedPayments = payments.map {
                it.copy(billId = billId)
            }

            dao.insertPayments(updatedPayments)
            loadBills()

            scheduleBillReminder(getApplication())
        }
    }

    suspend fun getBillById(billId: Int): Bill? {
        val bill = dao.getBillWithPaymentsById(billId) ?: return null
        val payments = dao.getPaymentsForBill(billId)

        val response_mdeol = bill.copy()

        response_mdeol.payments = payments

        return response_mdeol
    }


    fun togglePayment(billId: Int, index: Int) {
        viewModelScope.launch {
            val bill = dao.getBillWithPaymentsById(billId) ?: return@launch
            val payments = bill.payments.toMutableList()
            payments[index] = payments[index].copy(isPaid = !payments[index].isPaid)
            dao.updatePayment(payments[index])
            loadBills()
        }
    }

    fun updatePayments(billId: Int, updatedPayments: List<UserPayment>) {
        viewModelScope.launch {
            updatedPayments.forEach { dao.updatePayment(it) }
            loadBills()
        }
    }

    fun deleteBill(bill: Bill) {
        viewModelScope.launch {
            dao.deletePaymentsByBillId(bill.id)
            dao.deleteBill(bill)
            loadBills()
        }
    }

}