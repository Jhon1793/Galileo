package com.des.galtest.auth;

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

import com.des.galtest.R;
import com.des.galtest.ui.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private EditText Emaillog, Passwordlog,Numero;
    private String phoneNumber,otpid;
    private Button Ingresar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static final String TAG = "Login";
    private GoogleSignInClient mGoogleSingInClient;
    private SignInButton btngoogle;
    private final int RC_SIGN_IN=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        if(phoneNumber!=null){
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    otpid=s;
                }
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(Login.this, " "+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            };
            initOtp();
        }


        Ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeVerification(Numero.getText().toString());
            }
        });
        btngoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }

    private void signIn() {
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
            Toast.makeText(Login.this, "Logeo Exitoso", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(Login.this, "Logeo Erroneo", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential=GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, " Exitoso", Toast.LENGTH_SHORT).show();
                    FirebaseUser currentUser=mAuth.getCurrentUser();
                    updateUI(currentUser);
                    startActivity(new Intent(Login.this, Logout.class));

                }else{
                    Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }


    private void codeVerification(String telephone) {
        if(telephone.isEmpty()){
            Toast.makeText(Login.this, "Codigo en Blanco ",Toast.LENGTH_LONG).show();

        }else if(telephone.length()!=6){
            Toast.makeText(Login.this, "Codigo invalido ",Toast.LENGTH_SHORT).show();

        }else{
            PhoneAuthCredential credential=PhoneAuthProvider.getCredential(otpid,telephone);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        Emaillog = findViewById(R.id.emaillog);
        Passwordlog = findViewById(R.id.passwordlog);
        if(getIntent().hasExtra("telefono")){
            phoneNumber=getIntent().getStringExtra("telefono").toString();

        }else{
            phoneNumber=null;
        }
        phoneNumber=null;
        Numero=findViewById(R.id.ingNumero);

        Ingresar=(Button)findViewById(R.id.btmnIngresoNumero);
         btngoogle = findViewById(R.id.btnGoogle);
        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSingInClient= GoogleSignIn.getClient(this, gso);
    }

    private void initOtp(){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(Login.this, " LOGEO EXITOSO",Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            startActivity(new Intent(Login.this, Logout.class));
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Login.this, " "+task.getException(),Toast.LENGTH_SHORT).show();
                                updateUI(null);

                            }
                        }
                    }
                });
    }
    public void singWithEmailAndPassword(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Exito", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, " LOGEO EXITOSO",Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            startActivity(new Intent(Login.this, Logout.class));
                        } else {
                            Log.w("Error", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, " "+task.getException(),Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void signWithGoogle(GoogleSignInAccount acc) {
        AuthCredential authCredential= GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, " Exitoso", Toast.LENGTH_SHORT).show();
                    FirebaseUser currentUser=mAuth.getCurrentUser();
                    updateUI(currentUser);
                    startActivity(new Intent(Login.this, Logout.class));

                }else{
                    Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String phone = currentUser.getPhoneNumber();
            Toast.makeText(Login.this, "Buenvenido Usuario" + name + email + phone, Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonPress(View view){
        String email= Emaillog.getText().toString();
        String password = Passwordlog.getText().toString();
        if(!email.isEmpty()&&!password.isEmpty()){
            singWithEmailAndPassword(email,password);
        }else{
            Toast.makeText(this,"Por favor ingrese todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    private void signOut() {
        mAuth.signOut();
    }

    public void registrar(View view){
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
}