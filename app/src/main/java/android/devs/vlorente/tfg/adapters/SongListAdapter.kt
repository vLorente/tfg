package android.devs.vlorente.tfg.adapters

import android.content.Context
import android.content.Intent
import android.devs.vlorente.tfg.R
import android.devs.vlorente.tfg.`interface`.CustomItemClickListener
import android.devs.vlorente.tfg.model.SongModel
import android.devs.vlorente.tfg.service.PlayMusicService
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find
import java.util.concurrent.TimeUnit

/**
 * Created by Valentín Lorente Jiménez on 27/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */
class SongListAdapter(SongModel:ArrayList<SongModel>, context:Context): RecyclerView.Adapter<SongListAdapter.SongListViewHolder>() {

    private var mContext = context
    private var mSongModel = SongModel
    private  var allMusicList : ArrayList<String> = ArrayList()

    companion object {
        val MUSICLIST = "musiclist"
        val MUSICITEMPOS = "pos"
    }

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
        val songDuration = toMinuteAndSecond(model.mSongDuration.toLong())
        holder.songName.text = songName
        holder.songDuration.text = songDuration
        holder.setOnCustomItemClickListener(object : CustomItemClickListener{
            override fun onCustomItemClick(view: View, pos: Int) {

                for (i in 0 until mSongModel.size){
                    allMusicList.add(mSongModel[i].mSongPath)
                }

                val musicDataIntent = Intent(mContext,PlayMusicService::class.java)
                musicDataIntent.putStringArrayListExtra(MUSICLIST,allMusicList)
                musicDataIntent.putExtra(MUSICITEMPOS,pos)
                mContext.startService(musicDataIntent)
            }
        })
    }

    private fun toMinuteAndSecond(millis:Long): String {
        val duration = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
        return duration
    }


    class SongListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var songName : TextView
        var songDuration : TextView
        var albumnArt:ImageView
        var mCustomItemClickListener:CustomItemClickListener?=null

        init {
            songName = itemView.find(R.id.song_name)
            songDuration = itemView.find(R.id.song_duration)
            albumnArt = itemView.find(R.id.song_image_view)
            itemView.setOnClickListener(this)
        }

        fun setOnCustomItemClickListener(customItemClickListener: CustomItemClickListener){
            this.mCustomItemClickListener = customItemClickListener
        }

        override fun onClick(view: View?) {
            this.mCustomItemClickListener!!.onCustomItemClick(view!!, adapterPosition)
        }
    }
}