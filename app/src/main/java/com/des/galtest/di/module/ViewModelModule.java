package com.des.galtest.di.module;

import androidx.lifecycle.ViewModelProvider;

import com.des.galtest.utils.ViewModelFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
