package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApplicationFormActivity extends AppCompatActivity {

    TextView tvPetName, tvVisitAddress;
    RadioGroup rgSlots;
    Button btnSubmit;
    EditText etNotes;
    int petId = 0;
    int userId = 0;
    int selectedSlotId = 0;
    private static final String TAG = "ApplicationForm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_form);

        tvPetName = findViewById(R.id.tvPetNameSimple);
        tvVisitAddress = findViewById(R.id.tvVisitAddress);
        rgSlots = findViewById(R.id.rgSlots);
        btnSubmit = findViewById(R.id.btnSubmitApplication);
        etNotes = findViewById(R.id.etNotes);

        petId = getIntent().getIntExtra("pet_id", 0);
        String petNombre = getIntent().getStringExtra("pet_nombre");
        userId = getIntent().getIntExtra("user_id", 0);
        String visitAddress = getIntent().getStringExtra("visit_address");

        if (petNombre != null) tvPetName.setText(petNombre);
        if (visitAddress == null || visitAddress.trim().isEmpty()) visitAddress = "Refugio - Dirección: Calle Falsa 123";
        tvVisitAddress.setText(visitAddress);

        if (userId <= 0) {
            Toast.makeText(this, "Necesitás iniciar sesión para solicitar adopción", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadSlots();

        btnSubmit.setOnClickListener(v -> {
            int sel = rgSlots.getCheckedRadioButtonId();
            if (sel == -1) {
                Toast.makeText(ApplicationFormActivity.this, "Seleccioná un horario", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton rb = findViewById(sel);
            try {
                selectedSlotId = Integer.parseInt(rb.getTag().toString());
            } catch (Exception e) {
                Toast.makeText(ApplicationFormActivity.this, "Horario inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            sendSolicitud();
        });
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

                        rgSlots.removeAllViews();
                        for (int i = 0; i < slots.length(); i++) {
                            JSONObject s = slots.getJSONObject(i);
                            int id = s.optInt("id", 0);
                            String time = s.optString("slot_time", "");
                            boolean disponible = s.optBoolean("disponible", true);
                            int booked = s.optInt("booked", 0);

                            // Solo agregar slots disponibles y no booked
                            if (!disponible || booked == 1) continue;

                            RadioButton rb = new RadioButton(this);
                            rb.setId(android.view.View.generateViewId());
                            rb.setText(time);
                            rb.setTag(String.valueOf(id));
                            rgSlots.addView(rb);
                        }

                        if (rgSlots.getChildCount() == 0) {
                            Toast.makeText(this, "No hay horarios disponibles", Toast.LENGTH_LONG).show();
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

    private void sendSolicitud() {
        String notas = etNotes.getText().toString().trim(); // opcional

        String url = Constants.URL_SUBMIT_SOLICITUD;
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            Toast.makeText(ApplicationFormActivity.this, "Solicitud programada con éxito", Toast.LENGTH_LONG).show();
                            // Volver al HomeActivity
                            Intent i = new Intent(ApplicationFormActivity.this, HomeActivity.class);
                            i.putExtra("user_id", userId);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            // mostrar mensaje del servidor y recargar slots (por si cambió disponibilidad)
                            String msg = obj.optString("msg", "No se pudo crear solicitud");
                            Toast.makeText(ApplicationFormActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                            loadSlots();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Respuesta inválida", e);
                        Toast.makeText(ApplicationFormActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error red enviar solicitud", error);
                    Toast.makeText(ApplicationFormActivity.this, "Error de red al enviar solicitud", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("animal_id", String.valueOf(petId));
                p.put("horario_id", String.valueOf(selectedSlotId));
                p.put("usuario_id", String.valueOf(userId));
                p.put("notas", notas); // puede quedar vacío
                return p;
            }
        };

        Volley.newRequestQueue(this).add(sr);
    }
}
