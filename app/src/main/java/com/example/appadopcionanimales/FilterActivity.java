package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FilterActivity extends AppCompatActivity {

    Spinner spTamano, spSexo, spCategoria;
    Button btnApply, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        spTamano = findViewById(R.id.spTamano);
        spSexo = findViewById(R.id.spSexo);
        spCategoria = findViewById(R.id.spCategoria);
        btnApply = findViewById(R.id.btnApply);
        btnClear = findViewById(R.id.btnClear);

        setupSpinners();

        btnApply.setOnClickListener(v -> applyFilters());
        btnClear.setOnClickListener(v -> clearFilters());
    }

    private void setupSpinners() {
        ArrayAdapter<String> adapterTamano = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[] {"Todos", "Pequeño", "Mediano", "Grande"});
        adapterTamano.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTamano.setAdapter(adapterTamano);

        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[] {"Todos", "Macho", "Hembra"});
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSexo.setAdapter(adapterSexo);

        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[] {"Todos", "Perro", "Gato"});
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategoria);
    }

    private void applyFilters() {
        try {
            String tamano = spTamano.getSelectedItem() != null ? spTamano.getSelectedItem().toString() : "Todos";
            String sexo = spSexo.getSelectedItem() != null ? spSexo.getSelectedItem().toString() : "Todos";
            String categoria = spCategoria.getSelectedItem() != null ? spCategoria.getSelectedItem().toString() : "Todos";

            StringBuilder q = new StringBuilder();

            if (!"Todos".equalsIgnoreCase(tamano)) {
                q.append("tamano=").append(URLEncoder.encode(tamano, StandardCharsets.UTF_8.toString()));
            }
            if (!"Todos".equalsIgnoreCase(sexo)) {
                if (q.length() > 0) q.append("&");
                q.append("sexo=").append(URLEncoder.encode(sexo, StandardCharsets.UTF_8.toString()));
            }
            if (!"Todos".equalsIgnoreCase(categoria)) {
                if (q.length() > 0) q.append("&");
                q.append("categoria=").append(URLEncoder.encode(categoria, StandardCharsets.UTF_8.toString()));
            }

            Intent result = new Intent();
            result.putExtra("query", q.toString());
            setResult(RESULT_OK, result);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error aplicando filtros", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Intent result = new Intent();
            result.putExtra("query", "");
            setResult(RESULT_OK, result);
            finish();
        }
    }

    private void clearFilters() {
        Intent result = new Intent();
        result.putExtra("query", "");
        setResult(RESULT_OK, result);
        finish();
    }
}
