package com.example.flaptrack
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MyAdapter(private val context: Activity, private val arrayList : ArrayList<SavingData> ): ArrayAdapter<SavingData>(context, R.layout.activity_bird_list_item, arrayList)
{

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.activity_bird_list_item, null)

        val imageView : ImageView = view.findViewById(R.id.ivBirdPicture)
        val birdName : TextView = view.findViewById(R.id.tvBirdName)
        val birdSpecies : TextView = view.findViewById(R.id.tvBirdSpecies)
        val capturedDate : TextView = view.findViewById(R.id.tvCapturedDate)


        imageView.setImageResource(arrayList[position].dataImage)
        birdName.text = arrayList[position].birdName
        birdSpecies.text = arrayList[position].birdSpecie
        capturedDate.text = arrayList[position].date

        return view
    }
}