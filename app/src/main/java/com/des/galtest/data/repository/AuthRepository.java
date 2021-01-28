package com.des.galtest.data.repository;

import android.content.ContentResolver;
import android.net.Uri;
import android.widget.AdapterView;

import com.des.galtest.dataSource.remote.FirebaseAuthSource;
import com.des.galtest.model.Note;
import com.des.galtest.model.SitioWeb;
import com.des.galtest.model.Upload;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import io.reactivex.Completable;

public class AuthRepository {

    FirebaseAuthSource firebaseAuthSource;

    public AuthRepository(FirebaseAuthSource firebaseAuthSource) {
        this.firebaseAuthSource = firebaseAuthSource;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuthSource.getCurrentUser();
    }

    public String getCurrentUid() {
        return firebaseAuthSource.getCurrentUid();
    }

    public Completable register(String email, String password, String name, String phone) {
        return firebaseAuthSource.register(email, password, name, phone);
    }

    public  Completable updateDatbase(SitioWeb s, String nombresitio, String encargado){
        return firebaseAuthSource.updateDatbase(s,nombresitio,encargado);
    }
    public Completable deleteRealtimeDabase(SitioWeb s){
        return firebaseAuthSource.deleteRealtimeDabase(s);
    }
    public Completable addRealtimeDatabase(final String nombreSitio,final String supervisor){
        return firebaseAuthSource.addRealtimeDatabase(nombreSitio,supervisor);
    }
    public List<SitioWeb>  listaDatos(DataSnapshot snapshot){
        return firebaseAuthSource.listaDatos(snapshot);
    }
    public List<SitioWeb>  listadoDatos(){
        return firebaseAuthSource.listado();
    }

    public SitioWeb selectOne(AdapterView<?> parent, int posicion){
        return firebaseAuthSource.selectOne(parent, posicion);
    }
    public DatabaseReference databaseReference(){
        return firebaseAuthSource.getDatabaseReference();
    }


        public Completable login(String email, String password){
        return firebaseAuthSource.login(email,password);
    }
    public Completable loginGoogle(GoogleSignInAccount acc){
        return firebaseAuthSource.loginGoogle(acc);
    }
    public Completable loginFacebook(AccessToken token){
        return firebaseAuthSource.loginFacebook(token);
    }
    public  Completable rPassword(String email){
        return firebaseAuthSource.resetPasword(email);
    }
    public Completable addImageContent(String name, StorageTask mUploadTask, Uri mImageUri, ContentResolver getContextReslver ){
        return firebaseAuthSource.addImageContent(name,mUploadTask,mImageUri,getContextReslver);
    }
    public Completable deleteImage(int position, List<Upload> mUploads){
        return firebaseAuthSource.deleteImage(position,mUploads);
    }
    public List<Upload> ListaU(DataSnapshot dataSnapshot){
        return firebaseAuthSource.ListaU(dataSnapshot);
    }

    public Completable addFireStore(String title, String content){
        return firebaseAuthSource.addFireStore(title,content);
    }
    public Completable updateFireStore(String id, String title, String content){
        return firebaseAuthSource.updateFireStore(id,title,content);
    }
    public Completable deleteFireStore(String id, final int position,  List<Note> notesList){
        return firebaseAuthSource.deleteFireStore(id,position,notesList);
    }
    public List<Note> list(){
        return firebaseAuthSource.list();

    }
    public List<Note> list2(){
        return firebaseAuthSource.list();

    }
    public void signOut(){
        firebaseAuthSource.logout();
    }
}
