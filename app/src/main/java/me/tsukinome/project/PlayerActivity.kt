package me.tsukinome.project

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.getStringExtra(EXTRA_URI)
        val name = intent.getStringExtra(EXTRA_NAME) ?: "Video"
        if (uri.isNullOrBlank()) {
            finish()
            return
        }
        setContentView(R.layout.activity_player)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        findViewById<TextView>(R.id.player_name).text = name
        val close = findViewById<ImageButton>(R.id.player_close)
        close.setColorFilter(Color.WHITE)
        close.setOnClickListener { finish() }
        val video = findViewById<VideoView>(R.id.player)
        val toggle = findViewById<com.google.android.material.button.MaterialButton>(R.id.player_toggle)
        val settings = SettingsStore(this).load()
        video.setOnPreparedListener { player ->
            val volume = if (settings.soundOn) 1f else 0f
            player.setVolume(volume, volume)
            video.start()
            toggle.text = "Pause"
        }
        video.setOnCompletionListener { finish() }
        video.setOnErrorListener { _, _, _ ->
            VideoLibrary(this).remove(uri)
            Toast.makeText(
                this,
                "This video is no longer on the phone. It was removed from the list.",
                Toast.LENGTH_LONG,
            ).show()
            finish()
            true
        }
        toggle.setOnClickListener {
            if (video.isPlaying) {
                video.pause()
                toggle.text = "Play"
            } else {
                video.start()
                toggle.text = "Pause"
            }
        }
        findViewById<View>(R.id.player_back).setOnClickListener {
            video.seekTo((video.currentPosition - 5_000).coerceAtLeast(0))
        }
        findViewById<View>(R.id.player_forward).setOnClickListener {
            val duration = video.duration.takeIf { it > 0 } ?: return@setOnClickListener
            video.seekTo((video.currentPosition + 5_000).coerceAtMost(duration))
        }
        try {
            video.setVideoURI(Uri.parse(uri))
        } catch (_: Exception) {
            VideoLibrary(this).remove(uri)
            Toast.makeText(this, "Could not open that video.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onDestroy() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }

    companion object {
        const val EXTRA_URI = "uri"
        const val EXTRA_NAME = "name"
    }
}
