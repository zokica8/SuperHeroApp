package com.nsweb.heroapp.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.ui.fragments.CreateHeroFragment;
import com.nsweb.heroapp.ui.fragments.HttpFragment;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

public class HttpActivity extends AppCompatActivity implements HttpFragment.OnFragmentInteractionListener,
        CreateHeroFragment.OnFragmentInteractionListener {

    @Inject
    HttpFragment httpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);

        Toothpick.setConfiguration(Configuration.forDevelopment());
        Scope scope = Toothpick.openScopes(SuperHeroApplication.getInstance(), this);
        Toothpick.inject(this, scope);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fragment_container, httpFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}