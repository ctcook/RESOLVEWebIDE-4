package models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Table;
import play.db.jpa.Model;

/**
 *
 * @author Chuck
 */
@Entity
@Table(name="Projects")
public class Project extends Model {
    public String name;
    public String ownerEmail;
    public String jsonRep;
    public String jsonRepDate;
    public Boolean openProject;
    public Boolean standardHidden;
    public Boolean defaultProject;
    
    public Project(String name, String ownerEmail, Boolean openProject,
                    Boolean standardHidden){
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.jsonRep = "";
        this.jsonRepDate = "";
        this.openProject = openProject;
        this.standardHidden = standardHidden;
        this.defaultProject = false;
    }
    
    public static List<Project> getOpenProjects(){
        return find("byOpenProject", true).fetch();
    }
    
    public static List<Project> getUserProjects(String email){
        return find("byOwnerEmail", email).fetch();
    }
    
    public static Project getDefault(){
        return find("byDefaultProject", true).first();
    }
}
