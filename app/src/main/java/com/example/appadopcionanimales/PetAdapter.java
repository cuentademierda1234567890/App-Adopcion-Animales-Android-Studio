package com.example.appadopcionanimales;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.ViewHolder> {
    Context ctx;
    JSONArray pets;

    public PetAdapter(Context c, JSONArray petsArray) {
        this.ctx = c;
        this.pets = petsArray;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPet;
        TextView tvPetName, tvPetInfo;
        public ViewHolder(View v) {
            super(v);
            imgPet = v.findViewById(R.id.imgPet);
            tvPetName = v.findViewById(R.id.tvPetName);
            tvPetInfo = v.findViewById(R.id.tvPetInfo);
        }
    }

    @Override
    public PetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PetAdapter.ViewHolder holder, int position) {
        try {
            JSONObject pet = pets.getJSONObject(position);
            holder.tvPetName.setText(pet.optString("nombre","Sin nombre"));
            String info = pet.optString("tamano","") + " • " + pet.optString("categoria","");
            holder.tvPetInfo.setText(info);

            String foto = pet.optString("foto","");
            if (foto != null && !foto.isEmpty()) {
                Picasso.get().load(foto).fit().centerCrop().into(holder.imgPet);
            } else {
                holder.imgPet.setImageResource(R.mipmap.ic_launcher);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(ctx, PetDetailActivity.class);
                try { i.putExtra("pet_id", pet.getInt("id")); } catch (Exception e) {}
                ctx.startActivity(i);
            });

        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public int getItemCount() {
        return pets.length();
    }
}
