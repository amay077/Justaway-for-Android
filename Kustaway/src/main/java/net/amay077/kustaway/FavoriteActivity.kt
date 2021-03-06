package net.amay077.kustaway

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle

import net.amay077.kustaway.task.FavoriteTask

class FavoriteActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent.getBooleanExtra("notification", false)) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancelAll()
        }
        val statusId = intent.getLongExtra("statusId", -1L)
        if (statusId > 0) {
            FavoriteTask(statusId).execute()
        }
        finish()
    }
}
