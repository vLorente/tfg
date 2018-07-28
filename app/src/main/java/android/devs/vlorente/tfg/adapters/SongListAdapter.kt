package android.devs.vlorente.tfg.adapters

import android.devs.vlorente.tfg.R
import android.devs.vlorente.tfg.model.SongModel
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find

/**
 * Created by Valentín Lorente Jiménez on 27/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */
class SongListAdapter(SongModel:ArrayList<SongModel>): RecyclerView.Adapter<SongListAdapter.SongListViewHolder>() {

    var mSongModel = SongModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_row,parent,false)
        return SongListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mSongModel.size
    }

    override fun onBindViewHolder(holder: SongListViewHolder, position: Int) {
        val model = mSongModel[position]
        val songName  = model.mSongName
        val songDuration = model.mSongDuration
        holder.songName.text = songName
        holder.songDuration.text = songDuration
    }


    class SongListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var songName : TextView
        var songDuration : TextView
        var albumnArt:ImageView

        init {
            songName = itemView.find(R.id.song_name)
            songDuration = itemView.find(R.id.song_duration)
            albumnArt = itemView.find(R.id.song_image_view)
        }
    }
}