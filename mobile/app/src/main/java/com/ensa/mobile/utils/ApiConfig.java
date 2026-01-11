package com.ensa.mobile.utils;

public class ApiConfig {
    // API Gateway Base URL
    // Use 10.0.2.2 for Android Emulator (maps to localhost on host machine)
    // For physical device, use your computer's IP address: "http://192.168.x.x:8080/"
    public static final String API_GATEWAY_BASE_URL = "http://10.0.2.2:8080/";
    
    // Direct service URL (if needed for direct connection)
    // Use 10.0.2.2 for Android Emulator
    // For physical device, use your computer's IP address: "http://192.168.x.x:9090/"
    public static final String DIRECT_SERVICE_BASE_URL = "http://10.0.2.2:9090/";
}

