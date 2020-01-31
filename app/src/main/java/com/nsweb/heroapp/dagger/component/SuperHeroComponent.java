package com.nsweb.heroapp.dagger.component;

import com.nsweb.heroapp.dagger.modules.SuperHeroModule;
import com.nsweb.heroapp.ui.activities.GetSuperHeroesActivity;
import com.nsweb.heroapp.ui.activities.HttpActivity;
import com.nsweb.heroapp.ui.activities.IndividualSuperHeroActivity;
import com.nsweb.heroapp.ui.activities.MainActivity;
import com.nsweb.heroapp.ui.activities.SearchActivity;
import com.nsweb.heroapp.ui.activities.UpdateSuperHeroRestActivity;
import com.nsweb.heroapp.ui.fragments.CreateHeroFragment;
import com.nsweb.heroapp.ui.fragments.HttpFragment;
import com.nsweb.heroapp.ui.fragments.IndividualHeroFragment;
import com.nsweb.heroapp.ui.fragments.MainFragment;
import com.nsweb.heroapp.ui.fragments.ShowHeroesFragment;
import com.nsweb.heroapp.ui.fragments.UpdateHeroFragment;
import com.nsweb.heroapp.viewmodel.SuperHeroViewModel;

import dagger.Component;

@Component(modules = SuperHeroModule.class)
public interface SuperHeroComponent {

    SuperHeroViewModel superHeroViewModel();

    void inject(GetSuperHeroesActivity getSuperHeroesActivity);
    void inject(HttpActivity httpActivity);
    void inject(IndividualSuperHeroActivity individualSuperHeroActivity);
    void inject(MainActivity mainActivity);
    void inject(SearchActivity searchActivity);
    void inject(UpdateSuperHeroRestActivity updateSuperHeroRestActivity);
    void inject(CreateHeroFragment createHeroFragment);
    void inject(HttpFragment httpFragment);
    void inject(IndividualHeroFragment individualHeroFragment);
    void inject(MainFragment mainFragment);
    void inject(ShowHeroesFragment showHeroesFragment);
    void inject(UpdateHeroFragment updateHeroFragment);
}
