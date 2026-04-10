package au.edu.swin.passtask1_thecontest

import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private val winThreshold = 15

    private val keyScore = "score"
    private val keyHasWon = "has_won"

    private var score = 0
    private var hasWon = false

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    private var winSoundPlayer: MediaPlayer? = null

    private lateinit var scoreText: TextView
    private lateinit var buttonScore: Button
    private lateinit var buttonSteal: Button
    private lateinit var buttonReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreText = findViewById(R.id.scoreText)
        buttonScore = findViewById(R.id.buttonA)
        buttonSteal = findViewById(R.id.buttonB)
        buttonReset = findViewById(R.id.buttonC)

        // Disable system sounds
        buttonScore.isSoundEffectsEnabled = false
        buttonSteal.isSoundEffectsEnabled = false
        buttonReset.isSoundEffectsEnabled = false

        // Restore state on rotation
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(keyScore, 0)
            hasWon = savedInstanceState.getBoolean(keyHasWon, false)
            Log.d(tag, "State restored after rotation")
        }

        // Set up listeners
        buttonScore.setOnClickListener { incrementScore() }
        buttonSteal.setOnClickListener { decrementScore() }
        buttonReset.setOnClickListener { resetScore() }

        loadSounds()
        updateDisplay()
    }

    private fun incrementScore() {
        score++
        Log.d(tag, "Score incremented to $score")
        playClickSound()

        if (score > winThreshold && !hasWon) {
            hasWon = true
            Log.i(tag, "Win condition reached at score $score")
            playWinSound()
        }

        updateDisplay()
    }

    private fun decrementScore() {
        score--
        Log.d(tag, "Score decremented to $score")
        playClickSound()
        updateDisplay()
    }

    private fun resetScore() {
        score = 0
        hasWon = false
        Log.d(tag, "Score reset to 0")
        updateDisplay()
    }

    private fun updateDisplay() {
        scoreText.text = score.toString()

        // Turn green if won
        if (hasWon) {
            scoreText.setTextColor(Color.GREEN)
        } else {
            scoreText.setTextColor(Color.WHITE)
        }
    }

    private fun loadSounds() {
        try {
            val audioAttrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttrs)
                .build()

            clickSoundId = soundPool?.load(this, R.raw.click_sound, 1) ?: 0
            Log.d(tag, "Click sound loaded")
        } catch (e: Exception) {
            Log.e(tag, "Error loading click sound", e)
        }

        try {
            winSoundPlayer = MediaPlayer.create(this, R.raw.win_sound)
            winSoundPlayer?.setVolume(1.0f, 1.0f)
            Log.d(tag, "Win sound loaded")
        } catch (e: Exception) {
            Log.e(tag, "Error loading win sound", e)
        }
    }

    private fun playClickSound() {
        try {
            if (clickSoundId > 0) {
                soundPool?.play(clickSoundId, 0.3f, 0.3f, 1, 0, 1.0f)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error playing click sound", e)
        }
    }

    private fun playWinSound() {
        try {
            if (winSoundPlayer != null) {
                winSoundPlayer?.seekTo(0)
                winSoundPlayer?.start()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error playing win sound", e)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(keyScore, score)
        outState.putBoolean(keyHasWon, hasWon)
        Log.d(tag, "Game state saved")
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            winSoundPlayer?.release()
            winSoundPlayer = null
            soundPool?.release()
            soundPool = null
            Log.d(tag, "Resources released")
        } catch (e: Exception) {
            Log.e(tag, "Error releasing resources", e)
        }
    }
}