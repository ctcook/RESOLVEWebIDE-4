package controllers;

import play.*;
import play.mvc.*;

import java.util.*;
import models.Project;
import models.User;
import play.cache.Cache;

//import models.*;

//@With(Secure.class)
public class Interface extends Controller {
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            String email = Security.connected();
            User user = User.find("byEmail", email).first();
            renderArgs.put("user", user);
        }
    }

    public static void index() {
        /*String userName = "default";
        String userSession = session.getId();
        Boolean loggedIn = Cache.get(userSession + "_status", Boolean.class);
        User user = null;
        if(loggedIn != null){
            user = Cache.get(userSession + "_user", User.class);
            // --if(loggedIn){
                // --user = Cache.get(userSession + "_user", User.class);
                // --Cache.set(userSession + "_user", user);
            // --}
        }
        else{
            Cache.set(userSession + "_status", false);
            // @todo set to default user profile
            user = User.find("byEmail", "ctcook@g.clemson.edu").first();
            Cache.set(userSession + "_user", user);
        }*/
        List<Project> projects = Project.getOpenProjects();
        if(Security.isConnected()) {
            String email = Security.connected();
            projects.addAll(Project.getUserProjects(email));
        }
        renderArgs.put("projects", projects);
        renderArgs.put("defaultProject", Project.getDefault());
        render();
    }

}