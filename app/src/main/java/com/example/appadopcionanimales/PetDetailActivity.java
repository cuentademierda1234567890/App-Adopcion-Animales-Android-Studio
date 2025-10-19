package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class PetDetailActivity extends AppCompatActivity {
    ImageView imgPet;
    TextView tvName, tvAttrs, tvDesc;
    Button btnRequest;
    int petId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        imgPet = findViewById(R.id.imgPetMain);
        tvName = findViewById(R.id.tvPetName);
        tvAttrs = findViewById(R.id.tvPetAttrs);
        tvDesc = findViewById(R.id.tvPetDesc);
        btnRequest = findViewById(R.id.btnRequest);

        petId = getIntent().getIntExtra("pet_id", 0);
        if (petId<=0) { finish(); return; }

        loadPet();

        btnRequest.setOnClickListener(v -> {
            Intent i = new Intent(PetDetailActivity.this, ApplicationFormActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });
    }

    private void loadPet() {
        String url = Constants.URL_GET_ANIMAL + "?id=" + petId;
        StringRequest sr = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONObject pet = obj.getJSONObject("pet");
                            tvName.setText(pet.optString("nombre","Sin nombre"));
                            String attrs = pet.optString("tamano","") + " • " + pet.optString("categoria","") + " • " + pet.optString("sexo","");
                            tvAttrs.setText(attrs);
                            tvDesc.setText(pet.optString("descripcion",""));
                            String foto = pet.optString("foto","");
                            if (foto != null && !foto.isEmpty()) Picasso.get().load(foto).fit().centerCrop().into(imgPet);
                        } else {
                            Toast.makeText(this, "Mascota no encontrada", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(sr);
    }
}
