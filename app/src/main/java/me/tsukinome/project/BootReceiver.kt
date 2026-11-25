package me.tsukinome.project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        try {
            context.startForegroundService(Intent(context, TsukiWatchService::class.java))
        } catch (_: Exception) {
        }
    }
}
