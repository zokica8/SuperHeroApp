package com.nsweb.heroapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.activities.MainActivity;
import com.nsweb.heroapp.adapters.SuperHeroAdapter;
import com.nsweb.heroapp.database.SuperHeroDatabase;
import com.nsweb.heroapp.domain.SuperHero;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowHeroesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShowHeroesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.superheroes)
    ListView superheroes_list;

    public ShowHeroesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_superheroes, container, false);

        ButterKnife.bind(this, view);

        final MainActivity mainActivity = (MainActivity) getActivity();

        SuperHeroAdapter superHeroes = new SuperHeroAdapter(getActivity(), android.R.layout.simple_list_item_1, mainActivity.loadSuperHeroes());

        superheroes_list.setAdapter(superHeroes);

        superheroes_list.setOnItemClickListener((adapterView, view1, position, l) -> {
            SuperHero value = (SuperHero) adapterView.getItemAtPosition(position);
            mainActivity.showIndividualHero(value);
        });

        return view;
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
