package controllers;

import models.*;

public class Security extends Secure.Security {
    static boolean authenticate(String email, String password) {
        return User.connect(email, password) != null;
    }
    
    static boolean check(String profile) {
        String userLevel = User.find("byEmail", connected()).<User>first().userType;
        if(userLevel.equals(profile)) {
            return true;
        }
        return false;
}
    
    static void onDisconnected() {
        Interface.index();
    }
    
}
