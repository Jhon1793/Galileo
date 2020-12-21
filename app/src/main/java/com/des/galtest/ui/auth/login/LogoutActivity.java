package com.des.galtest.ui.auth.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.des.galtest.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {
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
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        mAuth.signOut();

        startActivity(new Intent(LogoutActivity.this, LoginActivity.class));

    }
}