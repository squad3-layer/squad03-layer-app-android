package com.example.services.storage

interface PreferencesService {
    fun getInt(key: String, defaultValue: Int = 0): Int
    fun getString(key: String, defaultValue: String? = null): String?
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun getLong(key: String, defaultValue: Long = 0L): Long
    fun getFloat(key: String, defaultValue: Float = 0f): Float

    fun putInt(key: String, value: Int)
    fun putString(key: String, value: String?)
    fun putBoolean(key: String, value: Boolean)
    fun putLong(key: String, value: Long)
    fun putFloat(key: String, value: Float)

    fun remove(key: String)
    fun clear()
    fun contains(key: String): Boolean
}
