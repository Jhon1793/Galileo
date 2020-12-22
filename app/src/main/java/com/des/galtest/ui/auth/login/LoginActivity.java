package com.des.galtest.ui.auth.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.des.galtest.R;
import com.des.galtest.ui.auth.register.RegisterActivity;
import com.des.galtest.ui.main.MainActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends Activity {

    private FirebaseAuth mAuth;
    private TextInputEditText Emaillog, Passwordlog;
    private static final String TAG = "Login";
    private GoogleSignInClient mGoogleSingInClient;
    private final int RC_SIGN_IN = 1;
    private Button login_Facebook;
    private LoginButton login;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        findViewById(R.id.btnGoogle).setOnClickListener(v -> signIn());
        login_Facebook.setOnClickListener(view -> singInFacebook());
    }

    private void singInFacebook() {
        Log.d(TAG, "LLega");
        login.performClick();
        login.setReadPermissions("email", "public_profile");
         callbackManager = CallbackManager.Factory.create();
        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Autentication", "exitoso" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("Facebook Autentication", "Cancelado");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "error" + error);
            }
        });

    }

    private void handleFacebookToken(AccessToken Token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(Token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Ingreso con Facebook:Exitoso");
                    Toast.makeText(LoginActivity.this, " Exitoso", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    Log.d(TAG, "LLegamos" + user);
                    startActivity(new Intent(LoginActivity.this, LogoutActivity.class));

                } else {
                    Log.d(TAG, "Ingreso con Facebook:Fallido" + task.getException());
                    Toast.makeText(LoginActivity.this, " Autentificacion Fallida", Toast.LENGTH_SHORT).show();
                    updateUI(null);

                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
    }

    private void signIn() {
        Intent signInIntent = mGoogleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completetask) {
        try {
            GoogleSignInAccount acc = completetask.getResult(ApiException.class);
            assert acc != null;
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this, "Logeo Erroneo", Toast.LENGTH_SHORT).show();
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    startActivity(new Intent(LoginActivity.this, LogoutActivity.class));

                } else {
                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        Emaillog = findViewById(R.id.emaillog);
        Passwordlog = findViewById(R.id.passwordlog);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSingInClient = GoogleSignIn.getClient(this, gso);
        FacebookSdk.sdkInitialize(getApplicationContext());
        login_Facebook = findViewById(R.id.login_button1);
        login = (LoginButton) findViewById(R.id.login_button);
    }

    public void singWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Exito", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, " LOGEO EXITOSO", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, LogoutActivity.class));
                        } else {
                            Log.w("Error", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, " " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void buttonPress(View view) {
        String email = Emaillog.getText().toString();
        String password = Passwordlog.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            singWithEmailAndPassword(email, password);
        } else {
            Toast.makeText(this, "Por favor ingrese todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    private void signOut() {
        mAuth.signOut();
    }

    public void registrar(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}