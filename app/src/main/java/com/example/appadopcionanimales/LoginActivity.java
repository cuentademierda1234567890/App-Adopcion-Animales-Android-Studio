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
                    Toast.makeText(LoginActivity.this, "Respuesta: " + response, Toast.LENGTH_LONG).show();
                },
                error -> {
                    String msg = formatVolleyError(error);
                    Log.e(TAG, msg, error);
                    Toast.makeText(LoginActivity.this, "Error de red: " + msg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                // El backend acepta 'email' o 'correo' y 'password' o 'contrasena'
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
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
