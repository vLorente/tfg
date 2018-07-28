package android.devs.vlorente.tfg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.devs.vlorente.tfg.Beans.UserModel
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.toast


/**
 * Created by Valentín Lorente Jiménez on 14/07/2018.
 * Copyright © 2017 vLorente. All rights reserved.
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener {


    private val TAG ="LoginActivity"

    //Init views
    lateinit var googleSignInButton: SignInButton
    lateinit var txtEmail: EditText
    lateinit var txtPassword: EditText
    lateinit var progressBar: ProgressBar
    lateinit var dbReference : CollectionReference

    //Request codes
    val GOOGLE_LOG_IN_RC = 1

    // Google API Client object.
    var googleApiClient: GoogleApiClient? = null
    // Firebase Auth Object.
    var firebaseAuth: FirebaseAuth? = null

    private val PERMISSION_REQUEST_CODE = 12
    private val permissionsRequired = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleSignInButton = findViewById<View>(R.id.googleButton) as SignInButton
        txtEmail = findViewById(R.id.login_email)
        txtPassword = findViewById(R.id.login_password)
        progressBar = findViewById(R.id.login_progressBar)
        dbReference = FirebaseFirestore.getInstance().collection("Users")

        googleSignInButton.setOnClickListener(this)
        firebaseAuth = FirebaseAuth.getInstance()
        // Configure Google Sign In
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // Creating and Configuring Google Api Client.
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this  /* OnConnectionFailedListener */) { }
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()




        //Registro
        val registerButton = findViewById<Button>(R.id.login_register_button)
        registerButton.setOnClickListener { view: View? ->
            ShowRegister()
        }

        setupPermissions()

    } //cierre onCreate


    //Funciones OnCLick
    override fun onClick(p0: View?) {
        googleLogin()
    }

    fun forgotPassword(view: View) {
        ShowForgotPassword()
    }
    fun login(view: View){
        loginUser()
    }
    //fin funciones onClick

    //Login con email y contraseña
    private fun loginUser(){
        val email:String = txtEmail.text.toString()
        val password:String = txtPassword.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.visibility = View.VISIBLE

            firebaseAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {
                            ShowMain()
                        }else{
                            progressBar.visibility = View.GONE
                            txtPassword.text.clear()
                            Toast.makeText(this,getString(R.string.error_data_login), Toast.LENGTH_LONG).show()
                        }
                    }
        }else{
            progressBar.visibility = View.GONE
            Toast.makeText(this,R.string.error_empty_fields,Toast.LENGTH_SHORT).show()
        }
    }

    private fun googleLogin() {
        Log.i(TAG, "Starting Google LogIn Flow.")
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, GOOGLE_LOG_IN_RC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "Got Result code ${requestCode}.")
        if (requestCode == GOOGLE_LOG_IN_RC) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.i(TAG, "With Google LogIn, is result a success? ${result.isSuccess}.")
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(result.signInAccount!!)
            } else {
                Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        progressBar.visibility = View.VISIBLE
        Log.i(TAG, "Authenticating user with firebase.")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            Log.i(TAG, "Firebase Authentication, is result a success? ${task.isSuccessful}.")
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.i(TAG, firebaseAuth?.uid!!)
                CreateUserGoogleSign(firebaseAuth?.uid!!)
                ShowMain()
            } else {
                progressBar.visibility = View.GONE
                // If sign in fails, display a message to the user.
                Log.e(TAG, getString(R.string.error_firebase_google_autentication))
                Toast.makeText(this,getString(R.string.error_firebase_google_autentication),Toast.LENGTH_LONG).show()
            }
        }
    }



    private fun ShowRegister(){
        startActivity(Intent(this,RegisterActivity::class.java))
    }
    private fun ShowMain(){
        var intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    private fun ShowForgotPassword(){
        startActivity(Intent(this,ForgotPasswordActivity::class.java))
    }

    private fun CreateUserGoogleSign(uid:String){

        dbReference.document(uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                   // Log.d(FragmentActivity.TAG, "DocumentSnapshot data: " + document.data!!)
                } else {
                    val user:FirebaseUser? = firebaseAuth?.currentUser
                    val myuser = UserModel(user?.displayName.toString(),user?.email.toString(),user?.uid)
                    dbReference.document(myuser.uid!!).set(myuser)
                }
            } else {
                //Log.d(FragmentActivity.TAG, "get failed with ", task.exception)
            }
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty()){
                grantResults.forEach {
                    i ->
                    if(grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        toast("Permiso $i concedidos")
                    }else {
                        toast("Permiso $i no concedidos")
                    }
                }
            }
        }
    }

    private fun setupPermissions(){
        //Pedir permisos
        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(permissionsRequired[0],permissionsRequired[1]), PERMISSION_REQUEST_CODE)
        }
    }

}