package com.nsweb.heroapp.ui.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.ui.activities.MainActivity;
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.data.database.SuperHeroDatabase;
import com.nsweb.heroapp.ui.dialogs.CustomDialog;
import com.nsweb.heroapp.data.domain.SuperHero;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IndividualHeroFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class IndividualHeroFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.start_over_btn)
    Button start_over_button;

    @BindView(R.id.individual_hero_tv)
    TextView individual_hero_text_view;

    @BindView(R.id.delete_btn)
    Button delete_button;

    @BindView(R.id.edit_btn)
    Button edit_button;

    @Nullable
    @BindView(R.id.superhero_image)
    ImageView superHeroImage;

    private Uri imagePath = Uri.parse("");

    @Inject
    SuperHeroDatabase database;

    public IndividualHeroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_hero, container, false);

        fragmentScope();

        ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        final SuperHero superHeroes = (SuperHero) bundle.getSerializable("valuesArray");
        if (bundle != null) {
            individual_hero_text_view.setText(superHeroes.getDescription());
            imagePath = Uri.parse(superHeroes.getImageUri());
            if(imagePath == null || imagePath.getPath() == null || imagePath.getPath().equals("")) {
                Drawable image = getResources().getDrawable(R.drawable.no_pic);
                superHeroImage.setImageDrawable(image);
            }
            else {
                //superHeroImage.setImageURI(imagePath);
                //Glide.with(this).load(new File(imagePath.toString())).override(500, 500).into(superHeroImage); // glide doesn't work either!
                Picasso.get().load(new File(imagePath.toString()))
                        .resize(500, 500).centerCrop().into(superHeroImage); // this works!
            }
        }

        start_over_button.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.returnToMainFragment();
        });

        delete_button.setOnClickListener(v -> {
            deleteFile();
            deletingSuperHero(superHeroes);
        });

        edit_button.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.updateIndividualHero(superHeroes);
        });

        return view;
    }

    private void fragmentScope() {
        Toothpick.setConfiguration(Configuration.forDevelopment());
        Scope scope = Toothpick.openScopes(SuperHeroApplication.getInstance(), MainActivity.class, this);
        Toothpick.inject(this, scope);
    }

    private void deletingSuperHero(SuperHero superHeroes) {
        CustomDialog areYouSureDialog = new CustomDialog(getActivity(),
                "Are you sure you want to delete the super hero?", "NO", "YES");
        areYouSureDialog.setContentView(R.layout.delete_superhero_dialog);
        areYouSureDialog.show();
        areYouSureDialog.setOnPositiveButtonClickListener(() -> {
            database.deleteSuperHero(superHeroes);
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.returnToMainFragment();

            long count = database.getSuperHeroCount();
            Toast.makeText(getContext(), "Number of super heroes after deleting: " + count, Toast.LENGTH_LONG).show();
            Timber.i("Number of super heroes after deleting: %d", count);
        });
        areYouSureDialog.setOnNegativeButtonClickListener(() -> System.out.println("Nothing was deleted."));
    }

    private void deleteFile() {
        File file = new File(imagePath.getPath());
        if(file.exists()) {
            file.delete();
        }
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
}