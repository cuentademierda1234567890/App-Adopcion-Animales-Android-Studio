package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {
    RecyclerView rvPets;
    ImageView imgAvatar;
    TextView tvWelcome;
    ImageButton btnFilter;
    int userId;
    String userNombre, userFoto;
    PetAdapter adapter;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_home);

            rvPets = findViewById(R.id.rvPets);
            imgAvatar = findViewById(R.id.imgAvatar);
            tvWelcome = findViewById(R.id.tvWelcome);
            btnFilter = findViewById(R.id.btnFilter);

            rvPets.setLayoutManager(new LinearLayoutManager(this));

            // leer extras con seguridad
            if (getIntent() != null) {
                userId = getIntent().getIntExtra("user_id", 0);
                userNombre = getIntent().getStringExtra("user_nombre");
                userFoto = getIntent().getStringExtra("user_foto");
            } else {
                userId = 0;
                userNombre = "";
                userFoto = "";
            }

            String displayName = "Usuario";
            if (userNombre != null && !userNombre.trim().isEmpty()) {
                String[] parts = userNombre.trim().split("\\s+");
                if (parts.length > 0) displayName = parts[0];
                else displayName = userNombre;
            }
            tvWelcome.setText("Hola, " + displayName);

            btnFilter.setOnClickListener(v -> {
                // abre filtros
                startActivityForResult(new Intent(HomeActivity.this, FilterActivity.class), 200);
            });

            loadAnimals(null);
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate HomeActivity", e);
            Toast.makeText(this, "Error inicializando pantalla principal", Toast.LENGTH_LONG).show();
        }
    }

    private void loadAnimals(String query) {
        String url = Constants.URL_GET_ANIMALES;
        if (query != null && !query.isEmpty()) url += "?" + query;
        StringRequest sr = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        if (response == null || response.trim().isEmpty()) {
                            rvPets.setAdapter(null);
                            return;
                        }
                        JSONObject obj = new JSONObject(response);
                        boolean ok = obj.optBoolean("success", false);
                        if (!ok) {
                            rvPets.setAdapter(null);
                            return;
                        }
                        JSONArray pets = obj.optJSONArray("pets");
                        if (pets == null || pets.length() == 0) {
                            rvPets.setAdapter(null);
                            return;
                        }
                        // *** IMPORTANTE: ahora pasamos userId al Adapter para que pueda enviarlo a PetDetailActivity
                        adapter = new PetAdapter(HomeActivity.this, pets, userId);
                        rvPets.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        );
        Volley.newRequestQueue(this).add(sr);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && data != null) {
            String query = data.getStringExtra("query");
            loadAnimals(query);
        }
    }
}
