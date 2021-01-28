package com.des.galtest.ui.crud.FireStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.des.galtest.R;
import com.des.galtest.model.Note;
import com.des.galtest.ui.crud.CrudGalileoActivity;
import com.des.galtest.utils.LoadingDialog;
import com.des.galtest.utils.ViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class CrudFireStore extends DaggerAppCompatActivity {
    private CrudFireStoreViewModel crudFireStoreViewModel ;
    private static final String TAG = "FireStore";

    private  RecyclerView recyclerView = null;
    private NoteRecyclerViewAdapter mAdapter;
    private ImageView add;
    private FirebaseFirestore firestoreDB;
    private ListenerRegistration firestoreListener;
    private static Context am;

    @Inject
    LoadingDialog loadingDialog;

    @Inject
    ViewModelFactory providerFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_fire_store);
        crudFireStoreViewModel= new ViewModelProvider(getViewModelStore(), providerFactory).get(CrudFireStoreViewModel.class);
        initview();
        loadNotesList();

        subscribeObservers();

       firestoreListener = firestoreDB.collection("notes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed!", e);
                            return;
                        }

                        List<Note> notesList = new ArrayList<>();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            Note note = doc.toObject(Note.class);
                            note.setId(doc.getId());
                            notesList.add(note);
                        }

                        mAdapter = new NoteRecyclerViewAdapter(notesList, getApplicationContext(), crudFireStoreViewModel);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
        add.setOnClickListener(view -> startActivity(new Intent(this, NoteActivity.class)));

    }

    private void loadNotesList2() {
        List<Note> notesList = new ArrayList<>();
        notesList=crudFireStoreViewModel.list2();
        mAdapter= new NoteRecyclerViewAdapter(notesList,getApplicationContext(),crudFireStoreViewModel);
        Log.d(TAG, "El contenido de la lista 2es .."+notesList.size());
        recyclerView.setAdapter(mAdapter);
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
                        loadNotesList();
                        break;
                    case ERROR:
                        loadingDialog.dismiss();
                        Log.d("CRUD", "subscribeObservers: Error");
                        break;
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void loadNotesList() {
        firestoreDB.collection("notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Note> notesList = new ArrayList<>();

                            for (DocumentSnapshot doc : task.getResult()) {
                                Note note = doc.toObject(Note.class);
                                note.setId(doc.getId());
                                notesList.add(note);
                            }

                            //mAdapter = new NoteRecyclerViewAdapter(notesList, getApplicationContext(), firestoreDB);
                            mAdapter= new NoteRecyclerViewAdapter(notesList,getApplicationContext(),crudFireStoreViewModel);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
       /* List<Note> notesList = new ArrayList<>();
        notesList=crudFireStoreViewModel.list();
        Log.d(TAG, "El contenido de la lista es .."+notesList.size());
        mAdapter= new NoteRecyclerViewAdapter(notesList,getApplicationContext(),crudFireStoreViewModel);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);*/
    }

    private void initview() {
        recyclerView = findViewById(R.id.rvNoteList);
        firestoreDB = FirebaseFirestore.getInstance();
        add= findViewById(R.id.ivAdd);
        this.am= getApplicationContext();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            if (item.getItemId() == R.id.addNote) {
                Intent intent = new Intent(this, NoteActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

      firestoreListener.remove();
    }



}