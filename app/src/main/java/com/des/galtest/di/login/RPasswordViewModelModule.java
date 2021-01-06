package com.des.galtest.di.login;

import androidx.lifecycle.ViewModel;

import com.des.galtest.di.util.ViewModelKey;

import com.des.galtest.ui.auth.login.RPasswordViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class RPasswordViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(RPasswordViewModel.class)
    public abstract ViewModel bindViewModel(RPasswordViewModel viewModel);
}
