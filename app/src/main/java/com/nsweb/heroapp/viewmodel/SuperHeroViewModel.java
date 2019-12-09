package com.nsweb.heroapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nsweb.heroapp.data.domain.SuperHero;
import com.nsweb.heroapp.data.repositories.SuperHeroRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class SuperHeroViewModel extends ViewModel {

    @Inject
    SuperHeroRepository superHeroRepository;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public SuperHeroViewModel() {

    }

    public LiveData<List<SuperHero>> getAllSuperHeroes() {
        MutableLiveData<List<SuperHero>> superHeroes = new MutableLiveData<>();

        disposable.add(superHeroRepository.getAllSuperHeroes()
                .map(superHeroesList -> superHeroesList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(superHeroes::setValue, Throwable::printStackTrace, () -> Timber.i("Process finished on: %s", Thread.currentThread().getName())));

        return superHeroes;
    }

    public MutableLiveData<SuperHero> getSuperHeroById(long id) {
        MutableLiveData<SuperHero> superHero = new MutableLiveData<>();

        disposable.add(superHeroRepository.getSuperHeroById(id)
                .map(i -> i)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(superHero::setValue, Throwable::printStackTrace, () -> Timber.i("Process completed on: %s", Thread.currentThread().getName())));

        return superHero;
    }

    public MutableLiveData<SuperHero> insertSuperHero(SuperHero superHero) {
        MutableLiveData<SuperHero> newSuperHero = new MutableLiveData<>();

        disposable.add(superHeroRepository.insertSuperHero(superHero)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(newSuperHero::setValue, Throwable::printStackTrace, () -> Timber.i("Save completed on: %s", Thread.currentThread().getName())));
        return newSuperHero;
    }

    public MutableLiveData<SuperHero> updateSuperHero(SuperHero superHero, long id) {
        MutableLiveData<SuperHero> updateSuperHero = new MutableLiveData<>();

        disposable.add(superHeroRepository.updateSuperHero(superHero, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(updateSuperHero::setValue, Throwable::printStackTrace, () -> Timber.i("Update completed on: %s", Thread.currentThread().getName())));
        return updateSuperHero;
    }

    public MutableLiveData<SuperHero> deleteSuperHero(long id) {
        MutableLiveData<SuperHero> deleteSuperHero = new MutableLiveData<>();

        disposable.add(superHeroRepository.deleteSuperHero(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        .subscribe(deleteSuperHero::setValue, Throwable::printStackTrace, () -> Timber.i("Delete completed on: %s", Thread.currentThread().getName())));

        return deleteSuperHero;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        disposable.clear();
    }
}
