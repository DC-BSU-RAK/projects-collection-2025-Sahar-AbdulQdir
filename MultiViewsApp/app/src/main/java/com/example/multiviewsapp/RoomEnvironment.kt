package com.example.multiviewsapp

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multiviewsapp.databinding.ActivityRoomEnvironmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RoomEnvironment : AppCompatActivity() {

    private lateinit var binding: ActivityRoomEnvironmentBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isMuted = false

    private var elapsedTimeInMillis = 0L
    private var isTimerRunning = false
    private val handler = Handler()
    private lateinit var runnable: Runnable

    private var roomId: String? = null
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomEnvironmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButtonAnimator.applyToAllButtons(binding.root, this)

        // Room info
        val roomName = intent.getStringExtra("ROOM_NAME")
        roomId = intent.getStringExtra("ROOM_ID")
        binding.roomTitle.text = roomName ?: "Room"

        // Set static background
        val backgroundRes = when (roomId) {
            "room1" -> R.drawable.bgroom1
            "room2" -> R.drawable.bgroom2
            "room3" -> R.drawable.bgroom3
            "room4" -> R.drawable.bgroom4
            "room5" -> R.drawable.bgroom5
            "room6" -> R.drawable.bgroom6
            "room7" -> R.drawable.bgroom7
            "room8" -> R.drawable.bgroom8
            else -> R.color.purple_500
        }
        binding.room.setBackgroundResource(backgroundRes)

        // Room sounds
        val soundResId = when (roomId) {
            "room1" -> R.raw.sounature
            "room2" -> R.raw.sousea
            "room3" -> R.raw.sounight
            "room4" -> R.raw.sounature
            "room5" -> R.raw.soulibrary
            "room6" -> R.raw.sousea
            "room7" -> R.raw.souspace
            "room8" -> R.raw.soulibrary
            else -> null
        }

        soundResId?.let {
            mediaPlayer = MediaPlayer.create(this, it).apply {
                isLooping = true
                start()
            }
        }

        // Mute/unmute
        val muteButton = findViewById<ImageButton>(R.id.btnMuteUnmute)
        muteButton.setOnClickListener {
            mediaPlayer?.let {
                isMuted = !isMuted
                if (isMuted) {
                    it.setVolume(0f, 0f)
                    muteButton.setImageResource(R.drawable.sound)
                } else {
                    it.setVolume(1f, 1f)
                    muteButton.setImageResource(R.drawable.sound)
                }
            }
        }

        // Exit
        binding.exitButton.setOnClickListener { finish() }

        // To-do list
        val taskAdapter = TaskAdapter(taskList)
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = taskAdapter

        binding.btnAddTask.setOnClickListener {
            val taskName = binding.etTask.text.toString().trim()
            if (taskName.isNotEmpty() && taskList.none { it.name == taskName }) {
                taskList.add(Task(taskName))
                taskAdapter.notifyItemInserted(taskList.size - 1)
                binding.rvTasks.scrollToPosition(taskList.size - 1)
                binding.etTask.text.clear()

                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etTask.windowToken, 0)
            }
        }

        // Timer
        fun updateTimerText() {
            val minutes = (elapsedTimeInMillis / 1000) / 60
            val seconds = (elapsedTimeInMillis / 1000) % 60
            binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
        }

        fun startTimer() {
            runnable = object : Runnable {
                override fun run() {
                    elapsedTimeInMillis += 1000
                    updateTimerText()
                    handler.postDelayed(this, 1000)
                }
            }
            handler.post(runnable)
            isTimerRunning = true
        }

        fun pauseTimer() {
            handler.removeCallbacks(runnable)
            isTimerRunning = false
        }

        fun resetTimer() {
            pauseTimer()
            elapsedTimeInMillis = 0L
            updateTimerText()
        }

        binding.btnStartPause.setOnClickListener {
            if (isTimerRunning) pauseTimer() else startTimer()
        }

        binding.btnReset.setOnClickListener {
            resetTimer()
        }

        updateTimerText()

    }

    override fun onDestroy() {
        super.onDestroy()

        // Save data (can still be used for progress tracking)
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val completedTasks = taskList.count { it.isDone }
        prefs.edit().apply {
            putInt("completed_tasks", completedTasks)
            putLong("session_time", elapsedTimeInMillis)
            apply()
        }

        // Firebase cleanup
        val userId = FirebaseAuth.getInstance().uid
        if (userId != null && roomId != null) {
            FirebaseDatabase.getInstance().reference
                .child("rooms").child(roomId!!).child("users").child(userId).removeValue()
        }

        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
