package com.example.cv0318.comefindme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = String.format("%s_TAG", LoginActivity.class.getSimpleName());
    private Button btnLogin;
    private EditText etEmail, etPassword;
    private TextView tvRegisterLink;
    private ProgressDialog loadingBar;
    private FirebaseAuth m_auth;
    private ImageView ivLoginGoogle;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        m_auth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        tvRegisterLink = findViewById(R.id.tvLoginNewAccountLink);
        ivLoginGoogle = findViewById(R.id.ivLoginGoogle);

        tvRegisterLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendUserToRegisterActivity();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                allowUserToLogin();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener()
                {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
                    {
                        Toast.makeText(LoginActivity.this, "Connection to Google Sign In Failed...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        ivLoginGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signIn();
            }
        });
    }

    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            loadingBar.setTitle("Google Sign In");
            loadingBar.setMessage("Please wait while you are being logged into your Google account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            
            if (result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "Please wait while we get the auth result...", Toast.LENGTH_SHORT).show();
            }
            else 
            {
                Toast.makeText(this, "Failed to receive auth result...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        m_auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        loadingBar.dismiss();

                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInWithCredential:success");
                            sendUserToMainActivity();
                        }
                        else
                        {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().getMessage();
                            sendUserToLoginActivity();
                            Toast.makeText(LoginActivity.this, String.format("Not authenticated: %s", message), Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void sendUserToLoginActivity()
    {
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = m_auth.getCurrentUser();

        if (currentUser != null)
        {
            sendUserToMainActivity();
        }
    }

    private void allowUserToLogin()
    {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty())
        {
            Toast.makeText(LoginActivity.this, "An email is required...", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty())
        {
            Toast.makeText(LoginActivity.this, "A password is required...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait while you are being logged into your account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            m_auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            loadingBar.dismiss();

                            if (task.isSuccessful())
                            {
                                sendUserToMainActivity();

                                Toast.makeText(LoginActivity.this, "Login was successful...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendUserToMainActivity()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToRegisterActivity()
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
