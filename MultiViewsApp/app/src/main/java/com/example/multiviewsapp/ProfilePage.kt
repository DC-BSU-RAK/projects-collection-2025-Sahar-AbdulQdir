package com.example.multiviewsapp

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.multiviewsapp.databinding.ActivityProfilePageBinding

import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class ProfilePage : AppCompatActivity() {

    private lateinit var binding: ActivityProfilePageBinding
    private val IMAGE_PICK_CODE = 1001
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButtonAnimator.applyToAllButtons(binding.root, this)

        // Set user name
        binding.userNameTextView.text = "Ali"

        // Load saved task + time data
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val completedTasks = prefs.getInt("completed_tasks", 0)
        val sessionTimeInMinutes = prefs.getLong("session_time", 0L) / 1000 / 60

        // ✅ Set up charts
        setupPieChart(completedTasks)
        setupLineChart(sessionTimeInMinutes)

        // Avatar picker
        binding.changeAvatarBtn.setOnClickListener {
            val pickImage = Intent(Intent.ACTION_PICK)
            pickImage.type = "image/*"
            startActivityForResult(pickImage, IMAGE_PICK_CODE)
        }

        findViewById<ImageButton>(R.id.backB).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            binding.avatarImageView.setImageURI(selectedImageUri)
        }
    }

    private fun setupPieChart(tasksDone: Int) {
        val pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(tasksDone.toFloat(), "Completed"))
        pieEntries.add(PieEntry(10f - tasksDone, "Remaining")) // Assuming out of 10

        val dataSet = PieDataSet(pieEntries, "Tasks").apply {
            setColors(ColorTemplate.MATERIAL_COLORS.toList())
            sliceSpace = 3f
            selectionShift = 5f
        }

        val data = PieData(dataSet).apply {
            setValueTextSize(14f)
            setValueTextColor(Color.WHITE)
        }

        binding.pieChart.apply {
            this.data = data
            description.isEnabled = false
            isRotationEnabled = true
            setUsePercentValues(true)
            setHoleColor(Color.TRANSPARENT)
            setEntryLabelColor(Color.BLACK)
            animateY(1000, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun setupLineChart(minutesSpent: Long) {
        val entries = listOf(
            Entry(0f, 0f),
            Entry(1f, 10f),
            Entry(2f, 25f),
            Entry(3f, minutesSpent.toFloat())
        )


        val dataSet = LineDataSet(entries, "Time Spent (min)").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.parseColor("#001F54")
            valueTextSize = 14f
            setDrawFilled(true)
            setDrawCircles(false)
            fillAlpha = 100

            // ✅ Set gradient
            val drawable = ContextCompat.getDrawable(this@ProfilePage, R.drawable.gradient_line_chart)
            fillDrawable = drawable
        }

        val data = LineData(dataSet)

        binding.lineChart.apply {
            this.data = data
            description.isEnabled = false
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            animateX(1000)
            invalidate()
        }
    }
}
