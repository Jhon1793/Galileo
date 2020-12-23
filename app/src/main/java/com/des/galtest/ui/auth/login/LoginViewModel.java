package com.des.galtest.ui.auth.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.des.galtest.data.repository.AuthRepository;
import com.des.galtest.utils.StateResource;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";
    AuthRepository authRepository;

    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<StateResource> onLogin = new MediatorLiveData<>();

    @Inject
    public LoginViewModel(AuthRepository authRepository) {
        Log.d(TAG, "LoginViewModel: working...");
        this.authRepository = authRepository;

        if (authRepository.getCurrentUser() == null) {
            Log.d(TAG, "LoginViewModel: No user loged in");
        }
    }


    public void login(String email, String password) {
        authRepository.login(email, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onLogin.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onLogin.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onLogin.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }

    public void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            assert acc != null;
            loginGoogle(acc);
        } catch (ApiException e) {
            ///    Toast.makeText(LoginActivity.this, "Logeo Erroneo", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginGoogle(GoogleSignInAccount acc) {
        authRepository.loginGoogle(acc).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onLogin.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onLogin.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onLogin.setValue(StateResource.error(e.getMessage()));
                    }
                });

    }

    public  void loginFacebook(AccessToken accessToken) {
        authRepository.loginFacebook(accessToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                        onLogin.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onLogin.setValue(StateResource.success());

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onLogin.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }
    public LiveData<StateResource> observeLogin() {
        return onLogin;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
