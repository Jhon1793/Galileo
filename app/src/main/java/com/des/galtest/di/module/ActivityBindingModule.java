package com.des.galtest.di.module;


import com.des.galtest.ui.main.HomeActivity;
import com.des.galtest.ui.main.MainFragmentBindingModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = {MainFragmentBindingModule.class})
    abstract HomeActivity bindHomeActivity();

}
