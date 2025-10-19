package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import org.json.JSONArray;

public class HomeActivity extends AppCompatActivity {
    RecyclerView rvPets;
    ImageView imgAvatar;
    TextView tvWelcome;
    ImageButton btnFilter;
    int userId;
    String userNombre, userFoto;
    PetAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rvPets = findViewById(R.id.rvPets);
        imgAvatar = findViewById(R.id.imgAvatar);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnFilter = findViewById(R.id.btnFilter);

        rvPets.setLayoutManager(new LinearLayoutManager(this));

        userId = getIntent().getIntExtra("user_id", 0);
        userNombre = getIntent().getStringExtra("user_nombre");
        userFoto = getIntent().getStringExtra("user_foto");

        tvWelcome.setText("Hola, " + (userNombre != null ? userNombre.split(" ")[0] : "Usuario"));

        btnFilter.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, FilterActivity.class);
            startActivityForResult(i, 200);
        });

        loadAnimals(null);
    }

    private void loadAnimals(String query) {
        String url = Constants.URL_GET_ANIMALES;
        if (query != null && !query.isEmpty()) url += "?" + query;
        StringRequest sr = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONArray pets = obj.getJSONArray("pets");
                            adapter = new PetAdapter(HomeActivity.this, pets);
                            rvPets.setAdapter(adapter);
                        } else {
                            Toast.makeText(HomeActivity.this, "No hay mascotas", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(HomeActivity.this, "Error parseando datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(HomeActivity.this, "Error de red", Toast.LENGTH_SHORT).show()
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
