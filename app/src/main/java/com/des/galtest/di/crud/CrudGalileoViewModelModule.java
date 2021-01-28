package com.des.galtest.di.crud;

import androidx.lifecycle.ViewModel;

import com.des.galtest.di.util.ViewModelKey;
import com.des.galtest.ui.auth.register.RegisterViewModel;
import com.des.galtest.ui.crud.CrudGalileoViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class CrudGalileoViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CrudGalileoViewModel.class)
    public abstract ViewModel bindViewModel(CrudGalileoViewModel viewModel);

}
