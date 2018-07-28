package android.devs.vlorente.tfg

import android.database.Cursor
import android.devs.vlorente.tfg.adapters.SongListAdapter
import android.devs.vlorente.tfg.model.SongModel
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.find

/**
 * Created by Valentín Lorente Jiménez on 13/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */

class FragmentPlayer : Fragment() {


    private val TAG = "Fragement Player"
    private var songModelData: ArrayList<SongModel> = ArrayList()
    private var songListAdapter: SongListAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.fragment_player,container,false)
        val context = this.context
        val songCursor: Cursor? = context?.contentResolver?.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null)



        while (songCursor != null && songCursor.moveToNext()){
            val songName = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            val songDuration = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            songModelData.add(SongModel(songName,songDuration))
            Log.i(TAG,"Nombre: $songName")
        }

        if (songModelData.isEmpty()){
            songModelData.add(SongModel("No se han encontrado canciones",""))
        }

        songListAdapter = SongListAdapter(songModelData)
        val layoutManager = LinearLayoutManager(context)
        val recyclerView = viewRoot.find(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = songListAdapter

        return viewRoot
    }
    


}