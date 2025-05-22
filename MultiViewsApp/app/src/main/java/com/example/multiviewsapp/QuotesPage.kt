package com.example.multiviewsapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class QuotesPage : AppCompatActivity() {

    private lateinit var quoteRecyclerView: RecyclerView
    private val quoteList = mutableListOf<Pair<String, String>>() // Pair<quote, author>
    private lateinit var adapter: QuoteAdapter
    private lateinit var spinner: Spinner
    private lateinit var loadQuotesBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes_page)

        quoteRecyclerView = findViewById(R.id.quoteRecyclerView)
        adapter = QuoteAdapter(quoteList) { quote, author ->
            saveToFavorites(quote, author)
        }

        quoteRecyclerView.layoutManager = LinearLayoutManager(this)
        quoteRecyclerView.adapter = adapter

        findViewById<ImageButton>(R.id.fav).setOnClickListener {
            Toast.makeText(this, "Favorites clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, FavoritesPages::class.java))
        }


        loadQuotesBtn = findViewById(R.id.loadQuotesBtn)

        loadQuotesBtn.setOnClickListener {
            quoteList.clear()
            adapter.notifyDataSetChanged()
            loadMultipleQuotes()
        }

        findViewById<ImageButton>(R.id.backB).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }

        // Load quotes automatically on page open
        loadMultipleQuotes()
    }


    private fun loadMultipleQuotes(count: Int = 10) {
        val queue = Volley.newRequestQueue(this)

        val url = "https://zenquotes.io/api/quotes"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = org.json.JSONArray(response.toString())


                    // Limit to 'count' quotes
                    for (i in 0 until minOf(count, jsonArray.length())) {
                        val quoteObj = jsonArray.getJSONObject(i)
                        val quote = quoteObj.getString("q")
                        val author = quoteObj.getString("a")

                        quoteList.add(Pair(quote, author))
                        adapter.notifyItemInserted(quoteList.size - 1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing quote", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Failed to load quotes: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        queue.add(request)
    }


    private fun saveToFavorites(quote: String, author: String) {
        val sharedPrefs = getSharedPreferences("Favorites", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val set = sharedPrefs.getStringSet("favoriteQuotes", mutableSetOf())?.toMutableSet()
        set?.add("\"$quote\" - $author")
        editor.putStringSet("favoriteQuotes", set)
        editor.apply()
        Toast.makeText(this, "Saved to Favorites", Toast.LENGTH_SHORT).show()
    }

    inner class QuoteAdapter(
        private val quoteList: List<Pair<String, String>>,
        private val onSaveClick: (String, String) -> Unit
    ) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

        inner class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val quoteText: TextView = itemView.findViewById(R.id.quoteText)
            val authorText: TextView = itemView.findViewById(R.id.authorText)
            val saveBtn: Button = itemView.findViewById(R.id.saveBtn)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.quote_item, parent, false)
            return QuoteViewHolder(view)
        }

        override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
            val (quote, author) = quoteList[position]
            holder.quoteText.text = "\"$quote\""
            holder.authorText.text = "- $author"
            holder.saveBtn.setOnClickListener {
                onSaveClick(quote, author)
            }
        }

        override fun getItemCount(): Int = quoteList.size
    }
}
