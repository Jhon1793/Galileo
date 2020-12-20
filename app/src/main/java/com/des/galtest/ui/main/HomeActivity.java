package com.des.galtest.ui.main;

import android.content.Intent;
import android.os.Bundle;

import com.des.galtest.R;
import com.des.galtest.ui.posts.PostsActivity;

import dagger.android.support.DaggerAppCompatActivity;

public class HomeActivity extends DaggerAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        findViewById(R.id.btnPublicaicones)
                .setOnClickListener( v -> startActivity(new Intent(this, PostsActivity.class)));
    }


}