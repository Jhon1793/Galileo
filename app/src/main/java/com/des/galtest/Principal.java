package com.des.galtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Principal extends AppCompatActivity {
    EditText  mensaje;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        mensaje=findViewById(R.id.editTextTextMultiLine);
        mAuth=FirebaseAuth.getInstance();

    }
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser=mAuth.getInstance().getCurrentUser();
        if(currentUser !=null){
            String email=currentUser.getEmail();
            String  name=currentUser.getDisplayName();
            mensaje.setText("Correo"+email+"  Nombre:"+name);
        }


    }
}