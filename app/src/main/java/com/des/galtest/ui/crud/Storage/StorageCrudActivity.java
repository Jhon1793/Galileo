package com.des.galtest.ui.crud.Storage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.des.galtest.R;
import com.des.galtest.model.Upload;
import com.des.galtest.ui.crud.CrudGalileoViewModel;
import com.des.galtest.utils.LoadingDialog;
import com.des.galtest.utils.ViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class StorageCrudActivity extends DaggerAppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private StorageCrudViewModel storageCrudViewModel;
    @Inject
    LoadingDialog loadingDialog;

    @Inject
    ViewModelFactory providerFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_crud);
        initView();
        storageCrudViewModel= new ViewModelProvider(getViewModelStore(),  providerFactory).get(StorageCrudViewModel.class);
      mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
       mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(StorageCrudActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }

        });
        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });
    }
    private void subscribeObservers() {
        storageCrudViewModel.observeCrud().observe(this, stateResource -> {
            if(stateResource != null){
                switch (stateResource.status){
                    case LOADING:
                        loadingDialog.show(getSupportFragmentManager(),"loadingDialog");
                        break;
                    case SUCCESS:
                        loadingDialog.dismiss();
                        ///moveToHomeActivity();
                        Toast.makeText(StorageCrudActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                        break;
                    case ERROR:
                        loadingDialog.dismiss();
                        Log.d("CRUD", "subscribeObservers: Error");
                        break;
                }
            }
        });


    }
    private void initView() {
        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mStorageRef= FirebaseStorage.getInstance().getReference("uploads");

    }
    private void openImagesActivity() {
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageView);

        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile() {
        if (mImageUri != null) {
            if(mEditTextFileName.getText().toString()!=null){
                storageCrudViewModel.addImageContent(mEditTextFileName.getText().toString(),
                        mUploadTask,mImageUri,getContentResolver());
                clearbox();
                Toast.makeText(StorageCrudActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(this, "Inserte un nombre", Toast.LENGTH_SHORT).show();
            }

            /*//mUploadTask = fileReference.putFile(mImageUri);
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(StorageCrudActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            storageCrudViewModel.addImageContent(mEditTextFileName.getText().toString(),taskSnapshot);
                            clearbox();
                           *//* Toast.makeText(StorageCrudActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),downloadUrl.toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);*//*

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(StorageCrudActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    })
                    ;*/
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearbox() {
        mEditTextFileName.setText("");
       Picasso.get().load("null").into(mImageView);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


}