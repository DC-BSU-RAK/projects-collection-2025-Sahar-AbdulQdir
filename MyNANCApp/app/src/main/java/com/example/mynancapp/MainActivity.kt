package com.example.mynancapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mynancapp.utils.ButtonSoundPlayer
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val infoButton: ImageButton = findViewById(R.id.infoButton)
        infoButton.setOnClickListener {
            showPopup()
            ButtonSoundPlayer.play(this)
        }

        val majorSpinner: Spinner = findViewById(R.id.majorSpinner)
        val yearSpinner: Spinner = findViewById(R.id.yearSpinner)
        val semesterSpinner: Spinner = findViewById(R.id.semesterSpinner)
        val showButton: ImageButton = findViewById(R.id.showButton)

        val major = arrayOf(
            "Select major:                                                       ⌕",
            "Business and Management (HRM/MRT)",
            "Business and Management (Accounting)",
            "Creative Computing",
            "Cyber Security",
            "Creative Media",
            "Psychology"
        )

        val year = arrayOf("Select year:                                                         ⌕","YR1", "YR2", "YR3")
        val semester = arrayOf("Select semester:                                                 ⌕","S1", "S2")

        val adapterMajor = ArrayAdapter(this, R.layout.spinner_item, major)
        adapterMajor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // ✅ Fix here
        majorSpinner.adapter = adapterMajor

        val adapterYear = ArrayAdapter(this, R.layout.spinner_item, year)
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = adapterYear

        val adapterSemester = ArrayAdapter(this, R.layout.spinner_item, semester)
        adapterSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        semesterSpinner.adapter = adapterSemester


        showButton.setOnClickListener {
            ButtonSoundPlayer.play(this)
            Log.d("MainActivity", "Show button clicked")

            val selectedMajor = majorSpinner.selectedItem.toString()
            val selectedYearStr = yearSpinner.selectedItem.toString()
            val selectedSemesterStr = semesterSpinner.selectedItem.toString()

            if (selectedMajor.startsWith("Select") || selectedYearStr.startsWith("Select") || selectedSemesterStr.startsWith("Select")) {
                Toast.makeText(this, "Please select a major, year, and semester.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val selectedYear = selectedYearStr.replace("YR", "").toInt()
            val selectedSemester = selectedSemesterStr.replace("S", "").toInt()

            val contentLayout: LinearLayout = findViewById(R.id.contentLayout)
            contentLayout.removeAllViews()

            val json = assets.open("module_guide.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(json)
            val majorsArray = jsonObject.getJSONArray("majors")

            var modulesFound = false

            for (i in 0 until majorsArray.length()) {
                val majorObject = majorsArray.getJSONObject(i)
                if (majorObject.getString("name") == selectedMajor) {
                    val yearsArray = majorObject.getJSONArray("years")
                    for (j in 0 until yearsArray.length()) {
                        val yearObject = yearsArray.getJSONObject(j)
                        if (yearObject.getInt("year") == selectedYear) {
                            val semestersArray = yearObject.getJSONArray("semesters")
                            for (k in 0 until semestersArray.length()) {
                                val semesterObject = semestersArray.getJSONObject(k)
                                if (semesterObject.getInt("semester") == selectedSemester) {
                                    val modulesArray = semesterObject.getJSONArray("modules")
                                    modulesFound = modulesArray.length() > 0
                                    for (m in 0 until modulesArray.length()) {
                                        val moduleObject = modulesArray.getJSONObject(m)
                                        val cardView = CardView(this)
                                        val cardLayout = LinearLayout(this).apply {
                                            orientation = LinearLayout.VERTICAL
                                            setPadding(30, 30, 30, 30)
                                        }
                                        cardView.apply {
                                            addView(cardLayout)
                                            layoutParams = LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                            ).apply {
                                                setMargins(0, 0, 0, 40)
                                            }
                                            radius = 26f
                                            cardElevation = 12f
                                        }

                                        val titleView = TextView(this)
                                        titleView.text = moduleObject.getString("name")
                                        titleView.textSize = 24f
                                        titleView.setTextColor(Color.parseColor("#1E88E5")) // Blue shade
                                        titleView.setPadding(0, 0, 0, 15)
                                        titleView.setTypeface(null, android.graphics.Typeface.BOLD)

                                        cardLayout.addView(titleView)

                                        val infoView = TextView(this).apply {
                                            text = moduleObject.getString("information")
                                            textSize = 16f
                                            setPadding(0, 0, 0, 20)
                                        }
                                        cardLayout.addView(infoView)

                                        val toolsTitle = TextView(this)
                                        toolsTitle.text = "\uD83D\uDCBB Tools:"
                                        toolsTitle.textSize = 20f
                                        toolsTitle.setTypeface(null, android.graphics.Typeface.BOLD)
                                        toolsTitle.setTextColor(Color.parseColor("#333333"))
                                        toolsTitle.setPadding(0, 20, 0, 10)

                                        cardLayout.addView(toolsTitle)

                                        val toolsArray = moduleObject.getJSONArray("tools")
                                        val toolLayout = LinearLayout(this).apply {
                                            orientation = LinearLayout.HORIZONTAL
                                            layoutDirection = LinearLayout.LAYOUT_DIRECTION_LTR
                                        }

                                        for (t in 0 until toolsArray.length()) {
                                            val toolObject = toolsArray.getJSONObject(t)
                                            val toolWrapper = LinearLayout(this).apply {
                                                orientation = LinearLayout.VERTICAL
                                                setPadding(10, 0, 10, 0)
                                            }

                                            val toolImage = ImageView(this).apply {
                                                layoutParams = LinearLayout.LayoutParams(60.dpToPx(), 60.dpToPx()).apply {
                                                    setMargins(0, 0, 0, 5)
                                                }
                                                scaleType = ImageView.ScaleType.CENTER_CROP
                                            }
                                            Picasso.get().load(toolObject.getString("image")).into(toolImage)
                                            toolImage.setOnClickListener {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(toolObject.getString("url")))
                                                startActivity(intent)
                                            }

                                            val toolName = TextView(this).apply {
                                                text = toolObject.getString("name")
                                                textSize = 14f
                                                setTextColor(Color.DKGRAY)
                                                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                                            }

                                            toolWrapper.addView(toolImage)
                                            toolWrapper.addView(toolName)
                                            toolLayout.addView(toolWrapper)
                                        }
                                        cardLayout.addView(toolLayout)

                                        val resourcesTitle = TextView(this)
                                        resourcesTitle.text = "\uD83D\uDDD2 Resources:"
                                        resourcesTitle.textSize = 20f
                                        resourcesTitle.setTypeface(null, android.graphics.Typeface.BOLD)
                                        resourcesTitle.setTextColor(Color.parseColor("#333333"))
                                        resourcesTitle.setPadding(0, 30, 0, 10)

                                        cardLayout.addView(resourcesTitle)

                                        val resourcesArray = moduleObject.getJSONArray("resources")
                                        for (r in 0 until resourcesArray.length()) {
                                            val resourceObject = resourcesArray.getJSONObject(r)
                                            val resourceLink = TextView(this).apply {
                                                text = "\uD83D\uDD17  ${resourceObject.getString("title")}"
                                                setTextColor(Color.parseColor("#1E88E5"))
                                                textSize = 15f
                                                setPadding(0, 10, 40, 5)
                                                setOnClickListener {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resourceObject.getString("url")))
                                                    startActivity(intent)
                                                }
                                            }
                                            cardLayout.addView(resourceLink)
                                        }

                                        contentLayout.addView(cardView)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (modulesFound) {
                Toast.makeText(this, "Modules displayed successfully.", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun showPopup() {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup, null)

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


    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}