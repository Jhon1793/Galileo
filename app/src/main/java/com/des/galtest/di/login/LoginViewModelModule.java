package com.des.galtest.di.login;

import androidx.lifecycle.ViewModel;

import com.des.galtest.di.util.ViewModelKey;
import com.des.galtest.ui.auth.login.LoginViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class LoginViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindViewModel(LoginViewModel viewModel);
}
