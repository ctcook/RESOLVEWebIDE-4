package controllers;

import models.*;
import play.mvc.Controller;
import play.data.validation.*;

public class Registration extends Controller {
    
    public static void index() {
        render();
    }
    
    public static void handleSubmit(String email, String emailConfirm,
            String password, String passwordConfirm,
            String firstName, String lastName) {
        
        // Validation Rules for User Email
        validation.required(email);
        validation.email(email);
        validation.required(emailConfirm);
        validation.equals(emailConfirm, email);
        
        // Validation Rules for Password
        validation.required(password);
        validation.minSize(password, 6);
        validation.required(passwordConfirm);
        validation.equals(passwordConfirm, password);
        
        // Validation Rules for Name
        validation.required(firstName);
        validation.required(lastName);
        
        // Handle Errors
        if (validation.hasErrors()) {
            render("@index");
        }
        
        // Render Success Page
        render(email, firstName, lastName);
        //User user = new User(email, password, firstName, lastName);
    }
}
