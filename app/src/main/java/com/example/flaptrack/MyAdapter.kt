package com.example.flaptrack

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide



class MyAdapter: RecyclerView.Adapter<MyAdapter.EntryViewHolder>() {

    private var arrayList = mutableListOf<BirdInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.activity_bird_list_item, parent, false)
        return EntryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val bird = arrayList[position]
        holder.setItem(bird)
        val bytes = Base64.decode(bird.image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        holder.recImage.setImageBitmap(bitmap)
    }

    fun setItem(list: MutableList<BirdInfo>){
        this.arrayList = list
        notifyDataSetChanged()
    }

    class EntryViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var recName: TextView? = null
        private var recSpecies: TextView? = null
        private var recDate: TextView? = null
        private var recLocation: TextView? = null

        val recImage: ImageView = itemView.findViewById(R.id.ivBirdPicture)

        fun setItem(bird: BirdInfo) {
            recName = itemView.findViewById(R.id.tvBirdName)
            recSpecies = itemView.findViewById(R.id.tvBirdSpecies)
            recDate = itemView.findViewById(R.id.tvDate)
            recLocation = itemView.findViewById(R.id.tvCurrentLocation)




            recName?.text = bird.birdName
            recSpecies?.text = bird.birdSpecies
            recDate?.text = bird.date
            recLocation?.text = bird.location


        }
    }
    }
//class MyAdapter(private val context: android.content.Context, private var arrayList: List<BirdInfo>) : RecyclerView.Adapter<MyViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_bird_list_item, parent, false)
//        return MyViewHolder(view)
//    }
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        Glide.with(context).load(arrayList[position].image)
//            .into(holder.recImage)
//        holder.recName.text = arrayList[position].birdName
//        holder.recSpecies.text = arrayList[position].birdSpecies
//        holder.recDate.text = arrayList[position].date
//    }
//
//    override fun getItemCount(): Int {
//        return arrayList.size
//    }
//
//    fun searchDataList(searchList: List<BirdInfo>){
//        arrayList = searchList
//        notifyDataSetChanged()
//    }
//}
//class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    var recImage: ImageView
//    var recName: TextView
//    var recSpecies: TextView
//    var recDate: TextView
//
//    init {
//        recImage = itemView.findViewById(R.id.ivBirdPicture)
//        recName = itemView.findViewById(R.id.tvBirdName)
//        recSpecies = itemView.findViewById(R.id.tvBirdSpecies)
//        recDate = itemView.findViewById(R.id.tvDate)
//    }
//}
//
//
//








//class MyAdapter(private val birdList : ArrayList<BirdInfo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>()
//{
//
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_bird_list_item, parent, false)
//        return MyViewHolder(itemView)
//    }
//
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//
//        val currentItem = birdList[position]
//
//        holder.theBirdName.text = currentItem.birdName
//        holder.theBirdSpecies.text = currentItem.birdSpecies
//        holder.theBirdDate.text = currentItem.date
//
//    }
//
//    override fun getItemCount(): Int {
//        return birdList.size
//    }

//    class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
//
//        val theBirdName : TextView = itemView.findViewById(R.id.tvBirdName)
//        val theBirdSpecies : TextView = itemView.findViewById(R.id.tvBirdSpecies)
//        val theBirdDate :  TextView = itemView.findViewById(R.id.tvCapturedDate)
//
//
//
//
//    }







//(private val context: Activity, private val arrayList : ArrayList<SavingData> ): ArrayAdapter<SavingData>(context, R.layout.activity_bird_list_item, arrayList)
//{

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//
//        val inflater : LayoutInflater = LayoutInflater.from(context)
//        val view : View = inflater.inflate(R.layout.activity_bird_list_item, null)
//
//        val imageView : ImageView = view.findViewById(R.id.ivBirdPicture)
//        val birdName : TextView = view.findViewById(R.id.tvBirdName)
//        val birdSpecies : TextView = view.findViewById(R.id.tvBirdSpecies)
//        val capturedDate : TextView = view.findViewById(R.id.tvCapturedDate)
//
//
//        imageView.setImageResource(arrayList[position].dataImage)
//        birdName.text = arrayList[position].birdName
//        birdSpecies.text = arrayList[position].birdSpecie
//        capturedDate.text = arrayList[position].date
//
//        return view
//    }