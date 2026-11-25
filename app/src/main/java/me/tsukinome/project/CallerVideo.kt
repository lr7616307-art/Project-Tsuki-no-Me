package me.tsukinome.project

import java.util.Random

object CallerVideo {
    private val random = Random()
    @Volatile
    private var lastUri: String = ""

    fun choose(context: android.content.Context): VideoRef? {
        val settings = SettingsStore(context).load()
        if (!settings.callerVideoEnabled) return null
        val videos = VideoLibrary(context).load()
        if (videos.isEmpty()) return null
        if (settings.callerVideoMode == AppSettings.MODE_SINGLE) {
            return videos.firstOrNull { it.uri == settings.callerVideoUri }
        }
        val uri = Rules.pickRandom(videos.map { it.uri }, lastUri, random) ?: return null
        lastUri = uri
        return videos.firstOrNull { it.uri == uri }
    }
}
