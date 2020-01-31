package com.nsweb.heroapp.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.nsweb.heroapp.BuildConfig;
import com.nsweb.heroapp.R;
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.data.database.SuperHeroDatabase;
import com.nsweb.heroapp.data.domain.SuperHero;
import com.nsweb.heroapp.ui.dialogoptions.DialogOptionsHelper;
import com.nsweb.heroapp.ui.dialogs.ChooseOptionDialog;
import com.nsweb.heroapp.ui.spinners.SuperHeroSpinner;
import com.nsweb.heroapp.viewmodel.SuperHeroViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class UpdateSuperHeroRestActivity extends AppCompatActivity {

    @BindView(R.id.superhero_name_et)
    EditText superHeroName;

    @BindView(R.id.superhero_description_et)
    EditText superHeroDescription;

    @BindView(R.id.superhero_power_one_spinner)
    Spinner super_hero_power_one_spinner;

    @BindView(R.id.superhero_power_two_spinner)
    Spinner super_hero_power_two_spinner;

    @BindView(R.id.update_btn)
    Button update_button;

    @BindView(R.id.image_btn)
    Button image_button;

    public Uri imageUri = Uri.parse("");

    private boolean clicked = false;

    private File photoFile;

    @Inject
    SuperHeroDatabase database;

    @Inject
    SuperHeroViewModel superHeroViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((SuperHeroApplication)getApplicationContext()).component.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_super_hero_rest);

        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> adapter = getCharSequenceArrayAdapter();

        setSpinners();

        super_hero_power_one_spinner.setAdapter(adapter);
        super_hero_power_two_spinner.setAdapter(adapter);

        final SuperHero superHero = getSuperHero();
        if(superHero.getImageUri() == null) {
            imageUri = Uri.parse("");
        }
        else {
            imageUri = Uri.parse(superHero.getImageUri());
        }
        deleteFile();

        update_button.setOnClickListener(v -> {
            String powerOne = (String) super_hero_power_one_spinner.getSelectedItem();
            String powerTwo = (String) super_hero_power_two_spinner.getSelectedItem();

            if (powerOne.equals(powerTwo)) {
                Toast.makeText(this, "Powers can't be equal!", Toast.LENGTH_LONG).show();
            } else {
                if (!clicked) {
                    deleteFile();
                    imageUri = Uri.parse("");
                }
                superHero.setName(superHeroName.getText().toString());
                superHero.setDescription(superHeroDescription.getText().toString());
                superHero.setPrimaryPower((String) super_hero_power_one_spinner.getSelectedItem());
                superHero.setSecondaryPower((String) super_hero_power_two_spinner.getSelectedItem());
                superHero.setImageUri(imageUri.toString());

                superHeroViewModel.updateSuperHero(superHero, superHero.getId()).observe(this, response -> {
                    long id = database.updateSuperHero(superHero);
                    Timber.i("Super Hero updated! ID: %d", id);
                    Toast.makeText(this,
                            "Congratulations! You updated the superhero with name: " + response.getName(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    public void browseGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, DialogOptionsHelper.BROWSE_GALLERY_REQUEST_UPDATE);
    }

    public void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            photoFile = createImageFile();
            if (photoFile != null) {
                uriFromFile(intent);
                startActivityForResult(intent, DialogOptionsHelper.CAMERA_REQUEST_UPDATE);
            }
        }
    }

    private void uriFromFile(Intent intent) {
        Uri photoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    private File createImageFile() {
        String imageTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "image_" + imageTimeStamp + "_";
        String suffix = ".jpg";

//        File picturesDirectory = getContext().getExternalFilesDir(null);
//        picturesDirectory.mkdir();
//        File imageFile = File.createTempFile(imageFileName, suffix, Environment.getExternalStorageDirectory()); // throws IOException

        File sdCard = Environment.getExternalStorageDirectory();
        File downloadDir = new File(sdCard.getAbsolutePath() + "/Download");
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        File imageFile = new File(downloadDir, imageFileName + suffix);

        return imageFile;
    }

    private void deleteFile() {
        File file = new File(Objects.requireNonNull(imageUri.getPath()));
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DialogOptionsHelper.BROWSE_GALLERY_REQUEST_UPDATE) {
            String[] columns = {MediaStore.Images.Media.DATA};

            Uri uri = Objects.requireNonNull(data).getData();
            String imagePath;
            try (Cursor cursor = this.getContentResolver().query(Objects.requireNonNull(uri), columns, null, null, null)) {
                Objects.requireNonNull(cursor).moveToFirst();

                int columnIndex = cursor.getColumnIndex(columns[0]);
                imagePath = cursor.getString(columnIndex);
            }
            imageUri = Uri.parse(imagePath);
        } else if (requestCode == DialogOptionsHelper.CAMERA_REQUEST_UPDATE) {
            galleryAddPicture();
            imageUri = Uri.parse(photoFile.getAbsolutePath());
        }
    }

    private void galleryAddPicture() {
        Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(photoFile.getAbsolutePath());
        Uri contentUri = Uri.fromFile(file);
        mediaIntent.setData(contentUri);
        this.sendBroadcast(mediaIntent);
    }

    @OnClick(R.id.image_btn)
    public void imageDialog() {
        ChooseOptionDialog dialog = new ChooseOptionDialog(this, "Please choose an option.");
        dialog.setOnGalleryButtonClickListener(this::browsePhotosFromGallery);
        dialog.setOnCameraButtonClickListener(this::takeAPhotoWithCamera);
        clicked = true;
    }

    private void browsePhotosFromGallery() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            browseGallery();
        } else {
            EasyPermissions.requestPermissions(this, "You need an access to a file storage.",
                    DialogOptionsHelper.BROWSE_GALLERY_REQUEST_UPDATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

    }

    private void takeAPhotoWithCamera() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            EasyPermissions.requestPermissions(this, "You need an access to a camera.",
                    DialogOptionsHelper.CAMERA_REQUEST_UPDATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private SuperHero getSuperHero() {
        Intent intent = getIntent();
        final SuperHero superHero = (SuperHero) intent.getSerializableExtra("updateSuperHero");
        long lastId = intent.getLongExtra("updateLastId", 0);

        superHero.setId(lastId);
        superHeroName.setText(superHero.getName());
        superHeroDescription.setText(superHero.getDescription());
        super_hero_power_one_spinner.setSelection(((ArrayAdapter<CharSequence>) super_hero_power_one_spinner.getAdapter())
                .getPosition(superHero.getPrimaryPower()));

        String[] secondaryPowers = getResources().getStringArray(R.array.superhero_array);
        super_hero_power_two_spinner.setSelection(Arrays.asList(secondaryPowers).indexOf(superHero.getSecondaryPower()));
        return superHero;
    }

    private ArrayAdapter<CharSequence> getCharSequenceArrayAdapter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.superhero_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void setSpinners() {
        super_hero_power_one_spinner.setOnItemSelectedListener(new SuperHeroSpinner());

        setSpinnerBackground(super_hero_power_one_spinner);

        super_hero_power_two_spinner.setOnItemSelectedListener(new SuperHeroSpinner());

        setSpinnerBackground(super_hero_power_two_spinner);
    }

    private void setSpinnerBackground(Spinner spinner) {
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.dropDownArrow), PorterDuff.Mode.SRC_ATOP);
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
    protected void onDestroy() {
        super.onDestroy();
    }
}