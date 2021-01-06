package com.des.galtest.ui.auth.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.des.galtest.data.repository.AuthRepository;
import com.des.galtest.utils.StateResource;
import com.squareup.okhttp.internal.tls.AndroidTrustRootIndex;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RPasswordViewModel extends ViewModel {
    private static final String TAG = "RPaswordViewModel";
    AuthRepository authRepository;


    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<StateResource> onRPasword = new MediatorLiveData<>();

    @Inject
    public RPasswordViewModel(AuthRepository authRepository) {
        Log.d(TAG, "LoginViewModel: working...");
        this.authRepository = authRepository;

        if (authRepository.getCurrentUser() == null) {
            Log.d(TAG, "LoginViewModel: No user loged in");
        }
    }
    public void resetPassword(String email){
        authRepository.rPassword(email).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onRPasword.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onRPasword.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onRPasword.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }
    public LiveData<StateResource> observeRPassword() {
        return onRPasword;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

}
