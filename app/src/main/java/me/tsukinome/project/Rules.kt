package me.tsukinome.project

import java.util.Random

object Rules {
    fun inWindow(nowMinutes: Int, start: Int, end: Int): Boolean {
        val startClamped = start.mod(24 * 60)
        val endClamped = end.mod(24 * 60)
        if (startClamped == endClamped) return true
        return if (startClamped < endClamped) {
            nowMinutes in startClamped until endClamped
        } else {
            nowMinutes >= startClamped || nowMinutes < endClamped
        }
    }

    fun playsInLastHour(timestamps: List<Long>, now: Long): Int {
        val cutoff = now - 60L * 60L * 1000L
        return timestamps.count { it in cutoff..now }
    }

    fun allowedByFrequency(timestamps: List<Long>, now: Long, limit: Int): Boolean {
        if (limit <= 0) return false
        return playsInLastHour(timestamps, now) < limit
    }

    fun pruneLog(timestamps: List<Long>, now: Long): List<Long> {
        val cutoff = now - 60L * 60L * 1000L
        return timestamps.filter { it >= cutoff }
    }

    fun pickRandom(uris: List<String>, lastUri: String, random: Random): String? {
        if (uris.isEmpty()) return null
        val pool = if (uris.size > 1 && lastUri.isNotEmpty()) {
            uris.filter { it != lastUri }.ifEmpty { uris }
        } else {
            uris
        }
        return pool[random.nextInt(pool.size)]
    }

    fun shouldPlayOver(
        foregroundPackage: String?,
        isHome: Boolean,
        selfPackage: String,
        settings: AppSettings,
    ): Boolean {
        if (foregroundPackage == null) return false
        if (foregroundPackage == selfPackage) return false
        if (isHome) return settings.playOnHome
        return when (settings.selectionMode) {
            AppSettings.MODE_EXCEPT -> foregroundPackage !in settings.selectedPackages
            else -> foregroundPackage in settings.selectedPackages
        }
    }

    fun formatClock(minutesFromMidnight: Int): String {
        val clamped = ((minutesFromMidnight % (24 * 60)) + (24 * 60)) % (24 * 60)
        val h = clamped / 60
        val m = clamped % 60
        return "%02d:%02d".format(h, m)
    }

    fun formatDuration(ms: Long): String {
        val totalSec = (ms / 1000L).toInt().coerceAtLeast(0)
        val m = totalSec / 60
        val s = totalSec % 60
        return if (m > 0) "%d:%02d".format(m, s) else "0:%02d".format(s)
    }
}
