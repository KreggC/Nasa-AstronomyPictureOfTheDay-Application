package com.apod.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.apod.R
import com.apod.fragments.FocusFragment
import com.apod.fragments.SearchFragment
import com.apod.model.APOD
import com.apod.utils.BitmapConverter
import com.apod.utils.DBHelper

class APODListAdapter(private val saved : MutableList<APOD>, internal var context : Context) : RecyclerView.Adapter<APODListAdapter.ViewHolder>() {


    private val dbHelper = DBHelper(this.context)
    private val bitmapConverter = BitmapConverter()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val title: TextView = itemView.findViewById(R.id.titleTextRecycler)
        val date: TextView = itemView.findViewById(R.id.dateTextRecycler)
        val delButton: ImageButton = itemView.findViewById(R.id.recycleDelete)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewRecycler)


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_item, parent, false)
        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        val currentItem = saved[position]

        var bitmap : Bitmap? = if(currentItem.mediaType == "image"){
            bitmapConverter.stringToBitmap(currentItem.image.toString())
        } else{
            null
        }


        holder.title.text = currentItem.title
        holder.date.text = currentItem.date

        if(bitmap != null){
            holder.imageView.setImageBitmap(bitmap)
        }else{
            val vectorDrawable = getDrawable(this.context, R.drawable.ic_baseline_play_circle_outline_24)
            holder.imageView.setImageDrawable(vectorDrawable)
        }


        holder.delButton.setOnClickListener {

            val deleteQuestion = "Delete this astronomy picture of the day?"
            val deleteMessage = "Would you like to delete this astronomy picture of the day?"


            val dialog =
                AlertDialog.Builder(context).setTitle(deleteQuestion)
                    .setMessage(deleteMessage)
                    //dialog accept
                    .setPositiveButton("Yes") { dialog, _ ->
                        dbHelper.deleteFromDB(currentItem.id)

                        saved.removeAt(position)
                        notifyItemRemoved(position)

                        dialog.dismiss()

                    }
                    //dialog deny
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }

            dialog.show()
        }

        holder.itemView.setOnClickListener {


                    val bundle = Bundle()
                    bundle.putInt("ID", currentItem.id)


                    val fragment = FocusFragment()
                    fragment.arguments = bundle

                    val transaction = (this.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_layout, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()


                }
            }



    override fun getItemCount() = saved.size

}
