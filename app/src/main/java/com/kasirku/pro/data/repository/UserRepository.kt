package com.kasirku.pro.data.repository

import com.kasirku.pro.data.dao.UserDao
import com.kasirku.pro.data.entity.User

class UserRepository(private val userDao: UserDao) {

    fun getAllActiveUsers() = userDao.getAllActiveUsers()
    fun getAllUsers() = userDao.getAllUsers()

    suspend fun getUserById(id: String) = userDao.getUserById(id)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun insert(user: User) = userDao.insert(user)
    suspend fun update(user: User) = userDao.update(user)
    suspend fun delete(user: User) = userDao.delete(user)
    suspend fun getUnsyncedUsers() = userDao.getUnsyncedUsers()
    suspend fun markAsSynced(id: String) = userDao.markAsSynced(id)
}
