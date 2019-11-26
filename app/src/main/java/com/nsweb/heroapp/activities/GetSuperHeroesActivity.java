package com.nsweb.heroapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.adapters.SuperHeroRecyclerAdapter;
import com.nsweb.heroapp.domain.SuperHero;
import com.nsweb.heroapp.retrofit.client.SuperHeroClient;
import com.nsweb.heroapp.retrofit.configuration.RetrofitInstance;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

public class GetSuperHeroesActivity extends AppCompatActivity {

    @BindView(R.id.superhero_recycler_view)
    RecyclerView superHeroRecyclerView;

    private RecyclerView.LayoutManager layoutManager;
    private SuperHeroRecyclerAdapter superHeroRecyclerAdapter;

    private Retrofit retrofit;
    private SuperHeroClient superHeroClient;

    private MainActivity mainActivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_super_heroes);

        ButterKnife.bind(this);

        layoutManager = new LinearLayoutManager(this);
        superHeroRecyclerView.setLayoutManager(layoutManager);

        getSuperHeroesFromApi();
    }

    private void getSuperHeroesFromApi() {

        Intent intent = getIntent();
        List<SuperHero> superHeroes = (List<SuperHero>) intent.getSerializableExtra("superheroes");

        superHeroRecyclerAdapter = new SuperHeroRecyclerAdapter(superHeroes);
        superHeroRecyclerView.setAdapter(superHeroRecyclerAdapter);

        retrofit = RetrofitInstance.getRetrofitInstance("http", "localhost", "3001");
        superHeroClient = retrofit.create(SuperHeroClient.class);

        superHeroRecyclerAdapter.setItemClickListener((view, position) -> {
            Timber.i("You clicked item with position: %d", position);
            findSuperHeroById(superHeroes.get(position).getSuperhero_id());
        });
    }

    private void findSuperHeroById(long id) {
        Observable<SuperHero> superHero = superHeroClient.getSuperHeroById(id);

        Disposable disposable = superHero.map(i -> i)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(instance -> getSuperHero(instance, id),
                        error -> Toast.makeText(this, "Can not get individual hero! Reason: " + error.getMessage(), Toast.LENGTH_LONG).show(),
                        () -> Timber.i("Process completed on: %s", Thread.currentThread().getName()));

        mainActivity.compositeDisposable.add(disposable);
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

        mainActivity.compositeDisposable.dispose();
    }
}
