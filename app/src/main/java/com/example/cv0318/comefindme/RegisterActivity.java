package com.example.cv0318.comefindme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity
{
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ProgressDialog loadingBar;
    private FirebaseAuth m_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        m_auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirm);

        btnRegister = findViewById(R.id.btnRegister);

        loadingBar = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createNewAccount();
            }
        });
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

    private void sendUserToMainActivity()
    {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void createNewAccount()
    {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (email.isEmpty())
        {
            Toast.makeText(this, "Please enter your email address...", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty())
        {
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        }
        else if (confirmPassword.isEmpty())
        {
            Toast.makeText(this, "Please confirm your password...", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirmPassword))
        {
            Toast.makeText(this, "The password entered does not match the confirmed password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait while your account is being created...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            m_auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            loadingBar.dismiss();

                            if (task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this, "Authentication was successful...", Toast.LENGTH_SHORT).show();

                                sendUserToSetupActivity();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendUserToSetupActivity()
    {
        Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
