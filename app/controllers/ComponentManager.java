package controllers;

import com.google.gson.Gson;
import models.User;
import models.UserComponent;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class ComponentManager extends Controller {
    public static void update(String job, String target){
        if(Security.isConnected()) {
            String email = Security.connected();
            User user = User.find("byEmail", email).first();
            if(job.compareTo("create") == 0){
                UserComponent uc = new Gson().fromJson(target, UserComponent.class);
                uc.author = user;
                uc.setCreatedDate();
                uc._save();
            }
            else{
                
            }
        }
            
    }
}
