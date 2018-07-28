package android.devs.vlorente.tfg

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"
    private val manager = supportFragmentManager
    private var googleApiClient: GoogleApiClient? = null
    private lateinit var firebaseAuth: FirebaseAuth




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

        //Listener que escucha los cambios en la atutenticación de firebase
        firebaseAuth= FirebaseAuth.getInstance()


        val navigation: BottomNavigationView = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


    }

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        loginCheck(currentUser)
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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_player ->{
                ShowFragmentPlayer()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_social -> {
                ShowFragmentSocial()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {


            R.id.nav_profile -> {
                ShowProfileActivity()
            }

            R.id.nav_config -> {

            }
            R.id.nav_logOut -> {
                alert(getString(R.string.close_session)) {
                    title = "Cerrar sesión"
                    yesButton {
                        toast("Cerrando sesión")
                        logOut()
                    }
                    noButton { }
                }.show()

            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback { status ->
            if (status.isSuccess) {
                ShowLoginActivity()
            } else {
                toast(R.string.not_logout_google)
            }
        }
    }

    private fun loginCheck(user: FirebaseUser?) {
        if (user != null) {
            ShowFragmentPlayer()
        } else {
            ShowLoginActivity()
        }
    }

    private fun ShowFragmentSocial(){
        val transaction = manager.beginTransaction()
        val fragment = FragmentTimeLine()
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

    private fun ShowProfileActivity(){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun ShowLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


}
