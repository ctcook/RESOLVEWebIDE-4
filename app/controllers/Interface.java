package controllers;

import play.*;
import play.mvc.*;

import java.util.*;
import models.User;
import play.cache.Cache;

//import models.*;

public class Interface extends Controller {

    public static void index() {
        String userName = "default";
        String userSession = session.getId();
        Boolean loggedIn = Cache.get(userSession + "_status", Boolean.class);
        User user = null;
        if(loggedIn != null){
            user = Cache.get(userSession + "_user", User.class);
            /*if(loggedIn){
                user = Cache.get(userSession + "_user", User.class);
                Cache.set(userSession + "_user", user);
            }*/
        }
        else{
            Cache.set(userSession + "_status", false);
            // @todo set to default user profile
            user = User.find("byEmail", "ctcook@g.clemson.edu").first();
            Cache.set(userSession + "_user", user);
        }
        render(user);
    }

}