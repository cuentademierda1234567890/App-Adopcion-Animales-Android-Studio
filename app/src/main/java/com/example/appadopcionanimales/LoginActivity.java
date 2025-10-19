package com.example.appadopcionanimales;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Constants.BASE_URL + "login.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("\"success\":true")) {
                        Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                    } else {
                        Toast.makeText(this, "Error del servidor: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("VolleyErr", "Volley onErrorResponse llamado", error);

                    StringBuilder info = new StringBuilder();
                    if (error.networkResponse != null) {
                        info.append("HTTP status code: ").append(error.networkResponse.statusCode).append("\n");
                        if (error.networkResponse.data != null) {
                            try {
                                String body = new String(error.networkResponse.data, "UTF-8");
                                info.append("Response body: ").append(body).append("\n");
                            } catch (Exception e) {
                                info.append("Response body: <no-parseable>\n");
                            }
                        }
                    } else {
                        info.append("networkResponse es null\n");
                    }

                    if (error.getCause() != null) info.append("Cause: ").append(error.getCause()).append("\n");
                    if (error.getMessage() != null) info.append("Message: ").append(error.getMessage()).append("\n");

                    Log.e("VolleyErr", info.toString());
                    Toast.makeText(this, "Error de red (revisa Logcat 'VolleyErr')", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        queue.add(request);
    }
}
