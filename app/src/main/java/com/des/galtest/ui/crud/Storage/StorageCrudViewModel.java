package com.des.galtest.ui.crud.Storage;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.des.galtest.data.repository.AuthRepository;
import com.des.galtest.model.Upload;
import com.des.galtest.utils.StateResource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StorageCrudViewModel extends ViewModel {
    private static final String TAG = "StorageCrudViewModel";
    AuthRepository authRepository;

    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<StateResource> onStorage = new MediatorLiveData<>();
    @Inject
    public StorageCrudViewModel(AuthRepository authRepository) {
        Log.d(TAG, "StorageCrudViewModel: working...");
        this.authRepository = authRepository;

        if (authRepository.getCurrentUser() == null) {
            Log.d(TAG, "StorageCrudViewModel:: No user loged in");
        }
    }
    public void addImageContent(String name, StorageTask mUploadTask,
                                Uri mImageUri, ContentResolver getContextReslver){
        authRepository.addImageContent( name,mUploadTask, mImageUri, getContextReslver).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onStorage.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onStorage.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onStorage.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }
    public void deleteImage(int position, List<Upload> mUploads){
        authRepository.deleteImage(position,mUploads).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onStorage.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onStorage.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onStorage.setValue(StateResource.error(e.getMessage()));
                    }
                });

    }
    public List<Upload> ListaU(DataSnapshot dataSnapshot){
        return authRepository.ListaU(dataSnapshot);
    }
    public LiveData<StateResource> observeCrud(){
        return onStorage;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
