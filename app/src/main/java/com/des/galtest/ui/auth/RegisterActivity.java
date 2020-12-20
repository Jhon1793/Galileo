package com.des.galtest.ui.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.des.galtest.ui.main.HomeActivity;
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
import com.hbb20.CountryCodePicker;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText Email,Password,telefono;
    private GoogleSignInClient mGoogleSingInClient;
    private final String TAG="RegisterActivity";
    private final int RC_SIGN_IN=1;
    private CountryCodePicker ccp;
    private Button btmnRegistro;
    CallbackManager callbackManager;
    private LoginButton loginButton;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Email = findViewById(R.id.emaillog);
        telefono = findViewById(R.id.edtNumero);
        Password = findViewById(R.id.passwordlog);
        telefono = findViewById(R.id.edtNumero);
        ccp = (CountryCodePicker) findViewById(R.id.ccp1);
        ccp.registerCarrierNumberEditText(telefono);
        btmnRegistro = (Button) findViewById(R.id.btmnRegistroNumero);
        btmnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("telefono", ccp.getFullNumberWithPlus().replace(" ", ""));
                startActivity(intent);
            }
        });
        SignInButton btngoogle = findViewById(R.id.btngoogle);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSingInClient = GoogleSignIn.getClient(this, gso);
        btngoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        /// AppEventsLogger.activateApp(this);

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "here we go3");
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
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if(user!=null){
                    updateUI(user);
                }else{
                    updateUI(null);
                }
            }
        };

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
        AuthCredential credential= FacebookAuthProvider.getCredential(Token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Ingreso con Facebook:Exitoso");
                    Toast.makeText(RegisterActivity.this, " Exitoso", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    Log.d(TAG,"LLegamos"+user);
                    startActivity(new Intent(RegisterActivity.this, LogoutActivity.class));

                }else{
                    Log.d(TAG, "Ingreso con Facebook:Fallido"+task.getException());
                    Toast.makeText(RegisterActivity.this, " Autentificacion Fallida", Toast.LENGTH_SHORT).show();
                    updateUI(null);

                }
            }
        });
    }


    private void signIn(){
        Intent signInIntent=mGoogleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(GoogleSignIn.getSignedInAccountFromIntent(data)!=null) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completetask) {
        try {
            GoogleSignInAccount acc = completetask.getResult(ApiException.class);
            Toast.makeText(RegisterActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(RegisterActivity.this, "Registro Erroneo", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential=GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, " Exitoso", Toast.LENGTH_SHORT).show();
                    FirebaseUser currentUser=mAuth.getCurrentUser();
                    updateUI(currentUser);
                    startActivity(new Intent(RegisterActivity.this, LogoutActivity.class));
                }else{
                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(RegisterActivity.this, "Bienvenido Usuario"+name + email, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this, " USUARIO CREADO CORRECTAMENTE",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ERROR", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "A "+task.getException(),
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
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser !=null){
            updateUI(currentUser);
            startActivity(new Intent(RegisterActivity.this, LogoutActivity.class));
        }else{
            updateUI(null);
            mAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener !=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}

 