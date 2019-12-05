package com.nsweb.heroapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.nsweb.heroapp.R;
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.database.SuperHeroDatabase;
import com.nsweb.heroapp.domain.SuperHero;
import com.nsweb.heroapp.fragments.CreateHeroFragment;
import com.nsweb.heroapp.fragments.IndividualHeroFragment;
import com.nsweb.heroapp.fragments.MainFragment;
import com.nsweb.heroapp.fragments.ShowHeroesFragment;
import com.nsweb.heroapp.fragments.UpdateHeroFragment;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

@Singleton
public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
        CreateHeroFragment.OnFragmentInteractionListener, ShowHeroesFragment.OnFragmentInteractionListener,
        IndividualHeroFragment.OnFragmentInteractionListener, UpdateHeroFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private Bundle bundle = new Bundle();

    private SuperHeroDatabase database = new SuperHeroDatabase();

    @Inject
    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityScope();

        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
        SugarContext.init(this);

        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager manager = this.getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fragment_container, mainFragment).commit();
    }

    private void activityScope() {
        Toothpick.setConfiguration(Configuration.forDevelopment());
        Scope scope = Toothpick.openScopes(SuperHeroApplication.getInstance(), this);
        Toothpick.inject(this, scope);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.search:
                openSearchActivity();
                break;
            case R.id.http:
                connectToRestApi();
                break;
            case R.id.exit:
                System.exit(0);
                break;
        }
        return true;
    }

    private void connectToRestApi() {
        Intent intent = new Intent();
        intent.setClass(this, HttpActivity.class);
        startActivity(intent);
    }

    public void openSearchActivity() {
        Intent intent = new Intent();
        intent.setClass(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                openSearchActivity();
                return true;
            case R.id.http:
                connectToRestApi();
                return true;
            case R.id.exit:
                exit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exit() {
        moveTaskToBack(true);
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    public void createHeroScreen() {
        CreateHeroFragment createHeroFragment = new CreateHeroFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, createHeroFragment)
                .addToBackStack(null).attach(createHeroFragment).commit();
    }

    public void showHeroesScreen() {
        List<SuperHero> superHeroes = loadSuperHeroes();
        Bundle bundle = new Bundle();
        bundle.putSerializable("valuesArray", (ArrayList<SuperHero>) superHeroes);
        ShowHeroesFragment showHeroesFragment = new ShowHeroesFragment();
        showHeroesFragment.setArguments(bundle);
        this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, showHeroesFragment)
                .addToBackStack(null).commit();
    }

    public List<SuperHero> loadSuperHeroes() {
        // pulling the data from the SQLite local database
        List<SuperHero> superHeroes = database.getAllSuperHeroes();

        return superHeroes;
    }

    public void showIndividualHero(SuperHero superHero) {
        bundle.putSerializable("valuesArray", superHero);
        IndividualHeroFragment individualHeroFragment = new IndividualHeroFragment();
        individualHeroFragment.setArguments(bundle);
        this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, individualHeroFragment)
                .addToBackStack(null).commit();
    }

    public void returnToMainFragment() {
        MainFragment mainFragment = new MainFragment();
        this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainFragment)
                .commit();
    }

    public void updateIndividualHero(SuperHero superHero) {
        bundle.putSerializable("hero", superHero);
        UpdateHeroFragment updateHeroFragment = new UpdateHeroFragment();
        updateHeroFragment.setArguments(bundle);
        this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, updateHeroFragment)
                .addToBackStack(null).attach(updateHeroFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }
}