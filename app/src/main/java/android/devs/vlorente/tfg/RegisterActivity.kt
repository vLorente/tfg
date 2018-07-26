package android.devs.vlorente.tfg

import android.content.Intent
import android.devs.vlorente.tfg.Beans.UserBean
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
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtPassword2: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var database: FirebaseFirestore
    private lateinit var dbReference: CollectionReference
    private lateinit var auth: FirebaseAuth
    private lateinit var btbRegister: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtName = findViewById(R.id.register_name)
        txtEmail = findViewById(R.id.register_email)
        txtPassword = findViewById(R.id.register_password)
        txtPassword2 = findViewById(R.id.register_password2)

        btbRegister = findViewById(R.id.register_button)

        progressBar = findViewById(R.id.progressBar)

        database = FirebaseFirestore.getInstance()
        dbReference = database.collection("/Users")
        auth = FirebaseAuth.getInstance()

        btbRegister.setOnClickListener { _: View? ->
            createAccount()
        }


    }

    private fun createAccount() {
        val name:String = txtName.text.toString()
        val email:String = txtEmail.text.toString()
        val password:String = txtPassword.text.toString()
        val password2:String = txtPassword2.text.toString()

        if(!TextUtils.isEmpty(name)  &&!TextUtils.isEmpty(email) &&!TextUtils.isEmpty(password) && !TextUtils.isEmpty(password2)){
            progressBar.visibility=View.VISIBLE

            if (TextUtils.equals(password,password2)){
                val myuser = UserBean(name,email,null)

                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(this){
                            task ->
                            if(task.isComplete){
                                val user:FirebaseUser?=auth.currentUser
                                verifyEmail(user)
                                myuser.uid = auth.currentUser?.uid
                                dbReference.document(auth.currentUser?.uid!!).set(myuser)
                                action()
                            }

                        }
            } else{
                txtPassword.text.clear()
                txtPassword2.text.clear()
                Toast.makeText(this,getString(R.string.error_passwords_not_match),Toast.LENGTH_SHORT).show()
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