package com.nsweb.heroapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nsweb.heroapp.activities.MainActivity;
import com.nsweb.heroapp.domain.SuperHero;

import java.util.List;

import javax.inject.Inject;

public class SuperHeroAdapter extends ArrayAdapter<SuperHero> {

    private MainActivity mainActivity = new MainActivity();

    public SuperHeroAdapter(@NonNull Context context, int resource, @NonNull List<SuperHero> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setText(mainActivity.loadSuperHeroes().get(position).getName());

        return view;
    }
}
