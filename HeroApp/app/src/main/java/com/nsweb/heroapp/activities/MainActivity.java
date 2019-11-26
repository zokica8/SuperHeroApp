package com.nsweb.heroapp.activities;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.fragments.BackStoryFragment;
import com.nsweb.heroapp.fragments.MainFragment;
import com.nsweb.heroapp.fragments.PickPowerFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
        PickPowerFragment.OnFragmentInteractionListener, BackStoryFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = this.getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new MainFragment();
            manager.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    public void pickPowerScreen() {
        PickPowerFragment powerFragment = new PickPowerFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, powerFragment)
                .addToBackStack(null).commit();
    }

    public void backStoryScreen() {
        BackStoryFragment backStoryFragment = new BackStoryFragment();
        this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, backStoryFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
