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
        if (this.pets == null) this.pets = new JSONArray();
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
            JSONObject pet = pets.optJSONObject(position);
            if (pet == null) {
                holder.tvPetName.setText("Sin nombre");
                holder.tvPetInfo.setText("");
                holder.imgPet.setImageResource(R.mipmap.ic_launcher);
                return;
            }

            holder.tvPetName.setText(pet.optString("nombre", "Sin nombre"));

            String tamano = pet.optString("tamano", "");
            String categoria = pet.optString("categoria", "");
            String sexo = pet.optString("sexo", "");
            String info = "";
            if (!tamano.isEmpty()) info += tamano;
            if (!categoria.isEmpty()) info += (info.isEmpty() ? "" : " • ") + categoria;
            if (!sexo.isEmpty()) info += (info.isEmpty() ? "" : " • ") + sexo;
            holder.tvPetInfo.setText(info);

            String foto = pet.optString("foto", "");
            if (foto != null && !foto.isEmpty()) {
                try {
                    Picasso.get().load(foto).fit().centerCrop().into(holder.imgPet);
                } catch (Exception e) {
                    holder.imgPet.setImageResource(R.mipmap.ic_launcher);
                }
            } else {
                holder.imgPet.setImageResource(R.mipmap.ic_launcher);
            }

            // Al tocar: enviar la mascota completa como JSON string al detalle
            holder.itemView.setOnClickListener(v -> {
                try {
                    Intent i = new Intent(ctx, PetDetailActivity.class);
                    i.putExtra("pet_json", pet.toString());
                    ctx.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            holder.tvPetName.setText("Error");
            holder.tvPetInfo.setText("");
            holder.imgPet.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return pets != null ? pets.length() : 0;
    }
}
