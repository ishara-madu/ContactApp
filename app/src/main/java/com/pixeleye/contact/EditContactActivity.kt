package com.pixeleye.contact

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pixeleye.contact.databinding.ActivityEditContactBinding
import java.io.File
import java.io.FileOutputStream
import java.util.zip.Inflater

class EditContactActivity : AppCompatActivity() {

    private lateinit var binding:ActivityEditContactBinding
    private var id: Int? = null
    private lateinit var selectedImagePath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditContactBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Select Image
        binding.editImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        id = intent.getIntExtra("id",0)
        val dbHelper = DatabaseHelper(this)
        val contact = dbHelper.getContactById(id!!)

        fun loadImageFromFilePath(filePath: String, imageView: ImageView) {
            val bitmap = BitmapFactory.decodeFile(filePath)
            imageView.setImageBitmap(bitmap)
        }

        if (contact?.imagePath!!.isNotEmpty()) {
            loadImageFromFilePath(contact.imagePath,  binding.editImageView)
        } else {
           binding.editImageView.setImageResource(R.drawable.user)
        }

        binding.editNameInput.setText(contact.name)
        binding.editPhoneInput.setText(contact.phone)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            uri?.let {
                // Show the selected image
                val profileImage: ImageView = findViewById(R.id.imageView)
                profileImage.setImageURI(uri)

                // Save the image to internal storage and get the file path
                val filePath = saveImageToFile(this, uri, "profile_image_${System.currentTimeMillis()}.png")

//                 Store the file path in the database
                if (filePath != null) {
                    selectedImagePath = filePath
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveImageToFile(context: Context, imageUri: Uri, fileName: String): String? {
        return try {
            // First, get the dimensions of the image without loading it fully into memory
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true  // This will load only the image's dimensions, not the whole bitmap
            }

            // Decode the image file to get its dimensions
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            // Calculate the sample size (scale factor) to downsize the image
            val requiredWidth = 80  // Desired width
            val requiredHeight = 80  // Desired height
            val scaleFactor = calculateInSampleSize(options, requiredWidth, requiredHeight)

            // Now decode the image with the correct sample size
            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = scaleFactor  // Set the scale factor to reduce the size
            }

            // Decode the image to a scaled bitmap
            val bitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, finalOptions)
            }

            // Check if the bitmap is null
            if (bitmap == null) {
                return null
            }

            // Save the scaled bitmap to internal storage
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            // Compress the bitmap to reduce its quality (e.g., 80% quality)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream) // Adjust the quality (0 to 100)
            outputStream.flush()
            outputStream.close()

            // Return the file path
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()  // Log the error for debugging
            null  // Return null if saving failed
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw width and height of image
        val height = options.outHeight
        val width = options.outWidth

        // Initializing scale factor to 1
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both width and height larger than required
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Keep halving the width/height until it's smaller than the required dimensions
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}