package com.example.appadopcionanimales;

public final class Constants {
    // Si ejecutás en el emulador estándar de Android Studio usar:
    // 10.0.2.2 mapea al host (tu PC)
    public static final String BASE_URL = "http://10.0.2.2/android-api/";
//    public static final String BASE_URL = "http://192.168.0.104/android-api/";

    public static final String URL_REGISTER = BASE_URL + "register.php";
    public static final String URL_LOGIN = BASE_URL + "login.php";
    public static final String URL_GET_ANIMALES = BASE_URL + "get_animales.php";
    public static final String URL_GET_ANIMAL = BASE_URL + "get_animal.php";
    public static final String URL_SUBMIT_SOLICITUD = BASE_URL + "submit_solicitud.php";

    private Constants() {}
}
