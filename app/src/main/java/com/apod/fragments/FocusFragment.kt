package com.apod.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import com.apod.R
import com.apod.network.Parse
import com.apod.utils.BitmapConverter
import com.apod.utils.DBHelper


class FocusFragment : Fragment() {


    //late init variables to be assigned later in the lifecycle of the fragment
    private lateinit var backButton : ImageButton
    private lateinit var searchFragment: SearchFragment
    private lateinit var imageView: ImageView
    private lateinit var parse: Parse
    private lateinit var titleFocus: TextView
    private lateinit var descriptionFocus: TextView
    private lateinit var dateFocus: TextView
    private lateinit var webView: WebView
    private lateinit var urlFocus: TextView
    private lateinit var dbHelper: DBHelper
    private lateinit var bitmapConverter: BitmapConverter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //assigning inflated view for later return
        val view = inflater.inflate(R.layout.focus_fragment, container, false)

        imageView = view.findViewById(R.id.ImageViewFocus)
        webView = view.findViewById(R.id.WebViewFocus)
        backButton = view.findViewById(R.id.backButtonFocus)
        titleFocus = view.findViewById<EditText>(R.id.titleTextFocus)
        descriptionFocus = view.findViewById<EditText>(R.id.DescriptionFocus)
        dateFocus = view.findViewById<EditText>(R.id.dateTextFocus)
        urlFocus = view.findViewById<EditText>(R.id.urlFocus)

        //instantiating objects of classes
        searchFragment = SearchFragment()
        parse = Parse()
        dbHelper = DBHelper(requireContext())
        bitmapConverter = BitmapConverter()

        val extras = arguments

        if(extras != null){

            val idIntent = extras.getInt("ID")

                val astronomyPicture = dbHelper.queryWithID(idIntent.toString())

                if(astronomyPicture != null){

                    titleFocus.text = astronomyPicture.title
                    descriptionFocus.text = astronomyPicture.explanation
                    dateFocus.text = astronomyPicture.date
                    urlFocus.text = astronomyPicture.url


                    val mediaCheck = astronomyPicture.mediaType

                    if(mediaCheck == "image"){
                        imageView.setImageBitmap(bitmapConverter.stringToBitmap(astronomyPicture.image.toString()))
                        imageView.visibility = View.VISIBLE
                        webView.visibility = View.INVISIBLE
                    } else{
                        imageView.visibility = View.INVISIBLE
                        webView.visibility = View.VISIBLE
                    }

                }

        }


        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //back button
        backButton.setOnClickListener{


            requireActivity().onBackPressed()


        }
    }
}