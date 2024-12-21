package com.droumaguet.projet

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope

class Messager {
    private val context: Activity

    constructor(context: Activity)
    {
        this.context = context
    }

    fun display(message: String)
    {
        context.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}