package com.des.galtest.dataSource.remote;

import com.des.galtest.utils.Constants;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.Completable;

public class FirebaseAuthSource {

    private static final String TAG = "FirebaseAuthSource";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Inject
    public FirebaseAuthSource(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
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

                    firebaseFirestore.collection(Constants.USERS_NODE)
                            .document(getCurrentUid()).set(map)
                            .addOnFailureListener(emitter::onError)
                            .addOnSuccessListener(aVoid -> emitter.onComplete());
                }));

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
    //logout
    public void logout(){
        firebaseAuth.signOut();
    }

}
