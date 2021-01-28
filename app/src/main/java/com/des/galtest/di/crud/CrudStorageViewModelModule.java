package com.des.galtest.di.crud;

import androidx.lifecycle.ViewModel;

import com.des.galtest.di.util.ViewModelKey;
import com.des.galtest.ui.crud.CrudGalileoViewModel;
import com.des.galtest.ui.crud.Storage.StorageCrudViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
@Module
public abstract class CrudStorageViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(StorageCrudViewModel.class)
    public abstract ViewModel bindViewModel(StorageCrudViewModel viewModel);
}
