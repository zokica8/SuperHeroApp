package com.nsweb.heroapp.ui.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.nsweb.heroapp.BuildConfig;
import com.nsweb.heroapp.R;
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.data.database.SuperHeroDatabase;
import com.nsweb.heroapp.data.domain.SuperHero;
import com.nsweb.heroapp.data.retrofit.configuration.RetrofitInstance;
import com.nsweb.heroapp.ui.activities.MainActivity;
import com.nsweb.heroapp.ui.dialogoptions.DialogOptionsHelper;
import com.nsweb.heroapp.ui.dialogs.ChooseOptionDialog;
import com.nsweb.heroapp.ui.spinners.SuperHeroSpinner;
import com.nsweb.heroapp.viewmodel.SuperHeroViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateHeroFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CreateHeroFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.save_btn)
    Button save_button;

    @BindView(R.id.superhero_power_one_spinner)
    Spinner super_hero_power_one_spinner;

    @BindView(R.id.superhero_power_two_spinner)
    Spinner super_hero_power_two_spinner;

    @BindView(R.id.superhero_name_et)
    EditText superHeroName;

    @BindView(R.id.superhero_description_et)
    EditText superHeroDescription;

    @BindView(R.id.image_btn)
    Button image_button;

    private static final String LOGTAG = "ZV";

    private Uri imageUri = Uri.parse("");

    private boolean clicked = false;

    private File photoFile;

    @Inject
    RetrofitInstance retrofitInstance;

    @Inject
    SuperHeroDatabase database;

    @Inject
    SuperHeroViewModel superHeroViewModel;

    public CreateHeroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_superhero, container, false);

        fragmentScope();

        ButterKnife.bind(this, view);
        ArrayAdapter<CharSequence> adapter = getCharSequenceArrayAdapter();

        setSpinners();

        super_hero_power_one_spinner.setAdapter(adapter);
        super_hero_power_two_spinner.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }

    private void fragmentScope() {
        Toothpick.setConfiguration(Configuration.forDevelopment());
        Scope scope = Toothpick.openScopes(SuperHeroApplication.getInstance(), MainActivity.class, this);
        Toothpick.inject(this, scope);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void browseGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), DialogOptionsHelper.BROWSE_GALLERY_REQUEST_CREATE);
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            photoFile = createImageFile();
            if (photoFile != null) {
                uriFromFile(intent);
                startActivityForResult(intent, DialogOptionsHelper.CAMERA_REQUEST_CREATE);
            }
        }
    }

    private void uriFromFile(Intent intent) {
        Uri photoUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    private File createImageFile() {
        String imageTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "image_" + imageTimeStamp + "_";
        String suffix = ".jpg";

        /*
        this is the directory in the external storage (SD card) that is specific for this application.
        Location: /storage/B99F-1DEF/Android/data/com.nsweb.heroapp/files
        Any picture created in Android folder is specific for the application and will not be shown in the gallery.
         */
//        File picturesDirectory = getContext().getExternalFilesDir(null);
//        picturesDirectory.mkdir();
        /*
        Environment.getExternalStorageDirectory() gets the root folder of the SD card and in that folder you will have a picture shown in the gallery.
        If you want to change the folder where you save the picture (not saving the picture in the root folder), you can get the absolute path of the SD card
        and just append the folder name where you save the picture. Also, check to see if that folder exists, and if it doesn't exist, create it.
        It doesn't matter if you create the new File with the File object or with the method createTempFile(). What matters is the directory where the file will be.
         */

        File sdCard = Environment.getExternalStorageDirectory();
        File downloadDir = new File(sdCard.getAbsolutePath() + "/Download");
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        //File imageFile = new File(Environment.getExternalStorageDirectory(), imageFileName + suffix);
        File imageFile = new File(downloadDir, imageFileName + suffix);
        //File imageFile = File.createTempFile(imageFileName, suffix, Environment.getExternalStorageDirectory()); // throws IOException
        //File imageFile = new File(picturesDirectory, imageFileName + suffix);

        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // code to get the image from a gallery and to get the URI to use later for displaying the image
        if (requestCode == DialogOptionsHelper.BROWSE_GALLERY_REQUEST_CREATE) {
            String[] columns = {MediaStore.Images.Media.DATA};


            Uri uri = Objects.requireNonNull(data).getData();
            String imagePath;
            try (Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(Objects.requireNonNull(uri), columns, null, null, null)) {
                Objects.requireNonNull(cursor).moveToFirst();

                int columnIndex = cursor.getColumnIndex(columns[0]);
                imagePath = cursor.getString(columnIndex);
            }
            imageUri = Uri.parse(imagePath);
        }
        // code to get the image from a camera and to get the URI to use later for displaying the image
        else if (requestCode == DialogOptionsHelper.CAMERA_REQUEST_CREATE) {
            addImageToGallery();
            imageUri = Uri.parse(photoFile.getAbsolutePath());
        }

    }

    private void addImageToGallery() {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        values.put(MediaStore.MediaColumns.DATA, photoFile.getAbsolutePath());

        Objects.requireNonNull(getContext()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    @OnClick(R.id.image_btn)
    public void imageDialog() {
        ChooseOptionDialog dialog = new ChooseOptionDialog(getActivity(), "Please choose an option.");
        dialog.setOnGalleryButtonClickListener(this::browsePhotosFromGallery);
        dialog.setOnCameraButtonClickListener(this::takeAPhotoWithCamera);
        clicked = true;
    }

    private void browsePhotosFromGallery() {
        if (EasyPermissions.hasPermissions(Objects.requireNonNull(getActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            browseGallery();
        } else {
            EasyPermissions.requestPermissions(getActivity(), "You need an access to a file storage.",
                    DialogOptionsHelper.BROWSE_GALLERY_REQUEST_CREATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

    }

    private void takeAPhotoWithCamera() {
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            EasyPermissions.requestPermissions(getActivity(), "You need an access to a camera.",
                    DialogOptionsHelper.CAMERA_REQUEST_CREATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @OnClick(R.id.save_btn)
    public void saveHero() {
        String powerOne = String.valueOf(super_hero_power_one_spinner.getSelectedItem());
        String powerTwo = String.valueOf(super_hero_power_two_spinner.getSelectedItem());

        if (powerOne.equals(powerTwo)) {
            Toast.makeText(getContext(), "Powers can't be equal!", Toast.LENGTH_LONG).show();
        } else {
            if (!clicked) {
                imageUri = Uri.parse("");
            }
            // save will be done both in the database and in the rest api
            SuperHero superHero = new SuperHero(superHeroName.getText().toString(), superHeroDescription.getText().toString(),
                    String.valueOf(super_hero_power_one_spinner.getSelectedItem()), String.valueOf(super_hero_power_two_spinner.getSelectedItem()),
                    imageUri.toString());
            superHeroViewModel.insertSuperHero(superHero).observe(this, newSuperHero -> Timber.i("SuperHero %s successfully created.", newSuperHero));

            long id = database.insertSuperHero(superHero);

            Toast.makeText(getContext(), "Id created: " + id, Toast.LENGTH_LONG).show();
            Log.i(LOGTAG, String.format("Super Hero inserted into database with ID: %d", id));

            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.returnToMainFragment();
        }
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

    private ArrayAdapter<CharSequence> getCharSequenceArrayAdapter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(Objects.requireNonNull(this.getActivity()), R.array.superhero_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}