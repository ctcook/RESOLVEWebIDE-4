package models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.Table;
import play.db.jpa.Model;

@Entity
@Table(name="userComponents")
public class UserComponent extends Model {
    public String name;
    public String pkg;
    public String project;
    public String type;
    public String parent;
    public Date createdAt;
    
    @Lob
    public String content;
    
    @ManyToOne
    public User author;
    
    public UserComponent(String name, String pkg, String project, String content, String type){
        this.name = name;
        this.pkg = pkg;
        this.project = project;
        this.content = content;
        this.type = type;
        this.author = null;
        this.createdAt = new Date();
    }
    
    public void setCreatedDate(){
        this.createdAt = new Date();
    }
}
