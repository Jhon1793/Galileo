package com.des.galtest.di.module;


import com.des.galtest.di.crud.CrudFireStoreViewModelModule;
import com.des.galtest.di.crud.CrudGalileoViewModelModule;
import com.des.galtest.di.crud.CrudStorageViewModelModule;
import com.des.galtest.di.login.LoginViewModelModule;
import com.des.galtest.di.login.RPasswordViewModelModule;
import com.des.galtest.di.register.RegisterViewModelModule;
import com.des.galtest.ui.auth.login.LoginActivity;
import com.des.galtest.ui.auth.login.RPasswordActivity;
import com.des.galtest.ui.auth.login.RPasswordViewModel;
import com.des.galtest.ui.auth.register.RegisterActivity;
import com.des.galtest.ui.crud.CrudGalileoActivity;
import com.des.galtest.ui.crud.FireStore.CrudFireStore;
import com.des.galtest.ui.crud.FireStore.NoteActivity;
import com.des.galtest.ui.crud.Storage.ImagesActivity;
import com.des.galtest.ui.crud.Storage.StorageCrudActivity;
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
    @ContributesAndroidInjector(modules = {
            RPasswordViewModelModule.class
    })
    abstract RPasswordActivity contributeRPasswordActivity();
    @ContributesAndroidInjector(modules = {
            CrudGalileoViewModelModule.class
    })
    abstract CrudGalileoActivity contributeCrudGalileoActivity();
    @ContributesAndroidInjector(modules = {
            CrudStorageViewModelModule.class

    })
    abstract StorageCrudActivity contributeStorageCrudActivity();
    @ContributesAndroidInjector(modules = {
            CrudStorageViewModelModule.class

    })
    abstract ImagesActivity contributeImagesActivity();
    @ContributesAndroidInjector(modules = {
            CrudFireStoreViewModelModule.class

    })
    abstract CrudFireStore contributeCrudFireStore();
    @ContributesAndroidInjector(modules = {
            CrudFireStoreViewModelModule.class

    })
    abstract NoteActivity contributeNoteActivity();
}
