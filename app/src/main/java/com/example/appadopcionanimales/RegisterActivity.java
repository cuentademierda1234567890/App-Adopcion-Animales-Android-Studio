package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etNombre, etCorreo, etContrasena;
    Button btnRegister;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        final String nombre = etNombre.getText().toString().trim();
        final String correo = etCorreo.getText().toString().trim();
        final String contrasena = etContrasena.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Constants.URL_REGISTER;
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "Params: nombre=" + nombre + " correo=" + correo);

        StringRequest sr = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Response: " + response);
                    // Intentá parsear JSON si tu backend devuelve JSON
                    Toast.makeText(RegisterActivity.this, "Respuesta: " + response, Toast.LENGTH_LONG).show();
                },
                error -> {
                    String msg = formatVolleyError(error);
                    Log.e(TAG, msg, error);
                    Toast.makeText(RegisterActivity.this, "Error de red: " + msg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                // Estos son los nombres que espera el backend que te di: nombre, correo, contrasena
                p.put("nombre", nombre);
                p.put("correo", correo);
                p.put("contrasena", contrasena);
                return p;
            }
        };

        Volley.newRequestQueue(this).add(sr);
    }

    private String formatVolleyError(VolleyError error) {
        if (error == null) return "error desconocido";
        if (error.networkResponse != null) {
            int status = error.networkResponse.statusCode;
            String body = "";
            try {
                body = new String(error.networkResponse.data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                body = "no se pudo leer body: " + e.getMessage();
            }
            return "HTTP " + status + " - " + body;
        } else if (error.getCause() != null) {
            return "Cause: " + error.getCause().toString();
        } else {
            return error.toString();
        }
    }
}
