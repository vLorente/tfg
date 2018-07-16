package android.devs.vlorente.tfg

import android.app.Application
import com.facebook.appevents.AppEventsLogger

/**
 * Created by Valentín Lorente Jiménez on 14/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */
class MyAplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)
    }
}