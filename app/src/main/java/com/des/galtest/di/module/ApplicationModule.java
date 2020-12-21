package com.des.galtest.di.module;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.des.galtest.R;
import com.des.galtest.data.repository.AuthRepository;
import com.des.galtest.dataSource.remote.FirebaseAuthSource;
import com.des.galtest.utils.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    @Singleton
    @Provides
    static FirebaseAuth getAuthInstance(){
        return FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    static FirebaseFirestore provideFirebaseInstance(){
        return FirebaseFirestore.getInstance();
    }

    @Singleton
    @Provides
    static FirebaseAuthSource getAuthSource(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore){
        return new FirebaseAuthSource(firebaseAuth,firebaseFirestore);
    }
    @Singleton
    @Provides
    static AuthRepository provideAuthRepository(FirebaseAuthSource authSource){
        return  new AuthRepository(authSource);
    }

    @Singleton
    @Provides
    static StorageReference provideStorageReference(){
        return FirebaseStorage.getInstance().getReference();
    }

    @Singleton
    @Provides
    static LoadingDialog provideLoadingDialog(){
        return new LoadingDialog();
    }

    @Singleton
    @Provides
    static RequestOptions provideRequestOptions(){
        return RequestOptions.placeholderOf(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground);
    }
    @Singleton
    @Provides
    static RequestManager provideGlideInstance(Application application, RequestOptions requestOptions){
        return Glide.with(application)
                .setDefaultRequestOptions(requestOptions);
    }

}
