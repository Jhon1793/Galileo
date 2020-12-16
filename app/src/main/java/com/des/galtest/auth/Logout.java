package com.des.galtest.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.des.galtest.R;
import com.google.firebase.auth.FirebaseAuth;

public class Logout extends AppCompatActivity {
    private Button logout;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        init();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CerrarSesion();
            }
        });

    }

    private void init() {
        logout =findViewById(R.id.btnCerrarSesion);
        mAuth=FirebaseAuth.getInstance();
    }

    private void CerrarSesion() {
        mAuth.signOut();
        startActivity(new Intent(Logout.this, Login.class));
    }
}