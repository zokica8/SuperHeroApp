package com.nsweb.heroapp.ui.activities;

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
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.data.database.SuperHeroDatabase;
import com.nsweb.heroapp.data.domain.SuperHero;
import com.nsweb.heroapp.ui.dialogs.CustomDialog;
import com.nsweb.heroapp.ui.fragments.UpdateHeroFragment;
import com.nsweb.heroapp.viewmodel.SuperHeroViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private Uri imagePath = Uri.parse("");

    @Inject
    SuperHeroDatabase database;

    @Inject
    SuperHeroViewModel superHeroViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((SuperHeroApplication)getApplicationContext()).component.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_super_hero);

        ButterKnife.bind(this);

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
        File file = new File(Objects.requireNonNull(imagePath.getPath()));
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
            superHeroViewModel.deleteSuperHero(superHeroes.getId()).observe(this, response -> {
                database.deleteSuperHero(superHeroes.getId());
                long count = database.getSuperHeroCount();
                Toast.makeText(IndividualSuperHeroActivity.this, "Number of super heroes after deleting: " + count, Toast.LENGTH_LONG).show();
                Timber.i("Number of super heroes after deleting: %d", count);
                Intent intent = new Intent(IndividualSuperHeroActivity.this, MainActivity.class);
                startActivity(intent);
            });
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
    }
}
