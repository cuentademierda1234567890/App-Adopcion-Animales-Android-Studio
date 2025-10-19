package com.example.appadopcionanimales;

public final class Constants {
    // Cambiá la IP por la IP de tu PC en la red local (por ejemplo "http://192.168.1.100/android-api/")
    public static final String BASE_URL = "http://10.0.2.2/android-api/";

    public static final String URL_REGISTER = BASE_URL + "register.php";
    public static final String URL_LOGIN = BASE_URL + "login.php";
    public static final String URL_GET_ANIMALES = BASE_URL + "get_animales.php";
    public static final String URL_GET_ANIMAL = BASE_URL + "get_animal.php";
    public static final String URL_SUBMIT_SOLICITUD = BASE_URL + "submit_solicitud.php";

    // constructor privado para evitar instanciación
    private Constants() {}
}
