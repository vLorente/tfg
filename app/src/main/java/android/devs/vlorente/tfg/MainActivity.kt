package android.devs.vlorente.tfg

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"
    private val manager = supportFragmentManager
    private var auth: FirebaseAuth? = null
    private var googleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //Google login silencioso
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this  /* OnConnectionFailedListener */) { }
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()
        //Fin google login

        auth = FirebaseAuth.getInstance()
        var firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            //Comprobamos que tenemos un usuario autenticado
            val user = firebaseAuth.currentUser
            if (user != null) {
                //En caso de tenerlo pintamos sus datos
                Toast.makeText(this, "Existe una sesión iniciada",Toast.LENGTH_SHORT).show()
            } else {
                //En caso de que no tengamos vamos al login
                ShowLoginActivity()
            }
        }

        ShowFragmentMain()

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {


            R.id.nav_perfil -> {
                ShowFragmentMain()
            }
            R.id.nav_gallery -> {
                ShowFragmentPlayer()
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_config -> {

            }
            R.id.nav_logOut -> {
                logOut()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback { status ->
            if (status.isSuccess) {
                ShowLoginActivity()
            } else {
                Toast.makeText(applicationContext, R.string.not_logout_google, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ShowFragmentMain(){
        val transaction = manager.beginTransaction()
        val fragment = FragmentMain()
        transaction.replace(R.id.fragment_holder,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun ShowFragmentPlayer(){
        val transaction = manager.beginTransaction()
        val fragment = FragmentPlayer()
        transaction.replace(R.id.fragment_holder,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun ShowLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
