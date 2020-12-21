package com.des.galtest.ui.auth.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.des.galtest.R;
import com.des.galtest.ui.auth.login.LoginActivity;
import com.des.galtest.ui.main.MainActivity;
import com.des.galtest.utils.LoadingDialog;
import com.des.galtest.utils.RxBindingHelper;
import com.des.galtest.utils.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public class RegisterActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    private RegisterViewModel registerViewModel;
    private TextInputEditText Name, Email, Phone, Password, Password2;
    private Button btn_register;
    private TextView txt_login;
    Observable<Boolean> formObservable;

    @Inject
    LoadingDialog loadingDialog;

    @Inject
    ViewModelFactory providerFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        intToolbar();
        intView();
        formValidation();
        registerViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(RegisterViewModel.class);

        subscribeObservers();
    }

    private void subscribeObservers() {
        registerViewModel.observeRegister().observe(this, stateResource -> {
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
                        showSnackBar(stateResource.message);
                        break;
                }
            }
        });
    }

    private void intView() {
        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        Phone = findViewById(R.id.phone);
        Password = findViewById(R.id.pass1);
        Password2 = findViewById(R.id.pass2);
        btn_register = findViewById(R.id.btn_register);
        txt_login = findViewById(R.id.txt_login);
        btn_register.setOnClickListener(this);
        txt_login.setOnClickListener(this);
    }

    private void intToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTextAppearance);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_register) {
            perform_register();
        }
        if (v.getId() == R.id.txt_login) {
            moveToLoginActivity();
        }
    }

    private void perform_register() {
        String name = Objects.requireNonNull(Name.getText()).toString();
        String email = Objects.requireNonNull(Email.getText()).toString().trim();
        String phone = Objects.requireNonNull(Phone.getText()).toString().trim();
        String password = Objects.requireNonNull(Password.getText()).toString().trim();
        registerViewModel.register(email, password, name, phone);
    }

    private void formValidation() {
        Observable<String> name_observable = RxBindingHelper.getObservableFrom(Name);
        Observable<String> email_observable = RxBindingHelper.getObservableFrom(Email);
        Observable<String> password_observable = RxBindingHelper.getObservableFrom(Password);
        Observable<String> password2_observable = RxBindingHelper.getObservableFrom(Password2);

        formObservable = Observable.combineLatest(name_observable, email_observable, password_observable, password2_observable, this::isValidForm);

        formObservable.subscribe(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NotNull Boolean aBoolean) {
                btn_register.setEnabled(aBoolean);
            }

            @Override
            public void onError(@NotNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private Boolean isValidForm(String name, String email, String password, String password2) {

        boolean isName = name.isEmpty();
        if (isName) {
            Name.setError("Nombre no válido");
        }

        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty();
        if (!isEmail) {
            Email.setError("Correo electrónico no válido");
        }

        boolean isPassword = password.length() > 6;
        if (!isPassword) {
            Password.setError("La contraseña debe ser mayor de 6 dígitos");
        }

        boolean isPasswordValidate = password.equals(password2);
        if (!isPasswordValidate) {
            Password2.setError("Las contraseñas no coinciden");
        }

        return !isName && isEmail && isPassword && isPasswordValidate;
    }

    private void showSnackBar(String msg) {
        View contextView = findViewById(android.R.id.content);
        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
    }

    private void moveToHomeActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void moveToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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

 