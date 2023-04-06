package com.apod.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.*

class BitmapConverter {

    fun bitmapToString(bitmap: Bitmap) : String {

        var bytearrayoutput = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100, bytearrayoutput)
        val byteArray = bytearrayoutput.toByteArray()

        return Base64.getEncoder().encodeToString(byteArray)
    }

    fun stringToBitmap(string : String) : Bitmap {

        val imageByteArray = Base64.getDecoder().decode(string)

        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

}