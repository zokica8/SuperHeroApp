package com.nsweb.heroapp.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nsweb.heroapp.data.domain.SuperHero;
import com.nsweb.heroapp.data.retrofit.configuration.RetrofitInstance;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class SuperHeroRepository {

    @Inject
    RetrofitInstance retrofitInstance;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public SuperHeroRepository() {

    }

    // manipulate the objects in the repository, based on what data you need
    public LiveData<List<SuperHero>> getAllSuperHeroes() {
        MutableLiveData<List<SuperHero>> superHeroes = new MutableLiveData<>();

        disposable.add(retrofitInstance.client().getAllSuperHeroes()
                .map(superHeroesList -> superHeroesList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(superHeroes::setValue, Throwable::printStackTrace,
                        () -> {
                            Timber.i("Process completed on %s", Thread.currentThread().getName());
                        }));

        return superHeroes;
    }

    public LiveData<SuperHero> getSuperHeroById(long id) {
        MutableLiveData<SuperHero> superHero = new MutableLiveData<>();

        disposable.add(retrofitInstance.client().getSuperHeroById(id)
                .map(oneSuperHero -> oneSuperHero)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(superHero::setValue, Throwable::printStackTrace, () -> {
                    Timber.i("Process completed on %s", Thread.currentThread().getName());
                }));

        return superHero;
    }

    public LiveData<SuperHero> insertSuperHero(SuperHero superHero) {
        MutableLiveData<SuperHero> newSuperHero = new MutableLiveData<>();

        disposable.add(retrofitInstance.client().insertSuperHero(superHero)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newSuperHero::setValue, Throwable::printStackTrace, () -> {
                    Timber.i("Save completed on: %s", Thread.currentThread().getName());
                }));

        return newSuperHero;
    }

    public LiveData<SuperHero> updateSuperHero(SuperHero superHero, long id) {
        MutableLiveData<SuperHero> updateSuperHero = new MutableLiveData<>();

        disposable.add(retrofitInstance.client().updateSuperHero(superHero, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateSuperHero::setValue, Throwable::printStackTrace, () -> {
                    Timber.i("Update completed on: %s", Thread.currentThread().getName());
                }));

        return updateSuperHero;
    }

    public LiveData<SuperHero> deleteSuperHero(long id) {
        MutableLiveData<SuperHero> deleteSuperHero = new MutableLiveData<>();

        disposable.add(retrofitInstance.client().deleteSuperHero(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deleteSuperHero::setValue, Throwable::printStackTrace, () -> {
                    Timber.i("Delete completed on: %s", Thread.currentThread().getName());
                }));

        return deleteSuperHero;
    }

    public void dispose() {
        // instead of calling dispose(), call clear()
        //disposable.dispose();
        disposable.clear();
    }
}
