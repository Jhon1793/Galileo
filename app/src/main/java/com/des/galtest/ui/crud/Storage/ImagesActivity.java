package com.des.galtest.ui.crud.Storage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.des.galtest.R;
import com.des.galtest.model.Upload;
import com.des.galtest.utils.LoadingDialog;
import com.des.galtest.utils.ViewModelFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class ImagesActivity extends DaggerAppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;
    private FirebaseStorage mStorage;
    private StorageCrudViewModel storageCrudViewModel;
    @Inject
    LoadingDialog loadingDialog;

    @Inject
    ViewModelFactory providerFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        initView();
        storageCrudViewModel= new ViewModelProvider(getViewModelStore(),  providerFactory).get(StorageCrudViewModel.class);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImagesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle);
        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(ImagesActivity.this);
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(ImagesActivity.this, "Select Item"+position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(ImagesActivity.this, "Update Item "+position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDeleteClick(int position) {
        Toast.makeText(ImagesActivity.this, "Delete Item"+position, Toast.LENGTH_SHORT).show();
        if(mUploads !=null)
        storageCrudViewModel.deleteImage(position,mUploads);
        Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
   /*     Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });*/

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}