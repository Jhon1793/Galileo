package com.des.galtest.ui.main;

import android.content.Intent;
import android.os.Bundle;

import com.des.galtest.R;
import com.des.galtest.ui.crud.CrudGalileoActivity;
import com.des.galtest.ui.crud.FireStore.CrudFireStore;
import com.des.galtest.ui.crud.Storage.StorageCrudActivity;
import com.des.galtest.ui.posts.PostsActivity;

import java.util.zip.ZipEntry;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        findViewById(R.id.btnPublicaicones)
                .setOnClickListener( v -> startActivity(new Intent(this, PostsActivity.class)));
        findViewById(R.id.btncrud).
                setOnClickListener(view -> startActivity(new Intent(this, CrudGalileoActivity.class)));
        findViewById(R.id.btncrudImg).
                setOnClickListener(view -> startActivity(new Intent(this, StorageCrudActivity.class)));
        findViewById(R.id.btncrudFire).
                setOnClickListener(view -> startActivity(new Intent(this, CrudFireStore.class)));
    }


}