package com.des.galtest.di.register;

import androidx.lifecycle.ViewModel;

import com.des.galtest.di.util.ViewModelKey;
import com.des.galtest.ui.auth.register.RegisterViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class RegisterViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel.class)
    public abstract ViewModel bindViewModel(RegisterViewModel viewModel);

}
