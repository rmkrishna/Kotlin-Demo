package com.muthu.salesmanager.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.User


class LoginActivity : BaseActivity(), View.OnClickListener {

    private val TAG = LoginActivity::class.java.simpleName

    private var mDatabase: DatabaseReference? = null

    private val RC_SIGN_IN = 9001

    // [START declare_auth]
    private var mAuth: FirebaseAuth? = null
    // [END declare_auth]

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        mDatabase = FirebaseDatabase.getInstance().reference

        var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance()

        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        mAuth?.let {
            var firBaseUser: FirebaseUser? = it.currentUser;

            updateSignIn(firBaseUser)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                updateSignIn(null)
            }


        }
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId()!!)

        showProgressDialog()

        var authCredential: AuthCredential = GoogleAuthProvider.getCredential(acct.idToken, null);
        mAuth!!.signInWithCredential(authCredential).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                updateSignIn(mAuth?.currentUser)
            } else {
                Log.w(TAG, "signInWithCredential:failure", it.exception)
                updateSignIn(null)
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }

    fun signIn() {
        var signInIntent = mGoogleSignInClient?.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    override fun onClick(v: View?) {
        var id = v?.id;

        if (id == R.id.sign_in_button) {
            signIn()
        }
    }

    fun updateSignIn(user: FirebaseUser?) {
        hideProgressDialog()

        user?.let {

            var email: String = it.email as String;
            var name: String = it.displayName as String;


            var user: User = User(name, email, 1);

            mDatabase?.child("users")?.child(it.uid)?.setValue(user)

            startActivity(Intent(this, MainActivity::class.java));

            finish()
        }
    }
}
