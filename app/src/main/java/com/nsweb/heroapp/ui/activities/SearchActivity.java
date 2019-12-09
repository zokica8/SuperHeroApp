package com.nsweb.heroapp.ui.activities;

import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.data.database.SuperHeroDatabase;
import com.nsweb.heroapp.data.domain.SuperHero;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.searchHero)
    EditText searchHero;

    @BindView(R.id.searchButton)
    Button searchButton;

    @Inject
    SuperHeroDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        activityScope();

        ButterKnife.bind(this);

        searchHero();
    }

    private void activityScope() {
        Toothpick.setConfiguration(Configuration.forDevelopment());
        Scope scope = Toothpick.openScopes(SuperHeroApplication.getInstance(), this);
        Toothpick.inject(this, scope);
    }

    private void searchHero() {
        searchButton.setOnClickListener(v -> {
            String input = searchHero.getText().toString();

            List<SuperHero> superHeroes = database.getSuperHeroByName(input);

            if (superHeroes.size() > 0) {
                Toast.makeText(SearchActivity.this,
                        "Congratulations, you found the superhero(es)! Count: " + superHeroes.size(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SearchActivity.this, "Sorry, no superheroes found! Please try again!", Toast.LENGTH_LONG).show();
            }
        });
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

    public void exit() {
        moveTaskToBack(true);
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
