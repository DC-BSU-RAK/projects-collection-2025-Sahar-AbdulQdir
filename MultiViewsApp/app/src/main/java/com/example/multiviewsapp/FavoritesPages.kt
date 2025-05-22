package com.example.multiviewsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

class FavoritesPages : AppCompatActivity() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private val favoriteQuotes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites_pages)

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView)
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        favoritesRecyclerView.adapter = FavoritesAdapter(favoriteQuotes)

        loadFavorites()

        findViewById<ImageButton>(R.id.backB).setOnClickListener {
            startActivity(Intent(this, QuotesPage::class.java))
        }
    }


    private fun loadFavorites() {
        val sharedPrefs = getSharedPreferences("Favorites", MODE_PRIVATE)
        val set = sharedPrefs.getStringSet("favoriteQuotes", emptySet())
        if (!set.isNullOrEmpty()) {
            favoriteQuotes.addAll(set)
            favoritesRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "No favorites saved yet", Toast.LENGTH_SHORT).show()
        }
    }

    inner class FavoritesAdapter(private val items: List<String>) :
        RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

        // ✅ Corrected ViewHolder referencing the TextView inside item_favourite.xml
        inner class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val quoteText: TextView = itemView.findViewById(R.id.favorite_quote_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favourite, parent, false)
            return FavoritesViewHolder(view)
        }

        override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
            holder.quoteText.text = items[position]
        }

        override fun getItemCount(): Int = items.size
    }

}
