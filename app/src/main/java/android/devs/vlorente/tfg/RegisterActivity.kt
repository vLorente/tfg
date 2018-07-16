package android.devs.vlorente.tfg

import android.content.Intent
import android.devs.vlorente.tfg.Beans.User
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Created by Valentín Lorente Jiménez on 15/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var txtName: EditText
    private lateinit var txtSurname: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var database: FirebaseFirestore
    private lateinit var dbReference: CollectionReference
    private lateinit var auth: FirebaseAuth
    private lateinit var btbRegister: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtName = findViewById(R.id.register_name)
        txtSurname = findViewById(R.id.register_surname)
        txtEmail = findViewById(R.id.register_email)
        txtPassword = findViewById(R.id.register_password)
        btbRegister = findViewById(R.id.register_button)

        progressBar = findViewById(R.id.progressBar)

        database = FirebaseFirestore.getInstance()
        dbReference = database.collection("/Users")
        auth = FirebaseAuth.getInstance()

        btbRegister.setOnClickListener { view: View? ->
            createAccount()
        }


    }

    private fun createAccount() {
        val name:String = txtName.text.toString()
        val surname:String = txtSurname.text.toString()
        val email:String = txtEmail.text.toString()
        val password:String = txtPassword.text.toString()

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) &&!TextUtils.isEmpty(email) &&!TextUtils.isEmpty(password) ){
            progressBar.visibility=View.VISIBLE

            val myuser = User(name,surname,email,password)

            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){
                        task ->
                        if(task.isComplete){
                            val user:FirebaseUser?=auth.currentUser
                            verifyEmail(user)

                            dbReference.add(myuser)
                            action()
                        }
                    }
        }else{
            Toast.makeText(this,getString(R.string.error_empty_fields),Toast.LENGTH_SHORT).show()
        }
    }
    private fun action(){
        startActivity(Intent(this,LoginActivity::class.java))
    }
    //Correo de verificación
    private fun verifyEmail(user: FirebaseUser?){
        user?.sendEmailVerification()
                ?.addOnCompleteListener(this){
                    task ->

                    if(task.isComplete){
                        Toast.makeText(this,"Se ha enviado un correo de verifiación",Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this,"Error al mandar el correo de verifiación",Toast.LENGTH_SHORT).show()
                    }
                }
    }


}