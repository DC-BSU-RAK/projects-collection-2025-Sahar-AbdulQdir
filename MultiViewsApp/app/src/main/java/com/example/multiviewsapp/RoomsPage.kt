package com.example.multiviewsapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.multiviewsapp.databinding.ActivityRoomsPageBinding
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import java.util.UUID

class RoomsPage : AppCompatActivity() {
    private lateinit var binding: ActivityRoomsPageBinding
    private lateinit var customizeRoomLauncher: ActivityResultLauncher<Intent>

    data class StudyRoom(
        val id: String,
        val name: String,
        val bgColor: String = "#FFFFFF",
        val widgetColor: String = "white",
        val fontColor: String = "black",
        val imageResName: String = ""
    )

    private val roomList = mutableListOf(
        StudyRoom("room1", "Study Café", "bgColor1_lavender", "widgetColor1_white", "fontColor1_black", "room1"),
        StudyRoom("room2", "Sea Room", "bgColor2_blue", "widgetColor2_gray", "fontColor1_black", "room2"),
        StudyRoom("room3", "Night Room", "bgColor3_green", "widgetColor1_white", "fontColor1_black", "room3"),
        StudyRoom("room4", "Nature Room", "bgColor4_dark", "widgetColor2_gray", "fontColor2_white", "room4"),
        StudyRoom("room5", "Sunset ", "bgColor4_dark", "widgetColor2_gray", "fontColor2_white", "room5"),
        StudyRoom("room6", "Beach Room", "#EEEEEE", "widgetColor1_white", "fontColor1_black", "room6"),
        StudyRoom("room7", "Night Owl", "bgColor4_dark", "widgetColor2_gray", "fontColor2_white", "room7"),
        StudyRoom("room8", "Library Room", "#EEEEEE", "widgetColor1_white", "fontColor1_black", "room8")
    )

    private val sharedPrefs by lazy { getSharedPreferences("room_prefs", MODE_PRIVATE) }
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButtonAnimator.applyToAllButtons(binding.root, this)

        customizeRoomLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val roomName = data?.getStringExtra("roomName") ?: "Custom Room"
                val bgColor = data?.getStringExtra("bgColor") ?: "#FFFFFF"
                val widgetColor = data?.getStringExtra("widgetColor") ?: "white"
                val fontColor = data?.getStringExtra("fontColor") ?: "black"
                val bgImageUri = data?.getStringExtra("bgImageUri")

                val newRoom = StudyRoom(
                    id = UUID.randomUUID().toString(),
                    name = roomName,
                    bgColor = bgImageUri ?: bgColor,
                    widgetColor = if (widgetColor == "white") "widgetColor1_white" else "widgetColor2_gray",
                    fontColor = if (fontColor == "black") "fontColor1_black" else "fontColor2_white",
                    imageResName = ""  // Custom rooms use uploaded image
                )

                sharedPrefs.edit().putString(newRoom.id, gson.toJson(newRoom)).apply()
                roomList.add(0, newRoom)
                loadRoomCards()
            }
        }

        loadSavedRoomsFromPrefs()
        loadRoomCards()


        binding.backB.setOnClickListener {
            finish()
        }
    }

    private fun loadSavedRoomsFromPrefs() {
        val allRooms = sharedPrefs.all
        for ((_, value) in allRooms) {
            try {
                val room = gson.fromJson(value as String, StudyRoom::class.java)
                if (room.name != "+ Add Custom?" && roomList.none { it.id == room.id }) {
                    roomList.add(0, room)
                }
            } catch (_: Exception) {}
        }
    }

    private fun loadRoomCards() {
        binding.roomGrid.removeAllViews()
        val inflater = LayoutInflater.from(this)

        for (room in roomList) {
            val card = inflater.inflate(R.layout.room_card, binding.roomGrid, false) as MaterialCardView
            card.strokeWidth = 0
            card.setStrokeColor(Color.TRANSPARENT)

            val roomImage = card.findViewById<ImageView>(R.id.roomImage)
            val roomNameText = card.findViewById<TextView>(R.id.roomName)

            // Load image
            if (room.bgColor.startsWith("content://")) {
                val imageUri = Uri.parse(room.bgColor)
                val inputStream = contentResolver.openInputStream(imageUri)
                val drawable = Drawable.createFromStream(inputStream, imageUri.toString())
                roomImage.setImageDrawable(drawable)
            } else {
                val resId = resources.getIdentifier(room.imageResName ?: "", "drawable", packageName)
                if (resId != 0) {
                    roomImage.setImageResource(resId)
                } else {
                    roomImage.setBackgroundColor(Color.LTGRAY)
                }
            }

            roomNameText.text = room.name

            // Card click logic
            card.setOnClickListener {
                    val intent = Intent(this, RoomEnvironment::class.java)
                    intent.putExtra("ROOM_ID", room.id)
                    intent.putExtra("ROOM_NAME", room.name)
                    intent.putExtra("BG_COLOR", room.bgColor)
                    intent.putExtra("WIDGET_COLOR", room.widgetColor)
                    intent.putExtra("FONT_COLOR", room.fontColor)
                    startActivity(intent)
            }

            binding.roomGrid.addView(card)
        }
    }

}