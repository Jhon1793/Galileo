package com.des.galtest.data.repository;

import com.des.galtest.dataSource.remote.FirebaseAuthSource;
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

    public void signOut(){
        firebaseAuthSource.logout();
    }
}
