/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;
import java.util.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import play.db.jpa.Model;

@Entity
@Table(name="Users")
public class User extends Model {
    public String email;
    public String firstName;
    public String lastName;
    public String password;
    public String userType;
    public String lastLogin;
    public String currentProject;
    public Boolean authenticated;
    public String confirmationCode;
    
    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        userType = "user";
    }
}
