package com.naujokaitis.maistas.mobile.utils;

public class Constants {
    // Use 10.0.2.2 for Android emulator to connect to localhost
    // Use your computer's IP address for physical device
    public static final String HOME_URL = "https://187441ff49c0.ngrok-free.app/";
    
    // Auth endpoints
    public static final String VALIDATE_USER_URL = HOME_URL + "validateUser";
    
    // Client endpoints
    public static final String ALL_CLIENTS_URL = HOME_URL + "allClients";
    public static final String INSERT_CLIENT_URL = HOME_URL + "insertClient";
    
    // Restaurant endpoints
    public static final String ALL_RESTAURANTS_URL = HOME_URL + "allRestaurants";
    public static final String RESTAURANT_URL = HOME_URL + "restaurant/";
    
    // Order endpoints
    public static final String ALL_ORDERS_URL = HOME_URL + "allOrders";
    public static final String ORDERS_BY_CLIENT_URL = HOME_URL + "orders/client/";
    public static final String ORDERS_AVAILABLE_URL = HOME_URL + "orders/available";
    public static final String INSERT_ORDER_URL = HOME_URL + "insertOrder";
    public static final String ORDER_PICKUP_URL = HOME_URL + "order/";
    
    // Chat endpoints
    public static final String CHAT_START_URL = HOME_URL + "api/chat/start/";
    public static final String CHAT_GET_BY_ORDER_URL = HOME_URL + "api/chat/order/";
    public static final String CHAT_MESSAGES_URL = HOME_URL + "api/chat/"; // + threadId + "/messages"
    public static final String CHAT_SEND_MESSAGE_URL = HOME_URL + "api/chat/"; // + threadId + "/message"

    // Review endpoints
    public static final String REVIEW_CREATE_URL = HOME_URL + "api/reviews/";
    public static final String REVIEWS_BY_RESTAURANT_URL = HOME_URL + "api/reviews/restaurant/";
    public static final String REVIEWS_BY_USER_URL = HOME_URL + "api/reviews/user/";
}
