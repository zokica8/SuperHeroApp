package com.nsweb.heroapp.fragments;

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
import com.nsweb.heroapp.activities.MainActivity;
import com.nsweb.heroapp.database.SuperHeroDatabase;
import com.nsweb.heroapp.dialogoptions.DialogOptionsHelper;
import com.nsweb.heroapp.dialogs.ChooseOptionDialog;
import com.nsweb.heroapp.domain.SuperHero;
import com.nsweb.heroapp.retrofit.client.SuperHeroClient;
import com.nsweb.heroapp.retrofit.configuration.RetrofitInstance;
import com.nsweb.heroapp.spinners.SuperHeroSpinner;
import com.orm.SugarRecord;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

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

    private SuperHeroDatabase database = new SuperHeroDatabase();

    private static final String LOGTAG = "ZV";

    public Uri imageUri = Uri.parse("");

    private boolean clicked = false;

    private File photoFile;

    private SuperHeroClient superHeroClient;

    private Retrofit retrofit;

    private long lastId = 0;

    private MainFragment mainFragment = new MainFragment();

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

        ButterKnife.bind(this, view);
        ArrayAdapter<CharSequence> adapter = getCharSequenceArrayAdapter();

        setSpinners();

        super_hero_power_one_spinner.setAdapter(adapter);
        super_hero_power_two_spinner.setAdapter(adapter);

        retrofit = RetrofitInstance.getRetrofitInstance("http", "localhost", "3001");
        superHeroClient = retrofit.create(SuperHeroClient.class);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void browseGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), DialogOptionsHelper.BROWSE_GALLERY_REQUEST_CREATE);
    }

    public void startCamera() {
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

            Uri uri = data.getData();
            String imagePath;
            try (Cursor cursor = getContext().getContentResolver().query(uri, columns, null, null, null)) {
                cursor.moveToFirst();

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

        getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
            Observable<SuperHero> insertedHero = superHeroClient.insertSuperHero(superHero);
            Disposable disposable = insertedHero
                    .map(created -> created)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        long getId = response.getSuperhero_id();
                        database.updateSequence(getId, "SUPER_HERO");
                    }, error -> Toast.makeText(getContext(), "Can not save individual hero! Reason: " + error.getMessage(), Toast.LENGTH_LONG).show(),
                            () -> Timber.i("Save completed on: %s", Thread.currentThread().getName()));

            long id = database.insertSuperHero(superHero);

            Toast.makeText(getContext(), "Id created: " + id, Toast.LENGTH_LONG).show();
            Log.i(LOGTAG, String.format("Super Hero inserted into database with ID: %d", id));

            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.returnToMainFragment();

            mainFragment.compositeDisposable.add(disposable);
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
                .createFromResource(this.getActivity(), R.array.superhero_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        mainFragment.compositeDisposable.dispose();
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
