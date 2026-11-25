package me.tsukinome.project

import android.os.Bundle
import android.telecom.TelecomManager
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dial)
        val number = findViewById<EditText>(R.id.dial_number)
        val incoming = intent?.data?.schemeSpecificPart
        if (!incoming.isNullOrBlank()) number.setText(incoming)
        val keys = listOf(
            R.id.key_1 to "1", R.id.key_2 to "2", R.id.key_3 to "3",
            R.id.key_4 to "4", R.id.key_5 to "5", R.id.key_6 to "6",
            R.id.key_7 to "7", R.id.key_8 to "8", R.id.key_9 to "9",
            R.id.key_star to "*", R.id.key_0 to "0", R.id.key_hash to "#",
        )
        keys.forEach { (id, digit) ->
            findViewById<View>(id).setOnClickListener {
                number.append(digit)
            }
        }
        findViewById<View>(R.id.key_delete).setOnClickListener {
            val text = number.text?.toString().orEmpty()
            if (text.isNotEmpty()) number.setText(text.dropLast(1))
            number.setSelection(number.text?.length ?: 0)
        }
        findViewById<View>(R.id.key_call).setOnClickListener {
            val raw = number.text?.toString().orEmpty().trim()
            if (raw.isEmpty()) {
                Toast.makeText(this, "Enter a number.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val telecom = getSystemService(TelecomManager::class.java)
                telecom.placeCall(android.net.Uri.fromParts("tel", raw, null), Bundle())
            } catch (_: SecurityException) {
                Toast.makeText(this, "Phone permission is needed to place a call.", Toast.LENGTH_LONG).show()
            } catch (_: Exception) {
                Toast.makeText(this, "Could not place that call.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
