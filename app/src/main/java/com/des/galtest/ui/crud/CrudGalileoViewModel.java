package com.des.galtest.ui.crud;

import android.util.Log;
import android.widget.AdapterView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.des.galtest.data.repository.AuthRepository;
import com.des.galtest.model.SitioWeb;
import com.des.galtest.utils.StateResource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CrudGalileoViewModel  extends ViewModel {
    private static final String TAG = "CrudGalileoViewModel";
    AuthRepository authRepository;

    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<StateResource> onCrud = new MediatorLiveData<>();


    @Inject
    public CrudGalileoViewModel(AuthRepository authRepository) {
        Log.d(TAG, "CrudGalileoViewModel: working...");
        this.authRepository = authRepository;

        if (authRepository.getCurrentUser() == null) {
            Log.d(TAG, "LoginViewModel: No user loged in");
        }
    }
    public void addDatabaseRealtime(String nombresitio,String encargado){
        authRepository.addRealtimeDatabase(nombresitio,encargado).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onCrud.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onCrud.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onCrud.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }
    public SitioWeb selectOne(AdapterView<?> parent, int posicion) {
        return authRepository.selectOne(parent, posicion);
    }
    public DatabaseReference databaseReference(){
        return authRepository.databaseReference();
    }
    public List<SitioWeb>  listaDatos(DataSnapshot snapshot){
        return authRepository.listaDatos(snapshot);
    }
    public void deleteDatabaseRealtime(SitioWeb s){

         authRepository.deleteRealtimeDabase(s).subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new CompletableObserver() {
                     @Override
                     public void onSubscribe(@NonNull Disposable d) {
                         disposable.add(d);
                         onCrud.setValue(StateResource.loading());
                     }

                     @Override
                     public void onComplete() {
                         onCrud.setValue(StateResource.success());
                     }

                     @Override
                     public void onError(@NonNull Throwable e) {
                         onCrud.setValue(StateResource.error(e.getMessage()));
                     }
                 });
    }
    public void updateDatabaseRealtime(SitioWeb s, String nombresitio, String encargado){
        authRepository.updateDatbase(s,nombresitio,encargado).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onCrud.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onCrud.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onCrud.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }

    public List<SitioWeb> listado(){
        return authRepository.listadoDatos();
    }

        public LiveData<StateResource> observeCrud(){
        return onCrud;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
