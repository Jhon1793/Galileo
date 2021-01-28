package com.des.galtest.ui.crud.FireStore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.des.galtest.R;
import com.des.galtest.ui.crud.CrudGalileoViewModel;
import com.des.galtest.utils.LoadingDialog;
import com.des.galtest.utils.ViewModelFactory;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class NoteActivity extends DaggerAppCompatActivity {
    private static final String TAG = "AddNoteActivity";
    private CrudFireStoreViewModel crudFireStoreViewModel ;
    TextView edtTitle;
    TextView edtContent;
    Button btAdd;
    String id = "";
    @Inject
    LoadingDialog loadingDialog;

    @Inject
    ViewModelFactory providerFactory;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initview();
        getDate();
        crudFireStoreViewModel= new ViewModelProvider(getViewModelStore(), providerFactory).get(CrudFireStoreViewModel.class);
        subscribeObservers();
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edtTitle.getText().toString();
                String content = edtContent.getText().toString();

                if (title.length() > 0) {
                    if (id.length() > 0) {
                        updateNote(id, title, content);
                    } else {
                        addNote(title, content);
                    }
                }

                finish();
            }
        });

    }

    private void addNote(String title, String content) {
        if(title !=null &content!=null){
            crudFireStoreViewModel.addFireStore(title,content);

            Toast.makeText(getApplicationContext(), "Ingreso exitoso", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(), "Rellene todos los datos", Toast.LENGTH_SHORT).show();

        }

    }

    private void subscribeObservers() {
        crudFireStoreViewModel.observeCrud().observe(this, stateResource -> {
            if(stateResource != null){
                switch (stateResource.status){
                    case LOADING:
                        loadingDialog.show(getSupportFragmentManager(),"loadingDialog");
                        break;
                    case SUCCESS:
                        loadingDialog.dismiss();
                        ///moveToHomeActivity();
                        // listarDatos();
                        break;
                    case ERROR:
                        loadingDialog.dismiss();
                        Log.d("CRUD", "subscribeObservers: Error");
                        break;
                }
            }
        });
    }

    private void updateNote(String id, String title, String content) {
        if(id!=null && title !=null &content!=null){
            crudFireStoreViewModel.updateFireStore(id,title,content);

            Toast.makeText(getApplicationContext(), "Actualizacion exitosa", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(), "Rellene todos los datos", Toast.LENGTH_SHORT).show();

        }
    }

    private void getDate() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("UpdateNoteId");
            edtTitle.setText(bundle.getString("UpdateNoteTitle"));
            edtContent.setText(bundle.getString("UpdateNoteContent"));
        }
    }

    private void initview() {
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btAdd = findViewById(R.id.btAdd);
    }
}
