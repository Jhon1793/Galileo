package com.des.galtest.ui.auth.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.des.galtest.R;
import com.des.galtest.ui.auth.register.RegisterViewModel;
import com.des.galtest.ui.main.MainActivity;
import com.des.galtest.utils.LoadingDialog;
import com.des.galtest.utils.RxBindingHelper;
import com.des.galtest.utils.StateResource;
import com.des.galtest.utils.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class RPasswordActivity extends DaggerAppCompatActivity implements View.OnClickListener  {
    private Button rPassword;
    private RPasswordViewModel RPasswordViewModel;
    private TextInputEditText rEmail;
    Observable<Boolean> formObservable;

    @Inject
    LoadingDialog loadingDialog;

    @Inject
    ViewModelFactory providerFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        intView();
        RPasswordViewModel= new ViewModelProvider(getViewModelStore(), providerFactory).get(RPasswordViewModel.class);

        subscribeObservers();
    }

    private void subscribeObservers() {
        RPasswordViewModel.observeRPassword().observe(this,stateResource -> {
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
    private void moveToHomeActivity() {
        Intent intent = new Intent(RPasswordActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private Boolean isValidForm( String email) {
        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty();
        if (!isEmail) {
            rEmail.setError("Correo electrónico no válido");
        }
        return isEmail;
    }
    private void intView() {
        rEmail=findViewById(R.id.emailr);
        rPassword =findViewById(R.id.btnResetPassword);
        rPassword.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnResetPassword) {
            performRPassword();
        }

    }

    private void performRPassword() {
        String email = Objects.requireNonNull(rEmail.getText()).toString().trim();
        if(isValidForm(email)) {
            Log.d("RESET", "Reseteo de correo enviado");
            RPasswordViewModel.resetPassword(email);

        }
    }




}