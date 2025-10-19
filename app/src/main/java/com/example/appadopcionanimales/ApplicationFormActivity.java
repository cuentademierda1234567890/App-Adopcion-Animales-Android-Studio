package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApplicationFormActivity extends AppCompatActivity {
    EditText etName, etAge, etPhone, etAddress, etNotes;
    Button btnSubmit;
    int petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_form);

        // --- IMPORTANT: make sure these IDs exist in the layout (they do in the XML above)
        etName = findViewById(R.id.etApplicantName);
        etAge = findViewById(R.id.etApplicantAge);
        etPhone = findViewById(R.id.etApplicantPhone);
        etAddress = findViewById(R.id.etApplicantAddress);
        etNotes = findViewById(R.id.etNotes);
        btnSubmit = findViewById(R.id.btnSubmitApplication);

        petId = getIntent().getIntExtra("pet_id", 0);

        btnSubmit.setOnClickListener(v -> submitApplication());
    }

    private void submitApplication() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) { Toast.makeText(this, "Nombre requerido", Toast.LENGTH_SHORT).show(); return; }

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.URL_SUBMIT_SOLICITUD,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + obj.optString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) { Toast.makeText(this, "Respuesta inválida", Toast.LENGTH_SHORT).show(); }
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("animal_id", String.valueOf(petId));
                p.put("solicitante_nombre", etName.getText().toString().trim());
                p.put("solicitante_edad", etAge.getText().toString().trim());
                p.put("solicitante_telefono", etPhone.getText().toString().trim());
                p.put("solicitante_direccion", etAddress.getText().toString().trim());
                p.put("notas", etNotes.getText().toString().trim());
                return p;
            }
        };
        Volley.newRequestQueue(this).add(sr);
    }
}
