package com.kasirku.pro.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kasirku.pro.data.entity.Role

@Dao
interface RoleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(role: Role)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(roles: List<Role>)

    @Update
    suspend fun update(role: Role)

    @Delete
    suspend fun delete(role: Role)

    @Query("SELECT * FROM roles ORDER BY name ASC")
    fun getAllRoles(): LiveData<List<Role>>

    @Query("SELECT * FROM roles ORDER BY name ASC")
    suspend fun getAllRolesList(): List<Role>

    @Query("SELECT * FROM roles WHERE id = :id")
    suspend fun getRoleById(id: String): Role?

    @Query("SELECT COUNT(*) FROM roles")
    suspend fun getRoleCount(): Int

    @Query("SELECT * FROM roles WHERE isSynced = 0")
    suspend fun getUnsyncedRoles(): List<Role>

    @Query("UPDATE roles SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
