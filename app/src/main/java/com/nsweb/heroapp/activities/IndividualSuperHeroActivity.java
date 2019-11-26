package com.nsweb.heroapp.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.database.SuperHeroDatabase;
import com.nsweb.heroapp.dialogs.CustomDialog;
import com.nsweb.heroapp.domain.SuperHero;
import com.nsweb.heroapp.fragments.UpdateHeroFragment;
import com.nsweb.heroapp.retrofit.client.SuperHeroClient;
import com.nsweb.heroapp.retrofit.configuration.RetrofitInstance;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

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

public class IndividualSuperHeroActivity extends AppCompatActivity implements UpdateHeroFragment.OnFragmentInteractionListener {

    @BindView(R.id.superhero_image)
    ImageView superHeroImage;

    @BindView(R.id.individual_hero_tv)
    TextView superHeroDescription;

    @BindView(R.id.start_over_btn)
    Button startOverButton;

    @BindView(R.id.edit_btn)
    Button editButton;

    @BindView(R.id.delete_btn)
    Button deleteButton;

    private SuperHeroDatabase database = new SuperHeroDatabase();

    private Uri imagePath = Uri.parse("");

    private MainActivity mainActivity = new MainActivity();

    private SuperHeroClient superHeroClient;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_super_hero);

        ButterKnife.bind(this);

        retrofit = RetrofitInstance.getRetrofitInstance("http","localhost", "3001");
        superHeroClient = retrofit.create(SuperHeroClient.class);

        getSuperHeroFromApi();

        startOverButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        editButton.setOnClickListener(v -> {
            Intent oldIntent = getIntent();
            SuperHero superHero = (SuperHero) oldIntent.getSerializableExtra("superhero");
            long position = oldIntent.getLongExtra("position", 0);
            Intent intent = new Intent(this, UpdateSuperHeroRestActivity.class);
            intent.putExtra("updateSuperHero", superHero);
            intent.putExtra("updateLastId", position);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> {
            deleteFile();
            Intent intent = getIntent();
            SuperHero superHero = (SuperHero) intent.getSerializableExtra("superhero");
            deletingSuperHero(superHero);
        });
    }

    private void deleteFile() {
        File file = new File(imagePath.getPath());
        if(file.exists()) {
            file.delete();
        }
    }

    private void deletingSuperHero(SuperHero superHeroes) {
        CustomDialog areYouSureDialog = new CustomDialog(this,
                "Are you sure you want to delete the super hero?", "NO", "YES");
        areYouSureDialog.setContentView(R.layout.delete_superhero_dialog);
        areYouSureDialog.show();
        areYouSureDialog.setOnPositiveButtonClickListener(() -> {
            Observable<SuperHero> superHero = superHeroClient.deleteSuperHero(superHeroes.getSuperhero_id());
            Disposable disposable = superHero
                    .map(hero -> hero)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        database.deleteSuperHero(superHeroes.getSuperhero_id());
                        long count = database.getSuperHeroCount();
                        Toast.makeText(IndividualSuperHeroActivity.this, "Number of super heroes after deleting: " + count, Toast.LENGTH_LONG).show();
                        Timber.i("Number of super heroes after deleting: %d", count);
                        Intent intent = new Intent(IndividualSuperHeroActivity.this, MainActivity.class);
                        startActivity(intent);
                    }, error -> Toast.makeText(IndividualSuperHeroActivity.this, "Can not get delete hero! Reason: " + error.getMessage(), Toast.LENGTH_LONG).show(),
                            () -> Timber.i("Delete completed on: %s", Thread.currentThread().getName()));

            mainActivity.compositeDisposable.add(disposable);
        });
        areYouSureDialog.setOnNegativeButtonClickListener(() -> System.out.println("Nothing was deleted."));
    }

    private void getSuperHeroFromApi() {
        Intent intent = getIntent();
        SuperHero superHero = (SuperHero) intent.getSerializableExtra("superhero");

        superHeroDescription.setText(superHero.getDescription());

        if(superHero.getImageUri() == null || superHero.getImageUri().equals("")) {
            Drawable image = getResources().getDrawable(R.drawable.no_pic);
            superHeroImage.setImageDrawable(image);
        }
        else {
//            Picasso.get().load(new File(superHero.getImageUri())).resize(500, 500).centerCrop()
//                    .into(superHeroImage);
            Picasso.get().load(superHero.getImageUri()).resize(500, 500).centerCrop()
                    .into(superHeroImage);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mainActivity.compositeDisposable.dispose();
    }
}
