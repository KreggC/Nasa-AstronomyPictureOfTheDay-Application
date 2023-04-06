package com.apod.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import com.apod.model.APOD

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_Name, null, 1) {

    companion object {
        private const val DB_Name = "APOD"
        private const val Table = "saved"
        private const val ID = "id"
        private const val Title = "title"
        private const val Explanation = "Explanation"
        private const val Url = "url"
        private const val imageDB = "image"
        private const val MediaType = "MediaType"
        private const val Date = "Date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSQL = "CREATE TABLE $Table ($ID INTEGER PRIMARY KEY, $Title TEXT, $Explanation TEXT, $Url TEXT, $imageDB TEXT, $MediaType TEXT, $Date TEXT);"
        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p0: Int, p1: Int) {
        val updateSQL = "DROP TABLE IF EXISTS $Table"
        db?.execSQL(updateSQL)
        onCreate(db)
    }

    fun queryDB() : MutableList<APOD>{
        val saved = ArrayList<APOD>()
        val db = this.readableDatabase
        val selectSQL = "SELECT * FROM $Table"
        val cursor = db.rawQuery(selectSQL, null)

        if( cursor != null ){
            if( cursor.moveToFirst() ){
                do {

                    //create object from cursor
                    val apod = APOD()

                    //check if column is blank before continuing and assigning apod parameters
                    val idCheck = cursor.getColumnIndex(ID)
                    if (idCheck == -1) continue
                    apod.id = Integer.parseInt(cursor.getString(idCheck))

                    val titleCheck = cursor.getColumnIndex(Title)
                    if (titleCheck == -1 ) continue
                    apod.title = cursor.getString(titleCheck)

                    val explanationCheck = cursor.getColumnIndex(Explanation)
                    if (explanationCheck == -1) continue
                    apod.explanation = cursor.getString(explanationCheck)

                    val urlCheck = cursor.getColumnIndex(Url)
                    if (urlCheck == -1) continue
                    apod.url = cursor.getString(urlCheck)

                    val imageCheck = cursor.getColumnIndex(imageDB)
                    if (imageCheck == -1) continue
                    if(cursor.getString(imageCheck) == "no_image") {
                        apod.image = null
                    } else {
                        apod.image = cursor.getString(imageCheck)
                    }

                    val mediaCheck = cursor.getColumnIndex(MediaType)
                    if (mediaCheck == -1) continue
                    apod.mediaType = cursor.getString(mediaCheck)

                    val dateCheck = cursor.getColumnIndex(Date)
                    if (dateCheck == -1) continue
                    apod.date = cursor.getString(dateCheck)

                    saved.add(apod)

                } while (cursor.moveToNext())
            }
        }
        //close db and cursor
        db.close()
        cursor.close()

        //return list of saved APODs
        return saved
    }



    fun insertDB(title: String, description : String, url : String, image : Bitmap?, mediaType : String, date: String ) {

        val db = this.writableDatabase
        val contentValues = ContentValues()
        val bitmapConverter = BitmapConverter()

        //contentValues filled for insertion
        contentValues.put(Title, title)
        contentValues.put(Explanation, description)
        contentValues.put(Url, url)

        if (image != null) {
            val imageString : String = bitmapConverter.bitmapToString(image)
            contentValues.put(imageDB, imageString)
        } else {
            contentValues.put(imageDB, "no_image")
        }

        contentValues.put(MediaType, mediaType)
        contentValues.put(Date, date)

        //nullColumnHack for autoincrement ID
        db.insert(Table, null, contentValues)

        //close database
        db.close()
    }

    fun deleteFromDB(id: Int){

        val db = this.writableDatabase
        db.delete(Table, "$ID=?", arrayOf(id.toString()))

        //close db
        db.close()

    }

    fun queryWithID(id: String) : APOD? {


        val queryString = "SELECT * FROM $Table WHERE $ID = ?"
        val args = arrayOf(id)

        val db = this.readableDatabase
        var astronomyPicture : APOD? = null


        val cursor = db.rawQuery(queryString, args)
        if (cursor != null && cursor.moveToFirst()) {

            astronomyPicture = APOD()

            val idCheck = cursor.getColumnIndex(ID)
            if (idCheck != -1) astronomyPicture.id = cursor.getInt(idCheck)

            val titleCheck = cursor.getColumnIndex(Title)
            if (titleCheck != -1) astronomyPicture.title = cursor.getString(titleCheck)

            val descCheck = cursor.getColumnIndex(Explanation)
            if (descCheck != -1) astronomyPicture.explanation = cursor.getString(descCheck)

            val urlCheck = cursor.getColumnIndex(Url)
            if (urlCheck != -1) astronomyPicture.url = cursor.getString(urlCheck)

            val imageCheck = cursor.getColumnIndex(imageDB)
            if (imageCheck != -1) astronomyPicture.image = cursor.getString(imageCheck)

            val mediaCheck = cursor.getColumnIndex(MediaType)
            if (mediaCheck != -1) astronomyPicture.mediaType = cursor.getString(mediaCheck)

            val dateCheck = cursor.getColumnIndex(Date)
            if (dateCheck != -1) astronomyPicture.date = cursor.getString(dateCheck)

        }

        cursor?.close()
        db.close()

        return astronomyPicture
    }
}