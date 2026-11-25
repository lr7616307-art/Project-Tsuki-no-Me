package me.tsukinome.project

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class VideoRef(
    val uri: String,
    val name: String,
    val durationMs: Long,
    val location: String = "",
    val picked: Boolean = false,
)

class VideoLibrary(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(): List<VideoRef> {
        val raw = prefs.getString(KEY, "[]") ?: "[]"
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).mapNotNull { i ->
                val obj = arr.optJSONObject(i) ?: return@mapNotNull null
                VideoRef(
                    uri = obj.optString("uri"),
                    name = obj.optString("name", "Video"),
                    durationMs = obj.optLong("durationMs"),
                    location = obj.optString("location"),
                    picked = obj.optBoolean("picked"),
                ).takeIf { it.uri.isNotBlank() }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun replace(videos: List<VideoRef>) {
        val arr = JSONArray()
        videos.forEach { video ->
            arr.put(
                JSONObject()
                    .put("uri", video.uri)
                    .put("name", video.name)
                    .put("durationMs", video.durationMs)
                    .put("location", video.location)
                    .put("picked", video.picked),
            )
        }
        prefs.edit().putString(KEY, arr.toString()).apply()
    }

    fun remove(uri: String) {
        replace(load().filter { it.uri != uri })
    }

    companion object {
        private const val PREFS = "tsuki_library"
        private const val KEY = "videos"
    }
}
