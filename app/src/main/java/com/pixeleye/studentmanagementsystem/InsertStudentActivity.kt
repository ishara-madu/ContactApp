package com.pixeleye.studentmanagementsystem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InsertStudentActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var selectedImagePath: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_student)

        dbHelper = DatabaseHelper(this)

        val nameInput: EditText = findViewById(R.id.nameInput)
        val phoneInput: EditText = findViewById(R.id.phoneInput)
        val profileImage: ImageView = findViewById(R.id.imageView)
        val selectImage: Button = findViewById(R.id.selectImage)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Select Image
        selectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        // Save Contact
        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty() && selectedImagePath.isNotEmpty()) {
                val contact = Contact(name = name, phone = phone, imagePath = selectedImagePath)
                val result = dbHelper.addContact(contact)
                if (result > 0) {
                    Toast.makeText(this, "Contact saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save contact!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            uri?.let {
                selectedImagePath = it.toString()
                val profileImage: ImageView = findViewById(R.id.imageView)
                profileImage.setImageURI(uri)
            }
        }
    }
}