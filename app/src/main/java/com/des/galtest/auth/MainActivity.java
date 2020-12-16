package com.des.galtest.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.des.galtest.ui.HomeActivity;
import com.des.galtest.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText Email,Password;
    private GoogleSignInClient mGoogleSingInClient;
    private final String TAG="MainActivity";
    private final int RC_SIGN_IN=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        Email = findViewById(R.id.emaillog);
        Password=findViewById(R.id.passwordlog);

        SignInButton btngoogle = findViewById(R.id.btngoogle);

        GoogleSignInOptions  gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSingInClient= GoogleSignIn.getClient(this, gso);

        btngoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        LoginButton loginButton = findViewById(R.id.login_button);

        CallbackManager callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"exitoso"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
                
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"Cancelado");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"error"+error);
            }
        });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mAuth.signOut();
                }
            }
        };
    }


    private void handleFacebookToken(AccessToken Token) {
        Log.d(TAG,"handleFacebookToken "+Token);
        AuthCredential credential= new FacebookAuthProvider().getCredential(Token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Ingreso con Facebook:Exitoso");
                    Toast.makeText(MainActivity.this, " Exitoso", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }else{
                    Log.d(TAG, "Ingreso con Facebook:Fallido"+task.getException());
                    Toast.makeText(MainActivity.this, " Autentificacion Fallida", Toast.LENGTH_SHORT).show();
                    updateUI(null);

                }
            }
        });
    }

    @Override
     protected void onStart(){
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        updateUI(currentUser);
     }
    private void signIn(){
        Intent signInIntent=mGoogleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completetask) {
        try {
            GoogleSignInAccount acc = completetask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(MainActivity.this, "Registro Erroneo", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential=GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, " Exitoso", Toast.LENGTH_SHORT).show();
                    FirebaseUser currentUser=mAuth.getCurrentUser();
                    updateUI(currentUser);
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));

                }else{
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=null){
            String  name=account.getDisplayName();
            String email=account.getEmail();
            String personId=account.getId();
            Toast.makeText(MainActivity.this, "Buenvenido Usuario"+name + email, Toast.LENGTH_SHORT).show();
        }


        Log.i("User:"," "+currentUser);
    }
    public void createEmailAndPassword(String email,String  password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("EXITO", "createUserWithEmail:success");
                            Toast.makeText(MainActivity.this, " USUARIO CREADO CORRECTAMENTE",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ERROR", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "A "+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void buttonPress(View view){
        String email=Email.getText().toString();
        String password=Password.getText().toString();
        if(!email.isEmpty()&&!password.isEmpty()){
                createEmailAndPassword(email,password);
        }else{
            Toast.makeText(this,"Por favor ingrese todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    public void login(View view){
        startActivity(new Intent(MainActivity.this, Login.class));
    }
}

 