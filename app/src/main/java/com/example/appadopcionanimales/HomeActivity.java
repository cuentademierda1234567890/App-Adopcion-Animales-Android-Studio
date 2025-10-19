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
                Intent i = new Intent(HomeActivity.this, FilterActivity.class);
                startActivityForResult(i, 200);
            });

            // cargar animales
            loadAnimals(null);
        } catch (Exception e) {
            // capturamos cualquier error en onCreate para evitar crash silencioso
            Log.e(TAG, "Error en onCreate HomeActivity", e);
            Toast.makeText(this, "Error inicializando pantalla principal", Toast.LENGTH_LONG).show();
        }
    }

    private void loadAnimals(String query) {
        String url = Constants.URL_GET_ANIMALES;
        if (query != null && !query.isEmpty()) url += "?" + query;
        Log.d(TAG, "Request URL: " + url);

        StringRequest sr = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d(TAG, "Response: " + response);
                        if (response == null || response.trim().isEmpty()) {
                            Log.e(TAG, "Respuesta vacía");
                            runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Respuesta vacía del servidor", Toast.LENGTH_LONG).show());
                            return;
                        }

                        JSONObject obj = new JSONObject(response);
                        boolean ok = obj.optBoolean("success", false);
                        if (!ok) {
                            String msg = obj.optString("msg", "Respuesta sin éxito");
                            Log.w(TAG, "Server returned success=false: " + msg);
                            runOnUiThread(() -> {
                                Toast.makeText(HomeActivity.this, "Server: " + msg, Toast.LENGTH_LONG).show();
                                rvPets.setAdapter(null);
                            });
                            return;
                        }

                        JSONArray pets = obj.optJSONArray("pets");
                        if (pets == null || pets.length() == 0) {
                            Log.i(TAG, "No hay mascotas en respuesta JSON");
                            runOnUiThread(() -> {
                                Toast.makeText(HomeActivity.this, "No hay mascotas disponibles", Toast.LENGTH_SHORT).show();
                                rvPets.setAdapter(null);
                            });
                            return;
                        }

                        // Defensive: crear adapter en try/catch
                        try {
                            adapter = new PetAdapter(HomeActivity.this, pets);
                            runOnUiThread(() -> rvPets.setAdapter(adapter));
                        } catch (Exception e) {
                            Log.e(TAG, "Error creando adapter", e);
                            runOnUiThread(() -> {
                                Toast.makeText(HomeActivity.this, "Error mostrando mascotas", Toast.LENGTH_LONG).show();
                                rvPets.setAdapter(null);
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando JSON", e);
                        runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Error parseando respuesta del servidor", Toast.LENGTH_LONG).show());
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error cargando animales", error);
                    runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Error de red: revisá Logcat", Toast.LENGTH_LONG).show());
                });

        // poner un pequeño timeout / retry policy? (opcional)
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
