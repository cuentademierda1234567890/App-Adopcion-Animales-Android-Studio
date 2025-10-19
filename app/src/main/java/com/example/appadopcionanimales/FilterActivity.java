package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

        // Valores de ejemplo
        String[] tamaños = {"Todos", "Pequeño", "Mediano", "Grande"};
        String[] sexos = {"Todos", "Macho", "Hembra"};
        String[] categorias = {"Todos", "Perro", "Gato"};

        spTamano.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tamaños));
        spSexo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sexos));
        spCategoria.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias));

        // Listener para insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.filter_root), (v, insets) -> insets);

        // Aplicar filtros
        btnApply.setOnClickListener(v -> applyFilters());

        // Limpiar filtros
        btnClear.setOnClickListener(v -> clearFilters());
    }

    private void applyFilters() {
        String size = spTamano.getSelectedItem().toString();
        String sex = spSexo.getSelectedItem().toString();
        String category = spCategoria.getSelectedItem().toString();

        // Aquí envías los filtros al RecyclerView de animales
        // Por ejemplo con un Intent:
        // Intent intent = new Intent(this, AnimalsActivity.class);
        // intent.putExtra("filter_size", size);
        // intent.putExtra("filter_sex", sex);
        // intent.putExtra("filter_category", category);
        // startActivity(intent);
    }

    private void clearFilters() {
        spTamano.setSelection(0);
        spSexo.setSelection(0);
        spCategoria.setSelection(0);
    }
}
