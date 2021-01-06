package com.des.galtest.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.des.galtest.R;
import com.des.galtest.ui.auth.register.RegisterActivity;
import com.des.galtest.ui.main.MainActivity;
import com.des.galtest.utils.LoadingDialog;
import com.des.galtest.utils.RxBindingHelper;
import com.des.galtest.utils.ViewModelFactory;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class LoginActivity extends DaggerAppCompatActivity implements View.OnClickListener {
    private LoginViewModel loginViewModel;
    private TextInputEditText  Email,Password;
    private Button btnlog,btngoogle,btnfacebook;
    private TextView registro,contrasena;
    Observable<Boolean> formObservable;
    private LoginButton loginF;
    private CallbackManager callbackManager;
    private static final String TAG = "LoginActivity";

    @Inject
    LoadingDialog loadingDialog;

    @Inject
    ViewModelFactory providerFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intView();
        formValidation();
        loginViewModel= new ViewModelProvider(getViewModelStore(), providerFactory).get(LoginViewModel.class);
        subscribeObservers();

    }

    private void subscribeObservers() {
        loginViewModel.observeLogin().observe(this,stateResource -> {
            if(stateResource != null){
                switch (stateResource.status){
                    case LOADING:
                        loadingDialog.show(getSupportFragmentManager(),"loadingDialog");
                        break;
                    case SUCCESS:
                        loadingDialog.dismiss();
                        moveToHomeActivity();
                        break;
                    case ERROR:
                        loadingDialog.dismiss();
                        break;
                }
            }
        });
    }
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button) {
            perform_login();
        }
        if (v.getId() == R.id.button2) {
            moveToLoginActivity();
        }
        if(v.getId() == R.id.btnGoogle){
            loginWithGoogle();
        }
        if(v.getId() == R.id.login_button1){
            loginWithFacebook();
        }
        if(v.getId() == R.id.txt_olv_pass){

            moveToResetPassword();
        }

    }

    private void moveToResetPassword() {

        Intent intent = new Intent(LoginActivity.this, RPasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loginWithFacebook() {
        loginF.performClick();
        loginF.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();

        loginF.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Autentication", "Completo");
                loginViewModel.loginFacebook(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d("Facebook Autentication", "Cancelado");
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "error" + error);
            }
        });
    }

    private void loginWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSingInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            loginViewModel.handleSignInResult(task);

        }
    }



    private void intView() {
    Email=findViewById(R.id.emaillog);
    Password=findViewById(R.id.passwordlog);
    btnlog=findViewById(R.id.button);
    btngoogle=findViewById(R.id.btnGoogle);
    registro=findViewById(R.id.button2);
    btnlog.setOnClickListener(this);
    registro.setOnClickListener(this);
    btngoogle.setOnClickListener(this);
    btnfacebook=findViewById(R.id.login_button1);
    loginF=findViewById(R.id.login_button);
    btnfacebook.setOnClickListener(this);
    contrasena=findViewById(R.id.txt_olv_pass);
    contrasena.setOnClickListener(this);
    }

    private void formValidation() {
        Observable<String> email_observable = RxBindingHelper.getObservableFrom(Email);
        Observable<String> password_observable = RxBindingHelper.getObservableFrom(Password);
        formObservable = Observable.combineLatest( email_observable, password_observable,  this::isValidForm);
        formObservable.subscribe(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                btnlog.setEnabled(aBoolean);
            }
            @Override
            public void onError(@NonNull Throwable e) {

            }
            @Override
            public void onComplete() {

            }
        });


    }


    private void perform_login() {
        String email = Objects.requireNonNull(Email.getText()).toString().trim();
        String password = Objects.requireNonNull(Password.getText()).toString().trim();
        loginViewModel.login(email, password);
    }

    private Boolean isValidForm( String email, String password) {
        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty();
        if (!isEmail) {
            Email.setError("Correo electrónico no válido");
        }
        return isEmail;
    }
    private void moveToLoginActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void moveToHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() ==android.R.id.home){
            onBackPressed();
        }
        return true;
    }

}