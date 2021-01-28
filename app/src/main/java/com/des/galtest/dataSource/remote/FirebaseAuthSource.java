package com.des.galtest.dataSource.remote;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.ContentResolver;

import com.des.galtest.model.Note;
import com.des.galtest.model.SitioWeb;
import com.des.galtest.model.Upload;
import com.des.galtest.ui.crud.FireStore.CrudFireStore;
import com.des.galtest.ui.crud.FireStore.NoteRecyclerViewAdapter;
import com.des.galtest.utils.Constants;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Completable;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FirebaseAuthSource {

    private static final String TAG = "FirebaseAuthSource";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference mStorageRef;
    FirebaseStorage mStorage;
    DatabaseReference mDatabaseRef;

    final String childS="Sitio";
    @Inject
    public FirebaseAuthSource(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseDatabase=FirebaseDatabase.getInstance();
        this.databaseReference=firebaseDatabase.getReference();
        mStorageRef= FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");
        mStorage=FirebaseStorage.getInstance();
    }

    //get current user uid
    public String getCurrentUid() {
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }

    //get current user
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    //create new account
    public Completable register(final String email, final String password, final String name, final String phone) {
        return Completable.create(emitter -> firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(emitter::onError)
                .addOnCompleteListener(task -> {
                    //create new user
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("email",email);
                    map.put("displayName",name);
                    map.put("phone",phone);
                    map.put("image","default");
                    map.put("status","default");
                    map.put("online",true);
                    firebaseAuth.getCurrentUser().sendEmailVerification();

                    firebaseFirestore.collection(Constants.USERS_NODE)
                            .document(getCurrentUid()).set(map)
                            .addOnFailureListener(emitter::onError)
                            .addOnSuccessListener(aVoid -> emitter.onComplete());
                }));

    }

    public Completable addRealtimeDatabase(final String nombreSitio,final String supervisor){
        SitioWeb sitio= new SitioWeb();
        sitio.setUID(UUID.randomUUID().toString());
        sitio.setNombreSitio(nombreSitio);
        sitio.setEncargado(supervisor);
        return Completable.create(emitter -> databaseReference.child(childS).child(sitio.getUID()).setValue(sitio)
        .addOnFailureListener(emitter::onError)
        .addOnSuccessListener(aVoid ->emitter.onComplete()));
    }

    public Completable deleteRealtimeDabase(SitioWeb s){
        SitioWeb si= new SitioWeb();
        si.setUID(s.getUID());
        return Completable.create(emitter -> databaseReference.child(childS).child(si.getUID()).removeValue()
        .addOnFailureListener(emitter::onError)
        .addOnSuccessListener(aVoid-> emitter.onComplete()));
    }
        public List<SitioWeb> listado(){
            Log.d("ANDROID","llEGA HASTA AQUI INICIO");
            List<SitioWeb> listSitio = new ArrayList<SitioWeb>();
            listSitio.clear();
            databaseReference.child(childS).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                SitioWeb si=dataSnapshot.getValue(SitioWeb.class);
                listSitio.add(si);
                }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Log.d("ANDROID","llEGA HASTA AQUI "+listSitio);
        return listSitio;
        }

    public List<SitioWeb> listaDatos(DataSnapshot snapshot){
        List<SitioWeb> listSitio = new ArrayList<SitioWeb>();
        listSitio.clear();
        for(DataSnapshot objSnapshot:snapshot.getChildren()){
            SitioWeb g=objSnapshot.getValue(SitioWeb.class);
            listSitio.add(g);
        }
        return listSitio;

    }
    public SitioWeb selectOne(AdapterView<?> parent,int posicion){
        SitioWeb s= new SitioWeb();
        Log.d(TAG, "CrudGalileoViewModel: working..."+parent+ ""+posicion);

        s=(SitioWeb) parent.getItemAtPosition(posicion);
        return s;
    }

    public DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("Sitio");
    }

    public Completable updateDatbase(SitioWeb s, String nombresitio, String encargado){
        SitioWeb si=new SitioWeb();
        si.setUID(s.getUID());
        si.setNombreSitio(nombresitio);
        si.setEncargado(encargado);
        return Completable.create(emitter -> databaseReference.child(childS).child(si.getUID()).setValue(si)
                .addOnFailureListener(emitter::onError)
                .addOnSuccessListener(aVoid->emitter.onComplete()));

    }
    //login
    public Completable login(final String email, final String password){

        return Completable.create(emitter -> firebaseAuth.signInWithEmailAndPassword(email,password)

                .addOnFailureListener(emitter::onError)
                .addOnSuccessListener(authResult -> emitter.onComplete()));
    }

    //loginGoogle
    public Completable loginGoogle(final GoogleSignInAccount acc){
        AuthCredential credential= GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        return Completable.create((emitter -> firebaseAuth.signInWithCredential(credential)
        .addOnFailureListener(emitter::onError)
        .addOnSuccessListener(authResult -> emitter.onComplete())));
    }


    //login Facebook
    public Completable loginFacebook(final AccessToken token){
        AuthCredential credential= FacebookAuthProvider.getCredential(token.getToken());
        return Completable.create((emitter -> firebaseAuth.signInWithCredential(credential)
                .addOnFailureListener(emitter::onError)
                .addOnSuccessListener(authResult -> emitter.onComplete())));
    }
    public Completable resetPasword(String email){
        return Completable.create(emitter -> firebaseAuth.sendPasswordResetEmail(email)
        .addOnFailureListener(emitter::onError)
        .addOnSuccessListener((authResult -> emitter.onComplete())));
    }
    //logout
    public void logout(){
        firebaseAuth.signOut();
    }
    public Completable addImageContent(String name, StorageTask mUploadTask,
                                       Uri mImageUri, ContentResolver getContextReslver){
        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                + "." + getFileExtension(mImageUri,getContextReslver));
            mUploadTask=fileReference.putFile(mImageUri);
        return Completable.create(emitter ->  fileReference.putFile(mImageUri)
        .addOnFailureListener(emitter::onError)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                Upload upload = new Upload(name.trim(),downloadUrl.toString());
                String uploadId = mDatabaseRef.push().getKey();
                mDatabaseRef.child(uploadId).setValue(upload);

            }
        }))       ;

       /* Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
        while (!urlTask.isSuccessful());
        Uri downloadUrl = urlTask.getResult();
        Upload upload = new Upload(name.trim(),downloadUrl.toString());
        String uploadId = mDatabaseRef.push().getKey();
        return Completable.create(emitter -> mDatabaseRef.child(uploadId).setValue(upload)
        .addOnFailureListener(emitter::onError)
        .addOnSuccessListener(authResult ->emitter.onComplete()));*/
    }
    public void addImage(Uri mImageUri,ContentResolver getContentResolver){
        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                + "." + getFileExtension(mImageUri,getContentResolver));

    }
    private String getFileExtension(Uri uri, ContentResolver getContentResolver) {
        ContentResolver cR = getContentResolver;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    public List<Upload> ListaU(DataSnapshot dataSnapshot){
        List<Upload> listU = new ArrayList<Upload>();
        listU.clear();
        for(DataSnapshot objSnapshot:dataSnapshot.getChildren()){
            Upload upload = objSnapshot.getValue(Upload.class);
            upload.setKey(objSnapshot.getKey());
            listU.add(upload);
        }

        return listU;
    }
    public Completable deleteImage(int position, List<Upload> mUploads){
        Upload selectedItem = mUploads.get(position);


        if(selectedItem!=null) {
            final String selectedKey = selectedItem.getKey();
            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());

            return Completable.create(emitter -> imageRef.delete()
                    .addOnFailureListener(emitter::onError)
                    .addOnCompleteListener(task ->
                            mDatabaseRef.child(selectedKey).removeValue()
                    )
            );
        }
        return null;
    }

    public void SelectImage(Upload selectedItem){

    }
    public void updateImage(){
    }
    public Completable addFireStore(String title, String content){
        Map<String, Object> note = new Note(title, content).toMap();
        return Completable.create(emitter -> firebaseFirestore.collection("notes")
        .add(note)
        .addOnSuccessListener(aVoid -> emitter.onComplete())
        .addOnFailureListener(emitter::onError));
    }
    public Completable updateFireStore(String id, String title, String content){
        Map<String, Object> note = (new Note(id, title, content)).toMap();
        return Completable.create(emitter -> firebaseFirestore.collection("notes")
        .document(id)
        .set(note)
        .addOnFailureListener(emitter::onError)
        .addOnSuccessListener(aVoid -> emitter.onComplete()));
    }
    public Completable deleteFireStore(String id, final int position,  List<Note> notesList){
        return Completable.create((emitter -> firebaseFirestore.collection("notes")
        .document(id)
        .delete()
        .addOnSuccessListener(aVoid -> emitter.onComplete())
        .addOnFailureListener(emitter::onError)

        ));
    }

    public List<Note> list(){
        List<Note> notesList = new ArrayList<>();
  /*      Task<QuerySnapshot> q =firebaseFirestore.collection("notes")
                .get();
            if(q!=null){
                for (DocumentSnapshot doc :q.getResult() ){
                    Note note = doc.toObject(Note.class);
                    //Log.d(TAG, "El contenido de la lista Autj es entra al for.."+note.getTitle());
                    note.setId(doc.getId());
                    notesList.add(note);

                }
                CrudFireStore crudFireStore;
                CrudFireStore crudFireStore1=new CrudFireStore();
                 NoteRecyclerViewAdapter mAdapter;
                mAdapter= new NoteRecyclerViewAdapter(notesList,crudFireStore1.getApplicationContext(),crudFireStore1.crudFireStoreViewModel);
                crudFireStore1.recyclerView.setAdapter(mAdapter);
            }*/
        Completable.create(emitter -> firebaseFirestore.collection("notes")
        .get()
        .addOnSuccessListener(aVoid ->emitter.onComplete())
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        Note note = doc.toObject(Note.class);
                        //Log.d(TAG, "El contenido de la lista Autj es entra al for.."+note.getTitle());
                        note.setId(doc.getId());
                        notesList.add(note);
                    }

                }
                Log.d(TAG, "El contenido de la lista Dentro  es .."+notesList.size());
            }
        })

        )
        ;
/*        Completable.create(emitter ->  firebaseFirestore.collection("notes")
                .get()
                .addOnSuccessListener(emitter.onComplete())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                Note note = doc.toObject(Note.class);
                                //Log.d(TAG, "El contenido de la lista Autj es entra al for.."+note.getTitle());
                                note.setId(doc.getId());
                                notesList.add(note);
                            }
                        }


                    };

                })
        ;*/

               Log.d(TAG, "El contenido de la lista Metodo  es .."+notesList.size());

    return notesList;
    }
    public List<Note> list2(){
         ListenerRegistration firestoreListener;
        List<Note> notesList = new ArrayList<>();
            firestoreListener=  firebaseFirestore.collection("notes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Listen failed!", error);
                            return;
                        }

                        for (DocumentSnapshot doc : value) {
                            Note note = doc.toObject(Note.class);
                            note.setId(doc.getId());
                            notesList.add(note);
                        }
                    }

                });
        return notesList;
    }

    }
