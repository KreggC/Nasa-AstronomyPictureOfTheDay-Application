package com.apod

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.apod.model.APOD
import com.apod.utils.DBHelper
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.model.MultipleFailureException.assertEmpty
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DBUnitTests {

    private lateinit var dbHelper : DBHelper


    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = DBHelper(context)
    }

    @Test
    fun insertDBTest(){

        val date = "2023-04-05"
        val description = "Test description"
        val url = "https://apod.nasa.gov/apod/image/2304/RubinsGalaxy_hst1024.jpg"
        val title = "Rubin's Galaxy"
        val mediaType = "video"
        val image : Bitmap? = null


        //insert
        dbHelper.insertDB(title, description, url, image, mediaType, date)


        //query
        val astronomyPictureList = dbHelper.queryDB()


        assertEquals(1, astronomyPictureList.size)
        assertEquals(date, astronomyPictureList[0].date)
        assertEquals(title, astronomyPictureList[0].title)
        assertEquals(description, astronomyPictureList[0].explanation)
        assertEquals(mediaType, astronomyPictureList[0].mediaType)
        assertEquals(url, astronomyPictureList[0].url)

    }

    @Test
    fun deleteFromDBTest(){

        val date = "2023-04-05"
        val description = "Test description"
        val url = "https://apod.nasa.gov/apod/image/2304/RubinsGalaxy_hst1024.jpg"
        val title = "Rubin's Galaxy"
        val mediaType = "video"
        val image : Bitmap? = null


        //insert
        dbHelper.insertDB(title, description, url, image, mediaType, date)


        //query
        val astronomyPictureList = dbHelper.queryDB()


        //delete using ID
        dbHelper.deleteFromDB(astronomyPictureList[0].id)


        //query again
        val astronomyPictureListNoItems = dbHelper.queryDB()


        //assert list is empty
        assert(astronomyPictureListNoItems.isEmpty())

    }
}