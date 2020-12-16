package com.des.galtest.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.des.galtest.R;
import com.des.galtest.ui.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private EditText Emaillog, Passwordlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        Emaillog = findViewById(R.id.emaillog);
        Passwordlog = findViewById(R.id.passwordlog);
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
        Log.i("User:"," "+currentUser);
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