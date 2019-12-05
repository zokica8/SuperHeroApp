package com.nsweb.heroapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.nsweb.heroapp.activities.MainActivity;
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.database.SuperHeroDatabase;
import com.nsweb.heroapp.dialogoptions.DialogOptionsHelper;
import com.nsweb.heroapp.dialogs.ChooseOptionDialog;
import com.nsweb.heroapp.domain.SuperHero;
import com.nsweb.heroapp.spinners.SuperHeroSpinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateHeroFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class UpdateHeroFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

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

    private Unbinder unbinder;

    public Uri imageUri = Uri.parse("");

    private boolean clicked = false;

    private File photoFile;

    @Inject
    SuperHeroDatabase database;

    @Inject
    public UpdateHeroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toothpick.setConfiguration(Configuration.forDevelopment());
        Scope scope = Toothpick.openScopes(SuperHeroApplication.getInstance(), MainActivity.class, this);
        Toothpick.inject(this, scope);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void browseGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, DialogOptionsHelper.BROWSE_GALLERY_REQUEST_UPDATE);
    }

    public void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            photoFile = createImageFile();
            if (photoFile != null) {
                uriFromFile(intent);
                startActivityForResult(intent, DialogOptionsHelper.CAMERA_REQUEST_UPDATE);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update_hero, container, false);

        unbinder = ButterKnife.bind(this, view);

        ArrayAdapter<CharSequence> adapter = getCharSequenceArrayAdapter();

        setSpinners();

        super_hero_power_one_spinner.setAdapter(adapter);
        super_hero_power_two_spinner.setAdapter(adapter);

        final SuperHero superHero = getSuperHero();
        imageUri = Uri.parse(superHero.getImageUri());
        deleteFile();

        update_button.setOnClickListener(v -> {
            String powerOne = (String) super_hero_power_one_spinner.getSelectedItem();
            String powerTwo = (String) super_hero_power_two_spinner.getSelectedItem();

            if (powerOne.equals(powerTwo)) {
                Toast.makeText(getContext(), "Powers can't be equal!", Toast.LENGTH_LONG).show();
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
                long id = database.updateSuperHero(superHero);

                Toast.makeText(getContext(), "Super Hero updated! ID: " + id, Toast.LENGTH_LONG).show();
                Timber.i("Super Hero updated! ID: %d", id);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void deleteFile() {
        File file = new File(imageUri.getPath());
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DialogOptionsHelper.BROWSE_GALLERY_REQUEST_UPDATE) {
            String[] columns = {MediaStore.Images.Media.DATA};

            Uri uri = data.getData();
            String imagePath;
            try (Cursor cursor = getContext().getContentResolver().query(uri, columns, null, null, null)) {
                cursor.moveToFirst();

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
        getActivity().sendBroadcast(mediaIntent);
    }

    @OnClick(R.id.image_btn)
    public void imageDialog() {
        ChooseOptionDialog dialog = new ChooseOptionDialog(getActivity(), "Please choose an option.");
        dialog.setOnGalleryButtonClickListener(() -> browsePhotosFromGallery());
        dialog.setOnCameraButtonClickListener(() -> takeAPhotoWithCamera());
        clicked = true;
    }

    private void browsePhotosFromGallery() {
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            browseGallery();
        } else {
            EasyPermissions.requestPermissions(getActivity(), "You need an access to a file storage.",
                    DialogOptionsHelper.BROWSE_GALLERY_REQUEST_UPDATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

    }

    private void takeAPhotoWithCamera() {
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            EasyPermissions.requestPermissions(getActivity(), "You need an access to a camera.",
                    DialogOptionsHelper.CAMERA_REQUEST_UPDATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private SuperHero getSuperHero() {
        Bundle bundle = this.getArguments();
        final SuperHero superHero = (SuperHero) bundle.getSerializable("hero");

        if (bundle != null) {
            superHeroName.setText(superHero.getName());
            superHeroDescription.setText(superHero.getDescription());
            super_hero_power_one_spinner.setSelection(((ArrayAdapter<CharSequence>) super_hero_power_one_spinner.getAdapter())
                    .getPosition(superHero.getPrimaryPower()));

            String[] secondaryPowers = getResources().getStringArray(R.array.superhero_array);
            super_hero_power_two_spinner.setSelection(Arrays.asList(secondaryPowers).indexOf(superHero.getSecondaryPower()));
        }
        return superHero;
    }

    private ArrayAdapter<CharSequence> getCharSequenceArrayAdapter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this.getActivity(), R.array.superhero_array, android.R.layout.simple_spinner_item);

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
    public void onAttach(Context context) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}