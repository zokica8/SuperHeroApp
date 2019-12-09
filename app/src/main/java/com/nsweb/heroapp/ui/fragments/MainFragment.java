package com.nsweb.heroapp.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.ui.activities.MainActivity;
import com.nsweb.heroapp.application.SuperHeroApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
@Singleton
public class MainFragment extends Fragment {

    @BindView(R.id.save_hero_btn)
    Button saveHeroButton;

    @BindView(R.id.list_heroes_btn)
    Button showHeroesButton;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private OnFragmentInteractionListener mListener;

    @Inject
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        fragmentScope();

        ButterKnife.bind(this, view);

        saveHeroButton.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.createHeroScreen();
        });

        showHeroesButton.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showHeroesScreen();
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void fragmentScope() {
        Toothpick.setConfiguration(Configuration.forDevelopment());
        Scope scope = Toothpick.openScopes(SuperHeroApplication.getInstance(), MainActivity.class, this);
        Toothpick.inject(this, scope);
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
