package com.apod.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope
import com.apod.utils.DBHelper
import com.apod.network.Parse
import com.apod.R
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {


    //late init variables to be assigned later in the lifecycle of the fragment
    private lateinit var saveButton : Button
    private lateinit var searchButton : Button
    private lateinit var dateText : TextView
    private lateinit var dbHelper : DBHelper
    private lateinit var parse : Parse
    private lateinit var titleText : TextView
    private lateinit var descriptionText : TextView
    private lateinit var imageView : ImageView
    private lateinit var webView : WebView
    private lateinit var urlSearch : TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton = view.findViewById<Button>(R.id.saveButtonSearch)
        searchButton = view.findViewById<Button>(R.id.searchButtonSearch)
        dateText = view.findViewById<TextView>(R.id.datePickerTextSearch)
        titleText = view.findViewById<TextView>(R.id.titleTextSearch)
        descriptionText = view.findViewById<TextView>(R.id.DescriptionSearch)
        imageView = view.findViewById<ImageView>(R.id.ImageView)
        webView = view.findViewById<WebView>(R.id.WebViewSearch)
        urlSearch = view.findViewById<TextView>(R.id.urlSearch)


        val calendar = Calendar.getInstance()

        //DatePickerDialog which calls updateCalendar Method
        val datePicker = DatePickerDialog.OnDateSetListener { _ , year, month, day ->


            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            //calling searchDateWithCalendar method while passing the calendar's set variables
            searchDateWithCalendar(calendar)

        }

        //search button opens a DatePickerDialog
        searchButton.setOnClickListener {

           DatePickerDialog(requireContext(), datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()

        }

        saveButton.setOnClickListener {

            val dbHelper = DBHelper(requireContext())

            //conditional statement to make sure
            if(imageView.isVisible) {

                dbHelper.insertDB(titleText.text.toString(), descriptionText.text.toString(), urlSearch.text.toString(),
                    imageView.drawable.toBitmap(), "image", dateText.text.toString())

            }
            else{
                dbHelper.insertDB(titleText.text.toString(), descriptionText.text.toString(), urlSearch.text.toString(),
                    null, "video", dateText.text.toString())
            }



        }

        searchButton.post { searchButton.performClick() }



    }


    //updateCalendar method formats the date and time, before setting the date with the
    //passed calendar, and then calling the api to fill the views with accurate information.
    private fun searchDateWithCalendar(calendar: Calendar){

        parse = Parse()


        //formatting date and time to be used in a search
        val dateTimeFormat = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(dateTimeFormat, Locale.CANADA)


        //calling search method to fill page
        var url : String = "https://api.nasa.gov/planetary/apod?api_key=2nCP2VMMzF163KI9pEsN430GQX66shkOgCaQ0DIP&date=" + simpleDateFormat.format(calendar.time)


        //parse JSON object from url
        lifecycleScope.launch {

            val jsonObject = withContext(Dispatchers.IO){

                parse.parseJSONFromURL(url)

            }

            if(jsonObject != null){
                if(jsonObject.getString("media_type") == "image"){

                    //parse bitmap using url retrieved from JSON object
                    lifecycleScope.launch {

                            val parsedBitmap = withContext(Dispatchers.IO) {

                                parse.parseImage(jsonObject.getString("url"))

                            }
                        if(parsedBitmap != null){

                            //set imageview
                            imageView.setImageBitmap(parsedBitmap)

                            //swap visibility of views
                            imageView.visibility = View.VISIBLE
                            webView.visibility = View.INVISIBLE

                        }
                    }
                } else if (jsonObject.getString("url").toString().contains("youtu")){

                    //open url with web view if the url is a video
                    webView.webViewClient = WebViewClient()
                    webView.settings.javaScriptEnabled = true
                    webView.loadUrl(parse.extractID(jsonObject.getString("url")))

                    //swap visibility of views
                    imageView.visibility = View.INVISIBLE
                    webView.visibility = View.VISIBLE

                }
                else {

                    //open url with web view if the url is a video
                    webView.webViewClient = WebViewClient()
                    webView.settings.javaScriptEnabled = true
                    webView.loadUrl(jsonObject.getString("url"))

                    //swap visibility of views
                    imageView.visibility = View.INVISIBLE
                    webView.visibility = View.VISIBLE


                }

                //Populate views using information from the returned JsonObject
                urlSearch.text = jsonObject.getString("url")
                dateText.text = jsonObject.getString("date")
                titleText.text = jsonObject.getString("title")
                descriptionText.text = jsonObject.getString("explanation")
                saveButton.visibility = View.VISIBLE

            }
        }
    }
}



