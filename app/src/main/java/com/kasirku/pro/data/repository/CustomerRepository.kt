package com.kasirku.pro.data.repository

import com.kasirku.pro.data.dao.CustomerDao
import com.kasirku.pro.data.entity.Customer

class CustomerRepository(private val customerDao: CustomerDao) {

    fun getAllCustomers() = customerDao.getAllCustomers()
    fun searchCustomers(query: String) = customerDao.searchCustomers(query)

    suspend fun getCustomerById(id: String) = customerDao.getCustomerById(id)
    suspend fun insert(customer: Customer) = customerDao.insert(customer)
    suspend fun update(customer: Customer) = customerDao.update(customer)
    suspend fun delete(customer: Customer) = customerDao.delete(customer)
    suspend fun getUnsyncedCustomers() = customerDao.getUnsyncedCustomers()
    suspend fun markAsSynced(id: String) = customerDao.markAsSynced(id)
}
