package com.des.galtest.di.component;

import android.app.Application;

import com.des.galtest.base.BaseActivity;
import com.des.galtest.di.module.ActivityBindingModule;
import com.des.galtest.di.module.ApplicationModule;
import com.des.galtest.di.module.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityBindingModule.class,
        ApplicationModule.class,
        ViewModelModule.class})
public interface ApplicationComponent extends AndroidInjector<BaseActivity> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        ApplicationComponent build();
    }
}