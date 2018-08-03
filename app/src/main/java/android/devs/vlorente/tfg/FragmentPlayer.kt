package android.devs.vlorente.tfg

import android.database.Cursor
import android.devs.vlorente.tfg.adapters.SongListAdapter
import android.devs.vlorente.tfg.model.SongModel
import android.net.Uri
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
import org.jetbrains.anko.toast

/**
 * Created by Valentín Lorente Jiménez on 13/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */

class FragmentPlayer : Fragment() {


    private val TAG = "Fragement Player"
    private var songModelData: ArrayList<SongModel> = ArrayList()
    private var songListAdapter: SongListAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val uri : Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val viewRoot = inflater.inflate(R.layout.fragment_player,container,false)
        val context = this.context

        try{
            val selection = MediaStore.Audio.Media.IS_MUSIC +"!=0"
            val songCursor: Cursor? = context?.contentResolver?.query(uri,null,selection,null,null)

            while (songCursor != null && songCursor.moveToNext()){
                val songName = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val songDuration = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val songPath = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                songModelData.add(SongModel(songName,songDuration,songPath))
                Log.i(TAG,"Direción en memoria: $songPath")
            }
            songCursor!!.close()

        }catch (e: Exception){
            Log.e(TAG,e.toString())
            context?.toast("No se a podido acceder al contenido multimedia")
        }


        if (!songModelData.isEmpty()){
            songListAdapter = SongListAdapter(songModelData, context!!)
            val layoutManager = LinearLayoutManager(context)
            val recyclerView = viewRoot.find(R.id.recycler_view) as RecyclerView
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = songListAdapter
        } else {
            context?.toast("No se han encontrado archivos multimedia")
        }



        return viewRoot
    }
    


}