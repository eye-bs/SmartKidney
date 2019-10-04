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
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
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

        setMarginBg()

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
        facebook_login_bt.setOnClickListener { facbookButtonOnclick() }

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

    private fun setMarginBg() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        //obs size 1214 * 619
        var width = displayMetrics.widthPixels
        if (width > 1214) {
            width -= 1214
        } else width = 1214 - width

        val param = objLogin.layoutParams as RelativeLayout.LayoutParams
        param.setMargins(0, width * -1, 0, 0)
        objLogin.layoutParams = param
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (!ApiObject.instant.firstLogin!!){
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(Constant.TAG, "Google sign in failed", e)
                }
            }else{
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
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
                Log.d(Constant.TAG, "login is " + task.isSuccessful)

                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivityForResult(intent, RC_REGISTER)
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

}
