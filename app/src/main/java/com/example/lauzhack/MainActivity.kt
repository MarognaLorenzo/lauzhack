package com.example.lauzhack

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log // Import for logging
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size // Import for size
import androidx.compose.material3.Button // Import for Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment // Import for alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp // Import for size units
import androidx.compose.ui.unit.sp // Import for font size
import com.example.lauzhack.ui.theme.LauzHackTheme
import java.io.IOException

// Define a TAG for the Logcat messages
private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LauzHackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Change the content to use the new ScreenWithButton composable
                    ScreenWithButton(
                        modifier = Modifier.padding(innerPadding),
                        onPlayAudio = {
                            val audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                            playAudioFromUrl(audioUrl)
                        }
                    )
                }
            }
        }
    }

    private fun playAudioFromUrl(url: String) {
        Log.d(TAG, "In the function playAudioFromUrl, url: $url")
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                setAudioAttributes(audioAttributes)
                setDataSource(url)
                setOnPreparedListener {
                    Log.d(TAG, "MediaPlayer prepared, starting playback")
                    start()
                }
                setOnCompletionListener {
                    Log.d(TAG, "MediaPlayer playback completed")
                    it.release()
                    mediaPlayer = null
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what: $what, extra: $extra")
                    mp.release()
                    mediaPlayer = null
                    true
                }
                prepareAsync()
            } catch (e: IOException) {
                Log.e(TAG, "MediaPlayer IOException: ${e.message}")
                release()
                mediaPlayer = null
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun ScreenWithButton(modifier: Modifier = Modifier, onPlayAudio: () -> Unit) {
    // Column to center the button on the screen
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The Big Button
        Button(
            onClick = {
                // This is where the log message is written to the console (Logcat)
                Log.d(TAG, "Button clicked, playing audio.")
                onPlayAudio()
            },
            // Make the button large
            modifier = Modifier
                .fillMaxWidth(0.8f) // 80% of the screen width
                .padding(24.dp)
                .size(width = 300.dp, height = 100.dp) // Explicit size for "big" effect
        ) {
            Text(
                text = "CLICK ME NOW !",
                fontSize = 28.sp // Larger text for a big button
            )
        }
    }
}