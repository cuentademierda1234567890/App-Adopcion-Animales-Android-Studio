package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ApplicationFormActivity extends AppCompatActivity {

    EditText etApplicantName, etApplicantAge, etApplicantPhone, etApplicantAddress, etNotes;
    Button btnSubmitApplication, btnAceptarHorario;
    RadioGroup rgSlots;
    LinearLayout slotsContainer;
    int selectedSlotId = 0;
    int petId = 0;
    private static final String TAG = "ApplicationForm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_form);

        etApplicantName = findViewById(R.id.etApplicantName);
        etApplicantAge = findViewById(R.id.etApplicantAge);
        etApplicantPhone = findViewById(R.id.etApplicantPhone);
        etApplicantAddress = findViewById(R.id.etApplicantAddress);
        etNotes = findViewById(R.id.etNotes);
        btnSubmitApplication = findViewById(R.id.btnSubmitApplication);
        btnAceptarHorario = findViewById(R.id.btnAceptarHorario);
        rgSlots = findViewById(R.id.rgSlots);

        // recibir pet_id y pet_nombre si vienen
        petId = getIntent().getIntExtra("pet_id", 0);
        String petNombre = getIntent().getStringExtra("pet_nombre");
        if (petNombre != null && !petNombre.isEmpty()) {
            // opcional: mostrar nombre en el formulario (no obligatorio)
        }

        // Cargar slots desde servidor
        loadSlots();

        // Aceptar horario: solo confirma seleccion (puede ser útil para UI)
        btnAceptarHorario.setOnClickListener(v -> {
            int sel = rgSlots.getCheckedRadioButtonId();
            if (sel == -1) {
                Toast.makeText(this, "Selecciona un horario primero", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton rb = findViewById(sel);
            selectedSlotId = Integer.parseInt(rb.getTag().toString());
            Toast.makeText(this, "Horario seleccionado: " + rb.getText(), Toast.LENGTH_SHORT).show();
        });

        btnSubmitApplication.setOnClickListener(v -> submitApplication());
    }

    private void loadSlots() {
        String url = Constants.URL_GET_SLOTS;
        StringRequest sr = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (!obj.optBoolean("success", false)) {
                            Toast.makeText(this, "No se pudieron cargar horarios", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONArray slots = obj.optJSONArray("slots");
                        if (slots == null) return;

                        // llenar RadioGroup dinámicamente
                        rgSlots.removeAllViews();
                        for (int i = 0; i < slots.length(); i++) {
                            JSONObject s = slots.getJSONObject(i);
                            int id = s.optInt("id", 0);
                            String time = s.optString("slot_time", "");
                            int booked = s.optInt("booked", 0);

                            RadioButton rb = new RadioButton(this);
                            rb.setId(View.generateViewId());
                            rb.setText(time + (booked == 1 ? " (ocupado)" : ""));
                            rb.setTag(String.valueOf(id));
                            rb.setEnabled(booked == 0);
                            rgSlots.addView(rb);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando slots", e);
                        Toast.makeText(this, "Error cargando horarios", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error de red cargando slots", error);
                    Toast.makeText(this, "Error de red al cargar horarios", Toast.LENGTH_SHORT).show();
                });
        Volley.newRequestQueue(this).add(sr);
    }

    private void submitApplication() {
        String name = etApplicantName.getText().toString().trim();
        String ageS = etApplicantAge.getText().toString().trim();
        String phone = etApplicantPhone.getText().toString().trim();
        String addr = etApplicantAddress.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (name.isEmpty() || ageS.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Completa nombre, edad y teléfono", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageS);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (age < 18) {
            Toast.makeText(this, "El solicitante debe ser mayor de 18 años", Toast.LENGTH_SHORT).show();
            return;
        }

        // comprobar slot seleccionado
        int sel = rgSlots.getCheckedRadioButtonId();
        if (sel == -1) {
            Toast.makeText(this, "Selecciona un horario disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton rb = findViewById(sel);
        selectedSlotId = Integer.parseInt(rb.getTag().toString());

        String url = Constants.URL_SUBMIT_SOLICITUD;
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            Toast.makeText(this, "Solicitud enviada y horario reservado", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + obj.optString("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error de red al enviar solicitud", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("animal_id", String.valueOf(petId));
                p.put("slot_id", String.valueOf(selectedSlotId));
                p.put("solicitante_nombre", name);
                p.put("solicitante_edad", String.valueOf(age));
                p.put("solicitante_telefono", phone);
                p.put("solicitante_direccion", addr);
                p.put("notas", notes);
                // p.put("user_id", String.valueOf(userId)); // opcional si querés enviar user id
                return p;
            }
        };

        Volley.newRequestQueue(this).add(sr);
    }
}
