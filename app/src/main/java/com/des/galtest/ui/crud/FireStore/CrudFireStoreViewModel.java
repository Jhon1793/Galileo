package com.des.galtest.ui.crud.FireStore;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.des.galtest.data.repository.AuthRepository;
import com.des.galtest.model.Note;
import com.des.galtest.utils.StateResource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CrudFireStoreViewModel extends ViewModel {
    private static final String TAG = "CrudFireStoreViewModel";
    AuthRepository authRepository;

    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<StateResource> onCrudFireStore = new MediatorLiveData<>();
    @Inject
    public CrudFireStoreViewModel(AuthRepository authRepository) {
        Log.d(TAG, "CrudFireStoreViewModel: working...");
        this.authRepository = authRepository;

        if (authRepository.getCurrentUser() == null) {
            Log.d(TAG, "CrudFireStoreViewModel: No user loged in");
        }
    }
    public void addFireStore(String title, String content){
        authRepository.addFireStore(title,content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onCrudFireStore.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onCrudFireStore.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onCrudFireStore.setValue(StateResource.error(e.getMessage()));
                    }
                });

    }
    public void deleteFireStore(String id, final int position,  List<Note> notesList){
        authRepository.deleteFireStore(id,position,notesList).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onCrudFireStore.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onCrudFireStore.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onCrudFireStore.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }
    public void updateFireStore(String id,String title, String content){
        authRepository.updateFireStore(id,title,content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onCrudFireStore.setValue(StateResource.loading());
                    }
                    @Override
                    public void onComplete() {
                        onCrudFireStore.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onCrudFireStore.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }
    public List<Note> list(){
        return authRepository.list();
    }
    public List<Note> list2(){
        return authRepository.list();
    }
    public LiveData<StateResource> observeCrud(){
        return onCrudFireStore;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

}
