package com.example.multiviewsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.multiviewsapp.databinding.ActivityHomePageBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID
import androidx.appcompat.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.VideoView
import com.yariksoffice.lingver.Lingver



class HomePage : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButtonAnimator.applyToAllButtons(binding.root, this)


        val videoView = findViewById<VideoView>(R.id.bgVideoView)
        val videoPath = "android.resource://${packageName}/${R.raw.homevid}"
        videoView.setVideoPath(videoPath)
        videoView.setOnPreparedListener { it.isLooping = true }
        videoView.start()


        val username = intent.getStringExtra("USERNAME")
        val nameToShow = if (!username.isNullOrBlank()) username else "mate"
        binding.welcomeText.text = getString(R.string.welcome_message, nameToShow)


        binding.createRoomButton.setOnClickListener {
            val intent = Intent(this, QuotesPage::class.java)
            startActivity(intent)
        }

        binding.info.setOnClickListener {
            val intent = Intent(this, PopupPage::class.java)
            startActivity(intent)
        }

        // ✅ Open existing rooms
        binding.joinRoomButton.setOnClickListener {
            val intent = Intent(this, RoomsPage::class.java)
            startActivity(intent)
        }


        findViewById<ImageButton>(R.id.btn_home).setOnClickListener{
            SingleToast.show(this, "Home Button clicked", Toast.LENGTH_SHORT)
            startActivity(Intent(this, HomePage::class.java))
        }


        findViewById<ImageButton>(R.id.btn_profile).setOnClickListener {
            SingleToast.show(this, "Profile clicked", Toast.LENGTH_SHORT)
            startActivity(Intent(this, ProfilePage::class.java))
        }

        findViewById<ImageButton>(R.id.btn_settings).setOnClickListener {
            SingleToast.show(this, "Settings clicked", Toast.LENGTH_SHORT)
            startActivity(Intent(this, SettingsPage::class.java))
        }
    }

    private fun showPopup() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.activity_popup_page, null)

        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT

        val instructWindow = PopupWindow(popupView, width, height, true)

        // Make background transparent to show popup background correctly
        instructWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Show the popup at the center of the screen
        instructWindow.showAtLocation(popupView, Gravity.CENTER, 0, -10)

        val closeButton: ImageButton = popupView.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            instructWindow.dismiss()
        }
    }


    private fun showRoomIdDialog(roomId: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Room Created")
            .setMessage("Your Room ID is:\n$roomId\n(Share this with friends!)")
            .setPositiveButton("Copy") { _, _ ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Room ID", roomId)
                clipboard.setPrimaryClip(clip)
                SingleToast.show(this, "Room ID copied", Toast.LENGTH_SHORT)
            }
            .setNegativeButton("Close", null)
            .create()
        dialog.show()
    }

    //    language
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }


}
