package com.nsweb.heroapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.nsweb.heroapp.data.domain.SuperHero;
import com.nsweb.heroapp.data.repositories.SuperHeroRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SuperHeroViewModel extends ViewModel {

    @Inject
    SuperHeroRepository superHeroRepository;

    @Inject
    public SuperHeroViewModel() {

    }

    public LiveData<List<SuperHero>> getAllSuperHeroes() {
        return superHeroRepository.getAllSuperHeroes();
    }

    public LiveData<SuperHero> getSuperHeroById(long id) {
        return superHeroRepository.getSuperHeroById(id);
    }

    public LiveData<SuperHero> insertSuperHero(SuperHero superHero) {
        return superHeroRepository.insertSuperHero(superHero);
    }

    public LiveData<SuperHero> updateSuperHero(SuperHero superHero, long id) {
        return superHeroRepository.updateSuperHero(superHero, id);
    }

    public LiveData<SuperHero> deleteSuperHero(long id) {
        return superHeroRepository.deleteSuperHero(id);
    }

    @Override
    protected void onCleared() {
        superHeroRepository.dispose();
    }
}
