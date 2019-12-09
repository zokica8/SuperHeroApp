package com.nsweb.heroapp.data.repositories;

import com.nsweb.heroapp.data.domain.SuperHero;
import com.nsweb.heroapp.data.retrofit.configuration.RetrofitInstance;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class SuperHeroRepository {

    @Inject
    RetrofitInstance retrofitInstance;

    @Inject
    public SuperHeroRepository() {

    }

    public Observable<List<SuperHero>> getAllSuperHeroes() {
        return retrofitInstance.client().getAllSuperHeroes();
    }

    public Observable<SuperHero> getSuperHeroById(long id) {
        return retrofitInstance.client().getSuperHeroById(id);
    }

    public Observable<SuperHero> insertSuperHero(SuperHero superHero) {
        return retrofitInstance.client().insertSuperHero(superHero);
    }

    public Observable<SuperHero> updateSuperHero(SuperHero superHero, long id) {
        return retrofitInstance.client().updateSuperHero(superHero, id);
    }

    public Observable<SuperHero> deleteSuperHero(long id) {
        return retrofitInstance.client().deleteSuperHero(id);
    }

}
