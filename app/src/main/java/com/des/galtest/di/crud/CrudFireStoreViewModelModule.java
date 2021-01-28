package com.des.galtest.di.crud;

import androidx.lifecycle.ViewModel;

import com.des.galtest.di.util.ViewModelKey;
import com.des.galtest.ui.crud.CrudGalileoViewModel;
import com.des.galtest.ui.crud.FireStore.CrudFireStoreViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class CrudFireStoreViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CrudFireStoreViewModel.class)
    public abstract ViewModel bindViewModel(CrudFireStoreViewModel viewModel);
}
