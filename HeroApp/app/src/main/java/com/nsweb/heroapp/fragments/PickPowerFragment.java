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
 * {@link PickPowerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PickPowerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickPowerFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button turtle_power_button;
    private Button lightning_button;
    private Button flight_button;
    private Button web_button;
    private Button laser_button;
    private Button strength_button;
    private Button backstory_button;

    public PickPowerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PickPowerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickPowerFragment newInstance(String param1, String param2) {
        PickPowerFragment fragment = new PickPowerFragment();
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
        View view = inflater.inflate(R.layout.fragment_pick_power, container, false);

        turtle_power_button = view.findViewById(R.id.turtle_power_btn);
        lightning_button = view.findViewById(R.id.lightning_btn);
        flight_button = view.findViewById(R.id.flight_btn);
        web_button = view.findViewById(R.id.web_btn);
        laser_button = view.findViewById(R.id.laser_btn);
        strength_button = view.findViewById(R.id.strength_btn);
        backstory_button = view.findViewById(R.id.back_story_btn);

        turtle_power_button.setOnClickListener(this);
        lightning_button.setOnClickListener(this);
        flight_button.setOnClickListener(this);
        web_button.setOnClickListener(this);
        laser_button.setOnClickListener(this);
        strength_button.setOnClickListener(this);

        backstory_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.backStoryScreen();
            }
        });

        backstory_button.setEnabled(false);
        backstory_button.getBackground().setAlpha(128);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        backstory_button.setEnabled(true);
        backstory_button.getBackground().setAlpha(255);

        Button button = (Button) view;
        turtle_power_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.turtle_power, 0, 0, 0);
        lightning_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightning, 0, 0, 0);
        flight_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.super_man_crest, 0, 0, 0);
        web_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spider_web, 0, 0, 0);
        laser_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.laser_vision, 0, 0, 0);
        strength_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.super_strength, 0, 0, 0);

        int leftDrawable = 0;

        if(button == turtle_power_button) {
            leftDrawable = R.drawable.turtle_power;
        }
        else if(button == lightning_button) {
            leftDrawable = R.drawable.lightning;
        }
        else if(button == flight_button) {
            leftDrawable = R.drawable.super_man_crest;
        }
        else if(button == web_button) {
            leftDrawable = R.drawable.spider_web;
        }
        else if(button == laser_button) {
            leftDrawable = R.drawable.laser_vision;
        }
        else if(button == strength_button) {
            leftDrawable = R.drawable.super_strength;
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
