package me.tsukinome.project

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.SystemClock
import android.telecom.Call
import android.telecom.InCallService

class TsukiCallService : InCallService() {
    private val callback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            if (CallBridge.call === call) {
                if (state == Call.STATE_ACTIVE && CallBridge.connectedAt == 0L) {
                    CallBridge.connectedAt = SystemClock.elapsedRealtime()
                }
                if (state == Call.STATE_DISCONNECTED) {
                    CallBridge.connectedAt = 0L
                    CallBridge.call = null
                    cancelCallNotification()
                } else {
                    CallBridge.showCallUi(this@TsukiCallService)
                }
                CallBridge.onChanged?.invoke()
            }
        }
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CallBridge.service = this
        CallBridge.call?.unregisterCallback(callback)
        CallBridge.call = call
        CallBridge.connectedAt = 0L
        call.registerCallback(callback)
        CallBridge.dismissUnlock(this)
        postCallNotification(call)
        CallBridge.showCallUi(this)
    }

    override fun onCallRemoved(call: Call) {
        call.unregisterCallback(callback)
        if (CallBridge.call === call) {
            CallBridge.call = null
            CallBridge.connectedAt = 0L
            CallBridge.onChanged?.invoke()
            cancelCallNotification()
        }
        super.onCallRemoved(call)
    }

    override fun onDestroy() {
        CallBridge.service = null
        cancelCallNotification()
        super.onDestroy()
    }

    private fun postCallNotification(call: Call) {
        val nm = getSystemService(NotificationManager::class.java) ?: return
        if (nm.getNotificationChannel(CHANNEL) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "Calls", NotificationManager.IMPORTANCE_HIGH).apply {
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                },
            )
        }
        val open = PendingIntent.getActivity(
            this,
            2,
            Intent(this, CallActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP,
            ),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val title = call.details?.callerDisplayName?.takeIf { it.isNotBlank() }
            ?: call.details?.handle?.schemeSpecificPart
            ?: "Incoming call"
        val notification = Notification.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_moon)
            .setContentTitle(title)
            .setContentText("Answer or decline")
            .setCategory(Notification.CATEGORY_CALL)
            .setFullScreenIntent(open, true)
            .setContentIntent(open)
            .setOngoing(true)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .build()
        nm.notify(NOTIF_ID, notification)
    }

    private fun cancelCallNotification() {
        getSystemService(NotificationManager::class.java)?.cancel(NOTIF_ID)
    }

    companion object {
        private const val CHANNEL = "tsuki_call"
        private const val NOTIF_ID = 42
    }
}
