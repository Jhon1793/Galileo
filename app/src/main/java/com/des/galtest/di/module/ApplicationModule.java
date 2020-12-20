package com.des.galtest.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
public class ApplicationModule {


    @Singleton
    @Provides
    static Context provideRetrofitService(Application application) {
        return application;
    }
}
