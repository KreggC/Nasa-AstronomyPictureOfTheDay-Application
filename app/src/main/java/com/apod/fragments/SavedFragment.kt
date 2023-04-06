package com.apod.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apod.utils.DBHelper
import com.apod.R
import com.apod.adapters.APODListAdapter


class SavedFragment : Fragment() {

    //late init variables to be assigned later in lifecycle of the fragment
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : APODListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dbHelper: DBHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.saved_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //DBHelper object to query the database for populating the recycler view
        dbHelper = DBHelper(requireContext())
        var saved = dbHelper.queryDB()

        //recycler view and adapter
        linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView = view.findViewById(R.id.recycler)
        adapter = APODListAdapter(saved = saved, requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager




    }
}

