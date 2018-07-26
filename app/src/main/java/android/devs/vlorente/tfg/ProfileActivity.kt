package android.devs.vlorente.tfg

import android.devs.vlorente.tfg.Beans.UserBean
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.jetbrains.anko.coroutines.experimental.Ref
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.find
import org.jetbrains.anko.longToast



/**
 * Created by Valentín Lorente Jiménez on 24/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var imgUser : ImageView
    private lateinit var nameUser : TextView
    private lateinit var emailUser : TextView
    private lateinit var lottie : LottieAnimationView
    private lateinit var linear : LinearLayout
    private lateinit var relative : RelativeLayout
    private lateinit var imgHeader : ImageView
    private lateinit var currentUser : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgUser = find(R.id.img_profile)
        imgHeader = find(R.id.header_cover_image)
        nameUser = find(R.id.txt_name_profile)
        emailUser = find(R.id.txt_email_profile)
        lottie = find(R.id.profile_animation)
        linear = find(R.id.linear_profile)
        relative = find(R.id.profile_layout)
        db = FirebaseFirestore.getInstance()
        currentUser = FirebaseAuth.getInstance()

        val ref: Ref<ProfileActivity> = this.asReference()
        val docRef = db.collection("Users").document(currentUser.uid!!)
        var user : UserBean? = null
        docRef.get().addOnSuccessListener { documentSnapshot ->
            user = documentSnapshot.toObject<UserBean>(UserBean::class.java)
        }

        async(UI){
            delay(1400)

            if(user != null){
                ref.invoke().displayUser(user!!)
                ref.invoke().stopAnimation()
            }
            else{
                longToast("Error al cargar los datos de usuario, perdone las molestias")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        playAnimation()
    }


    private fun displayUser(user : UserBean){
        val name : String? = user.name
        val email : String? = user.email

        nameUser.text = name
        emailUser.text = email
    }

    private fun playAnimation(){
        nameUser.visibility = View.GONE
        emailUser.visibility = View.GONE
        imgUser.visibility = View.GONE
        linear.visibility = View.GONE
        imgHeader.visibility = View.GONE
        relative.visibility = View.GONE
        lottie.visibility = View.VISIBLE

        lottie.playAnimation()
    }

    private fun stopAnimation(){
        lottie.pauseAnimation()
        nameUser.visibility = View.VISIBLE
        emailUser.visibility = View.VISIBLE
        imgUser.visibility = View.VISIBLE
        linear.visibility = View.VISIBLE
        imgHeader.visibility = View.VISIBLE
        relative.visibility = View.VISIBLE
        lottie.visibility = View.GONE
    }



}