package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * HomeActivity - versión diagnóstica.
 * Reemplazar completamente el archivo por este para detectar por qué el btnFilter no responde.
 */
public class HomeActivity extends AppCompatActivity {
    RecyclerView rvPets;
    ImageView imgAvatar;
    TextView tvWelcome;
    ImageButton btnFilter;
    ConstraintLayout topBar;
    int userId;
    String userNombre, userFoto;
    PetAdapter adapter;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_home);

            // referencias
            rvPets = findViewById(R.id.rvPets);
            imgAvatar = findViewById(R.id.imgAvatar);
            tvWelcome = findViewById(R.id.tvWelcome);
            btnFilter = findViewById(R.id.btnFilter);
            topBar = findViewById(R.id.topBar);

            // Log inicial para verificar que el layout correcto fue inflado
            Log.d(TAG, "onCreate: rvPets null? " + (rvPets==null));
            Log.d(TAG, "onCreate: imgAvatar null? " + (imgAvatar==null));
            Log.d(TAG, "onCreate: tvWelcome null? " + (tvWelcome==null));
            Log.d(TAG, "onCreate: btnFilter null? " + (btnFilter==null));
            Log.d(TAG, "onCreate: topBar null? " + (topBar==null));

            // Mostrar los hijos de topBar si existe (para ver si btnFilter está ahí o tapado)
            if (topBar != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("topBar children ids: ");
                for (int i = 0; i < topBar.getChildCount(); i++) {
                    View c = topBar.getChildAt(i);
                    int id = c.getId();
                    String name = (id != View.NO_ID) ? getResources().getResourceEntryName(id) : "no_id";
                    sb.append(name).append("(").append(id).append(") ");
                }
                Log.d(TAG, sb.toString());
            } else {
                Log.w(TAG, "topBar es null — revisá activity_home.xml");
            }

            // asegurar layout manager
            if (rvPets != null) rvPets.setLayoutManager(new LinearLayoutManager(this));

            // extras
            if (getIntent() != null) {
                userId = getIntent().getIntExtra("user_id", 0);
                userNombre = getIntent().getStringExtra("user_nombre");
                userFoto = getIntent().getStringExtra("user_foto");
            } else {
                userId = 0; userNombre = ""; userFoto = "";
            }

            String displayName = "Usuario";
            if (userNombre != null && !userNombre.trim().isEmpty()) {
                String[] parts = userNombre.trim().split("\\s+");
                if (parts.length > 0) displayName = parts[0];
            }
            if (tvWelcome != null) tvWelcome.setText("Hola, " + displayName);

            // Si btnFilter existe, forzamos bringToFront y listener
            if (btnFilter != null) {
                btnFilter.bringToFront();
                btnFilter.setOnClickListener(v -> {
                    Log.d(TAG, "FILTER_CLICK - listener fired");
                    Toast.makeText(HomeActivity.this, "Abrir filtros...", Toast.LENGTH_SHORT).show();
                    openFilter();
                });
            } else {
                Log.w(TAG, "btnFilter == null -> crear boton DBG programaticamente");
                // Crear un botón de debug y añadirlo al topBar para probar clicks
                if (topBar != null) {
                    Button dbg = new Button(this);
                    dbg.setText("DBG");
                    dbg.setId(View.generateViewId());
                    // params: WRAP_CONTENT en ConstraintLayout: usamos ConstraintLayout.LayoutParams
                    ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    // Anclar a la derecha del topBar
                    lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                    lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    lp.setMarginEnd(8);
                    dbg.setLayoutParams(lp);
                    topBar.addView(dbg);
                    dbg.bringToFront();

                    dbg.setOnClickListener(v -> {
                        Log.d(TAG, "DBG_BUTTON_CLICKED");
                        Toast.makeText(HomeActivity.this, "DBG click recibido", Toast.LENGTH_SHORT).show();
                        openFilter();
                    });

                    Log.d(TAG, "DBG boton añadido a topBar con id: " + dbg.getId());
                } else {
                    Log.e(TAG, "No se pudo crear DBG porque topBar es null");
                }
            }

            // Cargar animales inicialmente
            loadAnimals(null);

        } catch (Exception e) {
            Log.e(TAG, "Error onCreate HomeActivity", e);
            Toast.makeText(this, "Error inicializando Home (ver Logcat)", Toast.LENGTH_LONG).show();
        }
    }

    private void openFilter() {
        try {
            Intent i = new Intent(HomeActivity.this, FilterActivity.class);
            startActivityForResult(i, 200);
        } catch (Exception e) {
            Log.e(TAG, "No se pudo abrir FilterActivity", e);
            Toast.makeText(this, "No se pudo abrir filtros", Toast.LENGTH_SHORT).show();
        }
    }

    // Load animals (misma implementación defensiva que antes)
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
                            if (rvPets != null) rvPets.setAdapter(null);
                            return;
                        }
                        JSONObject obj = new JSONObject(response);
                        boolean ok = obj.optBoolean("success", false);
                        if (!ok) {
                            Log.w(TAG, "Server returned success=false: " + obj.optString("msg"));
                            if (rvPets != null) rvPets.setAdapter(null);
                            return;
                        }
                        JSONArray pets = obj.optJSONArray("pets");
                        if (pets == null || pets.length() == 0) {
                            Log.i(TAG, "No hay mascotas");
                            if (rvPets != null) rvPets.setAdapter(null);
                            return;
                        }
                        adapter = new PetAdapter(HomeActivity.this, pets);
                        if (rvPets != null) rvPets.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando JSON", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error cargando animales", error);
                });

        Volley.newRequestQueue(this).add(sr);
    }

    // onActivityResult intacto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 200 && data != null) {
                String query = data.getStringExtra("query");
                if (query == null || query.trim().isEmpty()) loadAnimals(null);
                else loadAnimals(query);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en onActivityResult", e);
        }
    }
}
