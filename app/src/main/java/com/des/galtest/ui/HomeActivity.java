package com.des.galtest.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.des.galtest.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        findViewById(R.id.btnPublicaicones)
                .setOnClickListener( v -> startActivity(new Intent(this, PublicacionesActivity.class)));
    }

}