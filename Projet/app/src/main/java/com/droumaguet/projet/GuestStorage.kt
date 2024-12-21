package com.droumaguet.projet

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

private val Context.guestsStore by preferencesDataStore(name="guests");

class GuestStorage() {

    private lateinit var context: Context
    private var guestsKey = stringPreferencesKey("guests")

    constructor(context: Context) : this() {
        this.context = context
    }

    suspend fun write(guests: ArrayList<String>)
    {
        val data = guests.joinToString(separator = ",")

        this.context.guestsStore.edit { preferences ->
            preferences[guestsKey] = data
        }
    }

    suspend fun read(): ArrayList<String>
    {
        val data = context.guestsStore.data.firstOrNull()?.get(guestsKey)

        return if (data != null) {
            ArrayList(data.split(","))
        } else {
            ArrayList()
        }
    }

    suspend fun remove(guestLogin: String) {
        val guests = read()
        guests.remove(guestLogin)
        write(guests)
    }
}