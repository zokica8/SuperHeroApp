package com.nsweb.heroapp.dagger.modules;

import com.nsweb.heroapp.data.repositories.SuperHeroRepository;
import com.nsweb.heroapp.data.retrofit.configuration.RetrofitInstance;
import com.nsweb.heroapp.viewmodel.SuperHeroViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class SuperHeroModule {

    @Provides
    public SuperHeroViewModel provideSuperHeroViewModel() {
        return new SuperHeroViewModel(provideSuperHeroRepository());
    }

    @Provides
    public SuperHeroRepository provideSuperHeroRepository() {
        return new SuperHeroRepository(provideRetrofitInstance());
    }

    @Provides
    public RetrofitInstance provideRetrofitInstance() {
        return new RetrofitInstance();
    }
}
