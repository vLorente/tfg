package android.devs.vlorente.tfg.Beans

/**
 * Created by Valentín Lorente Jiménez on 16/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */
data class UserModel (var name:String?, var email:String?, var uid:String?){
    constructor() : this("","","")
}

