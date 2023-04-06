package com.apod.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.apod.network.Parse
import com.apod.R
import com.apod.utils.DBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {


    //late init variables for later assignment
    private lateinit var saveButton : Button
    private lateinit var searchButton : Button
    private lateinit var searchFragment : SearchFragment
    private lateinit var imageView : ImageView
    private lateinit var parse : Parse
    private lateinit var titleHome : TextView
    private lateinit var descriptionHome : TextView
    private lateinit var dateHome : TextView
    private lateinit var webView: WebView
    private lateinit var urlHome : TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //assigning inflated view for later return
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        //assign views to variables
        saveButton = view.findViewById(R.id.saveButtonHome)
        searchButton = view.findViewById(R.id.searchButtonHome)
        imageView = view.findViewById(R.id.ImageViewHome)
        titleHome = view.findViewById<EditText>(R.id.titleTextHome)
        descriptionHome = view.findViewById<EditText>(R.id.DescriptionHome)
        dateHome = view.findViewById<EditText>(R.id.dateTextHome)
        urlHome = view.findViewById<EditText>(R.id.urlHome)
        webView = view.findViewById(R.id.webViewHome)

        //instantiating objects of classes
        searchFragment = SearchFragment()
        parse = Parse()


        //create a Date/Time format and assign today's date to currentDate value using the established format
        val dateTimeFormat = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(dateTimeFormat, Locale.CANADA)
        val currentDate = simpleDateFormat.format(Date())


        //building url that will be used for the API request
        val urlWithDate = "https://api.nasa.gov/planetary/apod?api_key=2nCP2VMMzF163KI9pEsN430GQX66shkOgCaQ0DIP&date=$currentDate"


        //Asynchronously call API using url as argument
        lifecycleScope.launch {
            val jsonObject = withContext(Dispatchers.IO) {
                parse.parseJSONFromURL(urlWithDate)
            }


            //Checks the api call returned a not null JsonObject
            if (jsonObject != null) {

                //Checks media_type to decide which view to populate
                if (jsonObject.getString("media_type") == "image") {

                    //parse image to bitmap if the url is an image
                    lifecycleScope.launch {
                        val parsedBitmap = withContext(Dispatchers.IO) {

                            parse.parseImage(jsonObject.getString("url"))

                        }

                        imageView.setImageBitmap(parsedBitmap)

                    }


                    imageView.visibility = View.VISIBLE
                    webView.visibility = View.INVISIBLE

                } else if(jsonObject.getString("url").toString().contains("youtube")){

                    //open url with web view if the url is a youtube video
                    webView.webViewClient = WebViewClient()
                    webView.settings.javaScriptEnabled = true
                    webView.loadUrl(parse.extractID(jsonObject.getString("url")))

                    imageView.visibility = View.INVISIBLE
                    webView.visibility = View.VISIBLE

                } else {

                    //open url with web view if the url is a video
                    webView.webViewClient = WebViewClient()
                    webView.settings.javaScriptEnabled = true
                    webView.loadUrl(jsonObject.getString("url"))

                    imageView.visibility = View.INVISIBLE
                    webView.visibility = View.VISIBLE

                }

                //Populate views using information from the returned JsonObject
                urlHome.text = jsonObject.getString("url")
                dateHome.text = jsonObject.getString("date")
                titleHome.text = jsonObject.getString("title")
                descriptionHome.text = jsonObject.getString("explanation")
            }
        }

        //return inflated view
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //save button listener
        saveButton.setOnClickListener {

            //DBHelper object for insertion
            val dbHelper = DBHelper(requireContext())

            //conditional statement to determine how the mediaType is saved into the database
            if(imageView.isVisible) {

                //insert bitmap and "image" if an image is displayed
                dbHelper.insertDB(titleHome.text.toString(), descriptionHome.text.toString(), urlHome.text.toString(),
                    imageView.drawable.toBitmap(), "image", dateHome.text.toString())

            }
            else{
                //insert null and "video" if a video is displayed
                dbHelper.insertDB(titleHome.text.toString(), descriptionHome.text.toString(), urlHome.text.toString(),
                    null, "video", dateHome.text.toString())
            }

        }

        saveButton.visibility = View.VISIBLE




        //search button listener that replaces current fragment with an instance of the Search Fragment
        searchButton.setOnClickListener {

            val fragmentManager = requireActivity().supportFragmentManager
            val fragment = searchFragment
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
    }
}
