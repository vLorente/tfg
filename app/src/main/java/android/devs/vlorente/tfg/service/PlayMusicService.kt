package android.devs.vlorente.tfg.service

import android.app.Service
import android.content.Intent
import android.devs.vlorente.tfg.adapters.SongListAdapter
import android.media.MediaPlayer
import android.os.IBinder

/**
 * Created by Valentín Lorente Jiménez on 28/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */
class PlayMusicService : Service(){

    var currentPos : Int = 0
    var musicDataList : ArrayList<String> = ArrayList()
    var mediaPlayer : MediaPlayer ?= null

    override fun onBind(p0: Intent?): IBinder  ?{
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        currentPos = intent!!.getIntExtra(SongListAdapter.MUSICITEMPOS,0)
        musicDataList = intent.getStringArrayListExtra(SongListAdapter.MUSICLIST)

        if(mediaPlayer != null){
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }

        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource(musicDataList[currentPos])
        mediaPlayer!!.prepareAsync()
        mediaPlayer!!.setOnPreparedListener{
            it.start()
        }

        return super.onStartCommand(intent, flags, startId)
    }
}