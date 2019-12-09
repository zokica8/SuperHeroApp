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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button accident_btn;
    private Button genetic_btn;
    private Button born_btn;
    private Button powers_btn;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_main, container, false);

       accident_btn = view.findViewById(R.id.accident_btn);
       genetic_btn = view.findViewById(R.id.genetic_btn);
       born_btn = view.findViewById(R.id.born_btn);
       powers_btn = view.findViewById(R.id.powers_btn);

       accident_btn.setOnClickListener(this);
       genetic_btn.setOnClickListener(this);
       born_btn.setOnClickListener(this);

       powers_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               MainActivity mainActivity = (MainActivity) getActivity();
               mainActivity.pickPowerScreen();
           }
       });

       powers_btn.setEnabled(false);
       powers_btn.getBackground().setAlpha(128);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        powers_btn.setEnabled(true);
        powers_btn.getBackground().setAlpha(255);

        Button button = (Button) v;
        accident_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightning, 0, 0, 0);
        genetic_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.atomic, 0, 0, 0);
        born_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket, 0, 0, 0);
        int leftDrawable = 0;

        if (button == accident_btn) {
            leftDrawable = R.drawable.lightning;
        }
        else if (button == genetic_btn) {
            leftDrawable = R.drawable.atomic;
        }
        else if (button == born_btn) {
            leftDrawable = R.drawable.rocket;
        }

        button.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, 0, R.drawable.item_selected, 0);
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
