package com.nsweb.heroapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsweb.heroapp.R;
import com.nsweb.heroapp.data.domain.SuperHero;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SuperHeroRecyclerAdapter extends RecyclerView.Adapter<SuperHeroRecyclerAdapter.SuperHeroViewHolder> {

    private List<SuperHero> superHeroes;
    private static ItemClickListener itemClickListener;

    public SuperHeroRecyclerAdapter(List<SuperHero> superHeroes) {
        this.superHeroes = superHeroes;
    }

    @NonNull
    @Override
    public SuperHeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);

        SuperHeroViewHolder viewHolder = new SuperHeroViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SuperHeroViewHolder holder, int position) {
        if(superHeroes.get(position).getImageUri().equals("")) {
            holder.superHeroImage.setImageResource(R.drawable.no_pic);
        }
        else {
            Picasso.get()
                    .load(superHeroes.get(position).getImageUri()).resize(300, 300).placeholder(R.drawable.no_pic)
                    .centerCrop().into(holder.superHeroImage);
        }
        holder.textViewSuperHeroName.setText(superHeroes.get(position).getName());
        holder.textViewSuperHeroDescription.setText(superHeroes.get(position).getDescription());
        holder.textViewSuperHeroPrimaryPower.setText(superHeroes.get(position).getPrimaryPower());
        holder.textViewSuperHeroSecondaryPower.setText(superHeroes.get(position).getSecondaryPower());
    }

    @Override
    public int getItemCount() {
        return superHeroes.size();
    }

    public static class SuperHeroViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView superHeroImage;
        public TextView textViewSuperHeroName;
        public TextView textViewSuperHeroDescription;
        public TextView textViewSuperHeroPrimaryPower;
        public TextView textViewSuperHeroSecondaryPower;

        public SuperHeroViewHolder(@NonNull View itemView) {
            super(itemView);
            superHeroImage = itemView.findViewById(R.id.superhero_image);
            textViewSuperHeroName = itemView.findViewById(R.id.textViewSuperHeroName);
            textViewSuperHeroDescription = itemView.findViewById(R.id.textViewSuperHeroDescription);
            textViewSuperHeroPrimaryPower = itemView.findViewById(R.id.textViewSuperHeroPrimaryPower);
            textViewSuperHeroSecondaryPower = itemView.findViewById(R.id.textViewSuperHeroSecondaryPower);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setItemClickListener(ItemClickListener onItemClickListener) {
        itemClickListener = onItemClickListener;
    }
}
