package com.nsweb.heroapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.data.domain.SuperHero;
import com.nsweb.heroapp.data.retrofit.configuration.RetrofitInstance;
import com.nsweb.heroapp.ui.adapters.SuperHeroRecyclerAdapter;
import com.nsweb.heroapp.viewmodel.SuperHeroViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class GetSuperHeroesActivity extends AppCompatActivity {

    @BindView(R.id.superhero_recycler_view)
    RecyclerView superHeroRecyclerView;

    @Inject
    SuperHeroViewModel superHeroViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((SuperHeroApplication)getApplicationContext()).component.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_super_heroes);

        ButterKnife.bind(this);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        superHeroRecyclerView.setLayoutManager(layoutManager);

        getSuperHeroesFromApi();
    }


    private void getSuperHeroesFromApi() {

        Intent intent = getIntent();
        List<SuperHero> superHeroes = (List<SuperHero>) intent.getSerializableExtra("superheroes");

        SuperHeroRecyclerAdapter superHeroRecyclerAdapter = new SuperHeroRecyclerAdapter(superHeroes);
        superHeroRecyclerView.setAdapter(superHeroRecyclerAdapter);

        superHeroRecyclerAdapter.setItemClickListener((view, position) -> {
            Timber.i("You clicked item with position: %d", position);
            findSuperHeroById(superHeroes.get(position).getId());
        });
    }

    private void findSuperHeroById(long id) {
        superHeroViewModel.getSuperHeroById(id).observe(this, instance -> getSuperHero(instance, id));
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

    private void getSuperHero(SuperHero superHero, long position) {
        Intent intent = new Intent();
        intent.putExtra("superhero", superHero);
        intent.putExtra("position", position);
        intent.setClass(this, IndividualSuperHeroActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
