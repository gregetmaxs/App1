package com.kasirku.pro.util

import android.content.Context
import android.content.SharedPreferences
import com.kasirku.pro.data.entity.Role

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("kasirku_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_ROLE_ID = "role_id"
        private const val KEY_ROLE_NAME = "role_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_CAN_POS = "can_pos"
        private const val KEY_CAN_ITEM = "can_item"
        private const val KEY_CAN_PURCHASE = "can_purchase"
        private const val KEY_CAN_REPORT = "can_report"
        private const val KEY_CAN_CUSTOMER = "can_customer"
        private const val KEY_CAN_EMPLOYEE = "can_employee"
        private const val KEY_CAN_DELIVERY = "can_delivery"
        private const val KEY_CAN_DELIVERY_PHOTO = "can_delivery_photo"
        private const val KEY_CAN_SETTINGS = "can_settings"
        private const val KEY_CAN_MANAGE_ROLES = "can_manage_roles"
    }

    fun saveSession(userId: String, email: String, name: String, roleId: String, roleName: String, role: Role) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putString(KEY_ROLE_ID, roleId)
            putString(KEY_ROLE_NAME, roleName)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_CAN_POS, role.canAccessPos)
            putBoolean(KEY_CAN_ITEM, role.canAccessItemManagement)
            putBoolean(KEY_CAN_PURCHASE, role.canAccessPurchase)
            putBoolean(KEY_CAN_REPORT, role.canAccessReport)
            putBoolean(KEY_CAN_CUSTOMER, role.canAccessCustomer)
            putBoolean(KEY_CAN_EMPLOYEE, role.canAccessEmployee)
            putBoolean(KEY_CAN_DELIVERY, role.canAccessDelivery)
            putBoolean(KEY_CAN_DELIVERY_PHOTO, role.canTakeDeliveryPhoto)
            putBoolean(KEY_CAN_SETTINGS, role.canAccessSettings)
            putBoolean(KEY_CAN_MANAGE_ROLES, role.canManageRoles)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun getUserId(): String = prefs.getString(KEY_USER_ID, "") ?: ""
    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "") ?: ""
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""
    fun getRoleId(): String = prefs.getString(KEY_ROLE_ID, "") ?: ""
    fun getRoleName(): String = prefs.getString(KEY_ROLE_NAME, "") ?: ""

    fun canAccessPos(): Boolean = prefs.getBoolean(KEY_CAN_POS, false)
    fun canAccessItemManagement(): Boolean = prefs.getBoolean(KEY_CAN_ITEM, false)
    fun canAccessPurchase(): Boolean = prefs.getBoolean(KEY_CAN_PURCHASE, false)
    fun canAccessReport(): Boolean = prefs.getBoolean(KEY_CAN_REPORT, false)
    fun canAccessCustomer(): Boolean = prefs.getBoolean(KEY_CAN_CUSTOMER, false)
    fun canAccessEmployee(): Boolean = prefs.getBoolean(KEY_CAN_EMPLOYEE, false)
    fun canAccessDelivery(): Boolean = prefs.getBoolean(KEY_CAN_DELIVERY, false)
    fun canTakeDeliveryPhoto(): Boolean = prefs.getBoolean(KEY_CAN_DELIVERY_PHOTO, false)
    fun canAccessSettings(): Boolean = prefs.getBoolean(KEY_CAN_SETTINGS, false)
    fun canManageRoles(): Boolean = prefs.getBoolean(KEY_CAN_MANAGE_ROLES, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
