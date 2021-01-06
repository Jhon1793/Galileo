package com.des.galtest.data.repository;

import com.des.galtest.dataSource.remote.FirebaseAuthSource;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

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

    public void signOut(){
        firebaseAuthSource.logout();
    }
}
