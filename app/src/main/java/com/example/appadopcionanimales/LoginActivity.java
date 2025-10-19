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

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Constants.URL_LOGIN;
        Log.d(TAG, "URL: " + url + " Params: email=" + email);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Response: " + response);

                    try {
                        String trimmed = response == null ? "" : response.trim();
                        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                            JSONObject obj = new JSONObject(trimmed);
                            boolean success = obj.optBoolean("success", false);
                            if (success) {
                                JSONObject user = obj.optJSONObject("user");
                                int userId = user != null ? user.optInt("id", 0) : obj.optInt("user_id", 0);
                                String nombre = user != null ? user.optString("nombre", "") : obj.optString("user_nombre", "");
                                String foto = user != null ? user.optString("foto", "") : obj.optString("user_foto", "");
                                onLoginSuccess(userId, nombre, foto);
                                return;
                            } else {
                                String msg = obj.optString("msg", "Credenciales inválidas");
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        if ("success".equalsIgnoreCase(trimmed)) {
                            onLoginSuccess(0, "", "");
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: " + trimmed, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando respuesta: " + e.getMessage(), e);
                        Toast.makeText(LoginActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String msg = formatVolleyError(error);
                    Log.e(TAG, msg, error);
                    Toast.makeText(LoginActivity.this, "Error de red: " + msg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("correo", email);
                params.put("contrasena", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void onLoginSuccess(int userId, String nombre, String fotoUrl) {
        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

        // Evitar crear múltiples instancias y limpiar historial de login
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        i.putExtra("user_id", userId);
        i.putExtra("user_nombre", nombre != null ? nombre : "");
        i.putExtra("user_foto", fotoUrl != null ? fotoUrl : "");

        // Flags: limpiar stack y evitar múltiples instancias erróneas
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        // finish() no estrictamente necesario porque CLEAR_TASK lo limpia, pero lo mantenemos
        finish();
    }

    private String formatVolleyError(VolleyError error) {
        if (error == null) return "error desconocido";
        if (error.networkResponse != null) {
            int status = error.networkResponse.statusCode;
            String body;
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
