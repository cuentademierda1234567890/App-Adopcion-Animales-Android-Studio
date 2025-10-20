package com.example.appadopcionanimales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText etNombre, etApellido, etEdad, etTelefono, etDireccion, etCorreo, etContrasena;
    Button btnRegister;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // referencias a los views (coinciden con tu activity_register.xml)
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etEdad = findViewById(R.id.etEdad);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        final String nombre = etNombre.getText().toString().trim();
        final String apellido = etApellido.getText().toString().trim();
        final String edadStr = etEdad.getText().toString().trim();
        final String telefono = etTelefono.getText().toString().trim();
        final String direccion = etDireccion.getText().toString().trim();
        final String correo = etCorreo.getText().toString().trim();
        final String contrasena = etContrasena.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || edadStr.isEmpty() ||
                telefono.isEmpty() || direccion.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edad < 18) {
            Toast.makeText(this, "Debes ser mayor de 18 años para registrarte", Toast.LENGTH_LONG).show();
            return;
        }

        String url = Constants.URL_REGISTER; // usa tu Constants (http://10.0.2.2/android-api/register.php)

        Log.d(TAG, "Registrando usuario en: " + url + " nombre=" + nombre + " correo=" + correo);

        StringRequest sr = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Response register: " + response);
                    try {
                        String trimmed = response.trim();
                        // intentamos parsear JSON
                        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                            JSONObject obj = new JSONObject(trimmed);
                            boolean ok = obj.optBoolean("success", false);
                            String msg = obj.optString("msg", obj.optString("message", ""));
                            if (ok) {
                                Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                // redirigir automáticamente al MainActivity
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                if (msg == null || msg.isEmpty()) msg = "Error al registrar";
                                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                            return;
                        }

                        // si no es JSON, aceptamos respuesta textual "success"
                        if (trimmed.equalsIgnoreCase("success")) {
                            Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            // mostrar texto crudo como mensaje
                            Toast.makeText(RegisterActivity.this, "Server: " + trimmed, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando respuesta: " + e.getMessage(), e);
                        Toast.makeText(RegisterActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String msg = formatVolleyError(error);
                    Log.e(TAG, "Volley error: " + msg, error);
                    Toast.makeText(RegisterActivity.this, "Error de red: " + msg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                // claves que espera tu backend: nombre, apellido, edad, telefono, direccion, correo, contrasena
                p.put("nombre", nombre);
                p.put("apellido", apellido);
                p.put("edad", String.valueOf(edad));
                p.put("telefono", telefono);
                p.put("direccion", direccion);
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
            String m = error.getMessage();
            return m != null ? m : error.toString();
        }
    }
}
