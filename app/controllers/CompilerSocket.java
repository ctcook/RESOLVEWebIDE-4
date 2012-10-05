package controllers;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.UserComponent;
import play.mvc.WebSocketController;

/**
 *
 * @author Chuck
 */
public class CompilerSocket extends WebSocketController {
    public static void hello(String target) {
        UserComponent uc = new Gson().fromJson(target, UserComponent.class);
        //System.out.println(decode(uc.content));
        outbound.send(uc.name);
    }
    
    private static String decode(String raw){
        String encoded = null;
        try {
            encoded = URLDecoder.decode(raw.replaceAll("%20", " "), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encoded;
    }
}
