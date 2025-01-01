package com.pixeleye.contact

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){
    companion object{
        private const val DATABASE_NAME = "ContactDatabase"
        private const val DATABASE_VERSION = 1

        val TABLE_NAME = "contacts"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_PHONE = "phone"
        val COLUMN_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_NAME TEXT, $COLUMN_PHONE TEXT, $COLUMN_IMAGE TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Add Contact
    fun addContact(contact: Contact): Long{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, contact.name)
        values.put(COLUMN_PHONE, contact.phone)
        values.put(COLUMN_IMAGE, contact.imagePath)
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val contact = Contact(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
                )
                contacts.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return contacts
    }

    fun getContactById(contactId: Int): Contact? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_PHONE, COLUMN_IMAGE),
            "$COLUMN_ID =?",
            arrayOf(contactId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val contact = Contact(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
            )
            cursor.close()
            db.close()
            return contact
        }
        cursor.close()
        db.close()
        return null
    }

    // Update Contact
    fun updateContact(contact: Contact): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, contact.name)
        values.put(COLUMN_PHONE, contact.phone)
        values.put(COLUMN_IMAGE, contact.imagePath)
        val result = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(contact.id.toString()))
        db.close()
        return result
    }

    // Delete Contact
    fun deleteContact(contactId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(contactId.toString()))
        db.close()
        return result
    }


}