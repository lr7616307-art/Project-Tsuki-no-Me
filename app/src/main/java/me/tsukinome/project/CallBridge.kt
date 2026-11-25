package me.tsukinome.project

import android.content.Context
import android.content.Intent
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.VideoProfile

object CallBridge {
    @Volatile
    var service: TsukiCallService? = null

    @Volatile
    var call: Call? = null

    @Volatile
    var connectedAt: Long = 0L

    var onChanged: (() -> Unit)? = null

    fun isInUse(): Boolean = call != null

    fun answer() {
        call?.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    fun decline() {
        val active = call ?: return
        active.reject(false, null)
    }

    fun hangup() {
        call?.disconnect()
    }

    fun toggleMute() {
        val inCall = service ?: return
        val muted = inCall.callAudioState?.isMuted == true
        inCall.setMuted(!muted)
        onChanged?.invoke()
    }

    fun toggleSpeaker() {
        val inCall = service ?: return
        val route = inCall.callAudioState?.route
        val next = if (route == CallAudioState.ROUTE_SPEAKER) {
            CallAudioState.ROUTE_WIRED_OR_EARPIECE
        } else {
            CallAudioState.ROUTE_SPEAKER
        }
        inCall.setAudioRoute(next)
        onChanged?.invoke()
    }

    fun speakerOn(): Boolean = service?.callAudioState?.route == CallAudioState.ROUTE_SPEAKER

    fun muted(): Boolean = service?.callAudioState?.isMuted == true

    fun showCallUi(context: Context) {
        val intent = Intent(context, CallActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                Intent.FLAG_ACTIVITY_CLEAR_TOP,
        )
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    fun dismissUnlock(context: Context) {
        try {
            context.sendBroadcast(
                Intent(TsukiWatchService.ACTION_DISMISS).setPackage(context.packageName),
            )
        } catch (_: Exception) {
        }
    }
}
