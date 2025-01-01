package com.pixeleye.contact

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var addButton: FloatingActionButton
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addButton = findViewById(R.id.addButton)
        emptyText = findViewById(R.id.emptyText)
        emptyText.visibility = View.GONE

        addButton.setOnClickListener {
            val intent = Intent(this, InsertContactActivity::class.java)
            startActivity(intent)
        }
        setList()
    }

    private fun setList() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dbHelper = DatabaseHelper(this)
        val contacts = dbHelper.getAllContacts()

        val adapter = ContactAdapter(contacts)
        recyclerView.adapter = adapter


        if (contacts.isEmpty()) {
            emptyText.visibility = View.VISIBLE
        }
    }

    //    Update contact list
    override fun onResume() {
        super.onResume()
        setList()
    }
}