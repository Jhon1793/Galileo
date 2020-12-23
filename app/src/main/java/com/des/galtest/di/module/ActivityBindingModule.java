package com.des.galtest.di.module;


import com.des.galtest.di.login.LoginViewModelModule;
import com.des.galtest.di.register.RegisterViewModelModule;
import com.des.galtest.ui.auth.login.LoginActivity;
import com.des.galtest.ui.auth.register.RegisterActivity;
import com.des.galtest.ui.main.MainActivity;
import com.des.galtest.ui.main.MainFragmentBindingModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = {MainFragmentBindingModule.class})
    abstract MainActivity bindHomeActivity();

    @ContributesAndroidInjector(modules = {
            RegisterViewModelModule.class
    })
    abstract RegisterActivity contributeRegisterActivity();
    @ContributesAndroidInjector(modules = {
            LoginViewModelModule.class
    })
    abstract LoginActivity contributeLoginActivity();

}
