package controllers;

import models.User;
import play.mvc.*;

@With(Secure.class)
public class Projects extends Controller {
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            String email = Security.connected();
            User user = User.find("byEmail", email).first();
            renderArgs.put("user", user);
        }
    }
    public static void projects(){
        render();
    }
}
