package com.nsweb.heroapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.activities.GetSuperHeroesActivity;
import com.nsweb.heroapp.activities.HttpActivity;
import com.nsweb.heroapp.application.SuperHeroApplication;
import com.nsweb.heroapp.domain.SuperHero;
import com.nsweb.heroapp.retrofit.configuration.RetrofitInstance;
import com.nsweb.heroapp.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HttpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HttpFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.http_get_button)
    Button httpGetButton;

    @Inject
    RetrofitInstance retrofitInstance;

    @Inject
    MainFragment mainFragment;

    public HttpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_http, container, false);

        fragmentScope();

        ButterKnife.bind(this, view);

        httpGetAllSuperHeroes();

        return view;
    }

    private void fragmentScope() {
        Toothpick.setConfiguration(Configuration.forDevelopment());
        Scope scope = Toothpick.openScopes(SuperHeroApplication.getInstance(), HttpActivity.class, this);
        Toothpick.inject(this, scope);
    }

    private void httpGetAllSuperHeroes() {
        httpGetButton.setOnClickListener(v -> getAllSuperHeroes());
    }

    private void getAllSuperHeroes() {
        Observable<List<SuperHero>> superHeroes = retrofitInstance.client().getAllSuperHeroes();

        Disposable disposable = superHeroes
                .map(superHeroesList -> superHeroesList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadSuperHeroesFromRestApi,
                        error -> Toast.makeText(getContext(),"Could not load the superheroes! Reason: " + error.getMessage(), Toast.LENGTH_LONG).show(),
                        () -> Timber.i("Process finished on: %s", Thread.currentThread().getName()));

        mainFragment.compositeDisposable.add(disposable);
    }

    private void loadSuperHeroesFromRestApi(List<SuperHero> superHeroesFromResponse) {
        int count = PreferencesUtils.getCount(getContext());
        if(count < 1) {
            SuperHero.saveInTx(superHeroesFromResponse);
            PreferencesUtils.setCount(getContext(), count + 1);
        }

        Intent intent = new Intent();
        intent.putExtra("superheroes", (ArrayList<SuperHero>)superHeroesFromResponse);
        intent.setClass(getActivity(), GetSuperHeroesActivity.class);
        startActivity(intent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
