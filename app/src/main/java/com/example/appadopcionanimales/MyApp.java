package com.example.appadopcionanimales;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                String stack = sw.toString();

                try {
                    File f = new File(getExternalFilesDir(null), "crash_log.txt");
                    FileOutputStream fos = new FileOutputStream(f, true);
                    String header = "----- CRASH: " + System.currentTimeMillis() + " -----\n";
                    fos.write(header.getBytes());
                    fos.write(stack.getBytes());
                    fos.write("\n\n".getBytes());
                    fos.close();
                } catch (Exception e) {
                    // ignore
                }

                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplicationContext(), "App crash capturado. Revisa crash_log.txt en Archivos de la app.", Toast.LENGTH_LONG).show()
                );

                Thread.sleep(1500);
            } catch (Exception ignored) {}

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(2);
        });
    }
}
