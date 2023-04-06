package com.apod

import android.graphics.Bitmap
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.apod.utils.BitmapConverter
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class BitmapConverterUnitTest {

    private lateinit var bitmapConverter: BitmapConverter
    private lateinit var testBitmap: Bitmap

    @Before
    fun setup() {
        bitmapConverter = BitmapConverter()
        testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    }

    @Test
    fun bitmapToStringTest() {
        val expectedString = Base64.getEncoder().encodeToString(byteArrayFromBitmap(testBitmap))
        val resultString = bitmapConverter.bitmapToString(testBitmap)
        assertEquals(expectedString, resultString)
    }

    @Test
    fun stringToBitmapTest() {
        val encodedString = Base64.getEncoder().encodeToString(byteArrayFromBitmap(testBitmap))
        val expectedBitmap = testBitmap
        val resultBitmap = bitmapConverter.stringToBitmap(encodedString)
        assertEquals(expectedBitmap.width, resultBitmap.width)
        assertEquals(expectedBitmap.height, resultBitmap.height)
    }

    private fun byteArrayFromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

}