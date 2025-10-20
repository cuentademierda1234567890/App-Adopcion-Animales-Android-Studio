package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class PetDetailActivity extends AppCompatActivity {
    ImageView imgPet;
    TextView tvName, tvInfo, tvDesc;
    Button btnSolicitar;
    JSONObject pet;
    private static final String TAG = "PetDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        imgPet = findViewById(R.id.imgPetDetail);
        tvName = findViewById(R.id.tvPetNameDetail);
        tvInfo = findViewById(R.id.tvPetInfoDetail);
        tvDesc = findViewById(R.id.tvPetDesc);
        btnSolicitar = findViewById(R.id.btnSolicitar);

        // Leer pet_json del Intent
        String petJson = getIntent().getStringExtra("pet_json");
        if (petJson == null || petJson.trim().isEmpty()) {
            // Si no vino JSON, intentar por id (por compatibilidad)
            int petId = getIntent().getIntExtra("pet_id", 0);
            if (petId <= 0) {
                Toast.makeText(this, "Datos de la mascota no disponibles", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else {
                // Si solo viene id, cargamos desde API
                loadPetFromApi(petId);
                return;
            }
        }

        try {
            pet = new JSONObject(petJson);
            populateFromPet(pet);
        } catch (Exception e) {
            Log.e(TAG, "Error parseando pet_json", e);
            Toast.makeText(this, "Error cargando datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSolicitar.setOnClickListener(v -> {
            // Abrir ApplicationFormActivity pasando datos de la mascota
            try {
                Intent i = new Intent(PetDetailActivity.this, ApplicationFormActivity.class);
                i.putExtra("pet_id", pet.optInt("id", 0));
                i.putExtra("pet_nombre", pet.optString("nombre", ""));
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(PetDetailActivity.this, "No se pudo abrir formulario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFromPet(JSONObject pet) {
        String nombre = pet.optString("nombre", "Sin nombre");
        String tamano = pet.optString("tamano", "");
        String categoria = pet.optString("categoria", "");
        String sexo = pet.optString("sexo", "");
        String descripcion = pet.optString("descripcion", "");
        String foto = pet.optString("foto", "");

        tvName.setText(nombre);

        String info = "";
        if (!tamano.isEmpty()) info += tamano;
        if (!categoria.isEmpty()) info += (info.isEmpty() ? "" : " • ") + categoria;
        if (!sexo.isEmpty()) info += (info.isEmpty() ? "" : " • ") + sexo;
        tvInfo.setText(info);

        tvDesc.setText(descripcion != null ? descripcion : "");

        if (foto != null && !foto.isEmpty()) {
            try {
                Picasso.get().load(foto).fit().centerCrop().into(imgPet);
            } catch (Exception e) {
                imgPet.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            imgPet.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private void loadPetFromApi(int id) {
        // Si quieres mantener la opción de cargar por ID desde API,
        // implementa una llamada a Constants.URL_GET_ANIMAL?id=...
        // Por ahora avisamos y cerramos (evitar crash)
        Toast.makeText(this, "Carga por id no implementada en este build", Toast.LENGTH_SHORT).show();
        finish();
    }
}
