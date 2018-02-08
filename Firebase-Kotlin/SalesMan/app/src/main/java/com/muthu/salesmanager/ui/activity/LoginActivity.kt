package com.muthu.salesmanager.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.muthu.salesmanager.R
import com.muthu.salesmanager.model.User
import com.muthu.salesmanager.util.PermissionUtil


class LoginActivity : BaseActivity(), View.OnClickListener {

    private val TAG = LoginActivity::class.java.simpleName

    private var mDatabase: DatabaseReference? = null

    private val RC_SIGN_IN = 9001

    // [START declare_auth]
    private var mAuth: FirebaseAuth? = null
    // [END declare_auth]

    private var mGoogleSignInClient: GoogleSignInClient? = null

    private var mEmailField: EditText? = null;
    private var mPasswordField: EditText? = null;

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

        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener(this)
        findViewById<Button>(R.id.email_sign_in_button).setOnClickListener(this)
        findViewById<Button>(R.id.email_create_account_button).setOnClickListener(this)

        PermissionUtil.hasExternalStoragePermission(this, 100)
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

    private fun signIn() {
        var signInIntent = mGoogleSignInClient?.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private fun emailSignIn(email: String, password: String) {
        println("emailSignIn " + email + " password " + password)

        if (!validateForm(email, password)) {
            return
        }

        showProgressDialog()

        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")

                updateSignIn(mAuth?.currentUser)
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                updateSignIn(null)
            }

            hideProgressDialog()
        }

    }

    private fun emailSignUp(email: String, password: String) {
        println("emailSignUp " + email)

        if (!validateForm(email, password)) {
            return
        }

        showProgressDialog()

        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")

                updateSignIn(mAuth?.currentUser)
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                updateSignIn(null)
            }

            hideProgressDialog()
        }
    }

    override fun onClick(v: View?) {
        var id = v?.id;

        when (id) {
            R.id.sign_in_button -> signIn()
            R.id.email_sign_in_button -> emailSignIn(mEmailField?.text.toString(), mPasswordField?.text.toString())
            R.id.email_create_account_button -> emailSignUp(mEmailField?.text.toString(), mPasswordField?.text.toString())
        }
    }

    fun updateSignIn(user: FirebaseUser?) {
        hideProgressDialog()

        user?.let {

            var email: String = it.email as String;

            var name: String? = it.displayName;

            if (name == null) {
                name = email
            }

            var user: User = User(name, email, 1);

            mDatabase?.child("users")?.child(it.uid)?.setValue(user)

            startActivity(Intent(this, MainActivity::class.java));

            finish()
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var valid = true

        if (TextUtils.isEmpty(email)) {
            mEmailField?.setError("Required.")
            valid = false
        } else {
            mEmailField?.setError(null)
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField?.setError("Required.")
            valid = false
        } else {
            mPasswordField?.setError(null)
        }

        return valid
    }
}
