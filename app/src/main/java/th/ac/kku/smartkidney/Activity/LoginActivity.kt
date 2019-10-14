package th.ac.kku.smartkidney

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_splash.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private var mGoogleSignInClient1: GoogleSignInClient? = null
    private var mGoogleSignInClient: GoogleApiClient? = null
    private val RC_SIGN_IN = 1
    private val RC_REGISTER = 2
    private var mAuth: FirebaseAuth? = null
    private lateinit var callbackManager: CallbackManager

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        textSingUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        //------------------Prepare Login--------------------
        if (ConnectivityHelper.isConnectedToNetwork(this)) {

        } else {

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.connect_falied_dialog)
            dialog.setCancelable(false)

            val button1 = dialog.findViewById<TextView>(R.id.button_dialog)
            button1.setOnClickListener {
                dialog.cancel()
                finish()
                exitProcess(0)
            }
            dialog.show()
        }

        val callbackId = 42
        checkPermissions(
            callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        )

        //---------------------Login logic---------------------
        mAuth = FirebaseAuth.getInstance()

            //-------------------Facebook Auth--------------
//            FacebookSdk.sdkInitialize(applicationContext)
//            AppEventsLogger.activateApp(this)
//            callbackManager = CallbackManager.Factory.create()

        callbackManager = CallbackManager.Factory.create()

        facebook_login_bt.setReadPermissions("email", "public_profile")
        facebook_login_bt.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
            }
        })

        /////////////////////////////

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient1 = GoogleSignIn.getClient(this, gso)

        mGoogleSignInClient = GoogleApiClient.Builder(applicationContext)
            .enableAutoManage(
                this
            ) { Toast.makeText(this@LoginActivity, "You got Error.", Toast.LENGTH_LONG).show() }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        google_login_bt.setOnClickListener { signIn() }

    }

    private fun facbookButtonOnclick() {
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookToken(result!!.accessToken)
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                }
            })
    }

    private fun handleFacebookToken(accessToken: AccessToken?) {
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val myUserObj = mAuth!!.currentUser
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                }
        }
        if (requestCode == RC_REGISTER) {
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, AgreementActivity::class.java)
                startActivity(intent)
            }else{
                signOut()
            }
        }

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient1!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener { }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->

                if (task.isSuccessful) {
                        loginApiCall(mAuth!!.currentUser!!.email!!)
                } else {
                    Toast.makeText(
                        this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkPermissions(callbackId: Int, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions =
                permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED
        }
        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId)
    }


    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user =  mAuth!!.currentUser
                    loginApiCall(mAuth!!.currentUser!!.email!!)


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }

            }
    }

    @SuppressLint("CheckResult")
    private fun loginApiCall(email: String) {
        loginProgressBar.visibility = View.VISIBLE
        google_login_bt.elevation = 0f
        facebook_login_bt.elevation = 0f

        val observable = ApiService.loginApiCall().login(email)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ loginResponse ->
                ApiObject.instant.firstLogin = loginResponse.firstLogin

                if (ApiObject.instant.firstLogin!!) {

                } else {
                    ApiObject.instant.user = loginResponse.users
                    val id = loginResponse.users.id
                    val intent = Intent(this, HomeActivity::class.java)
                    val apiHandler = ApiHandler(this,loginProgressBar,intent)
                    apiHandler.comboGetBloodPressure(id)
                }

            }, { error ->
                println(error.message.toString())
                loginProgressBar.visibility = View.INVISIBLE
                google_login_bt.elevation = 5f
                facebook_login_bt.elevation = 5f
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivityForResult(intent, RC_REGISTER)
            }
            )
    }

}
