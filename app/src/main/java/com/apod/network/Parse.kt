package com.apod.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Parse() {


    //https://api.nasa.gov/planetary/apod?api_key=2nCP2VMMzF163KI9pEsN430GQX66shkOgCaQ0DIP&date=

    //https://api.nasa.gov/planetary/apod?api_key=2nCP2VMMzF163KI9pEsN430GQX66shkOgCaQ0DIP

    suspend fun parseJSONFromURL(string: String) : JSONObject? {

        return withContext(Dispatchers.IO) {

            var jsonObject: JSONObject?

            try {

                //opens connection, opens input stream, uses buffered reader to read the webpage as text, creates json object using the string
                val url = URL(string)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                val inputStream = connection.inputStream
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                jsonObject = JSONObject(jsonString)

                //return JSON object
                jsonObject

            } catch (e: Exception) {

                //if there is an exception, print the stack trace, set the JSON object to null, and return the null json object.
                e.printStackTrace()
                jsonObject = null
                jsonObject

            }
        }
    }


    suspend fun parseDate(string: String): String {

        return withContext(Dispatchers.IO) {


            try {


                val parseURL = URL(string)
                var parsedURL: String


                /*
                opens connection, opens input stream, starts a reader and string builder, reads webpage and returns lines as string.
                null check in other classes when using method.
                 */

                val connection = parseURL.openConnection()
                val inputStream = connection.getInputStream()
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?

                //loop page for lines
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                    stringBuilder.append("\n")
                }

                //stringBuilder to a string
                parsedURL = stringBuilder.toString()

                //return parsed string
                parsedURL


            } catch (e: Exception) {

                e.printStackTrace()
                ""

            }
        }
    }

    suspend fun parseImage(string : String) : Bitmap? {


        return withContext(Dispatchers.IO) {

            var bitmap : Bitmap?

            try {

                val url = URL(string)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream

                //sets bitmap using BitmapFactory with the opened InputStream as an argument
                bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap

            } catch ( e : Exception) {

                /*        
                if there is an exception, print the stack trace, set the bitmap to null, and return the null json object.
                null check in other classes when using method.
                */
                
                e.printStackTrace()
                bitmap = null
                bitmap

            }
        }
    }

     fun extractID(url: String): String {
        val videoID = url.substringAfterLast("/")

        return "https://youtube.com/watch?v=$videoID"
    }
}