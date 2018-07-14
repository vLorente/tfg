package android.devs.vlorente.tfg

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Valentín Lorente Jiménez on 13/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */

class FragmentMain : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main,container,false)
    }


}