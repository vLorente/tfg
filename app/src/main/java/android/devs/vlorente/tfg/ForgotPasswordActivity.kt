package android.devs.vlorente.tfg

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var txtEmail:EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        txtEmail = findViewById(R.id.forgot_password_email)
        progressBar = findViewById(R.id.forgot_password_progressBar)
        auth = FirebaseAuth.getInstance()
    }

    fun send(view: View){
        progressBar.visibility = View.VISIBLE
        val email = txtEmail.text.toString()

        if(!TextUtils.isEmpty(email)){
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(this){
                        task ->
                        if(task.isSuccessful) {
                            startActivity(Intent(this,LoginActivity::class.java))
                        }else{
                            progressBar.visibility = View.GONE
                            Toast.makeText(this,getString(R.string.error_password_reset),Toast.LENGTH_SHORT).show()
                        }
            }
        }else{
            progressBar.visibility = View.GONE
            Toast.makeText(this,R.string.error_empty_fields,Toast.LENGTH_SHORT).show()
        }
    }

}
