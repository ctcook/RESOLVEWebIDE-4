package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="compilerResults")
public class CompilerResult extends Model {
    public String name;
    public String pkg;
    public String project;
    public String fileType;
    public String eventType;
    //@Lob
    public String content;
    public String results;
    public int error;
    public Date eventDate;
    @ManyToOne
    public User author;
    public String parent;

    public CompilerResult(String name, String pkg, String project, String fileType, String eventType,
                          String content, String results, int error, User author, String parent){
        this.name = name;
        this.pkg = pkg;
        this.project = project;
        this.fileType = fileType;
        this.eventType = eventType;
        this.content = content;
        this.results = results;
        this.error = error;
        this.eventDate = new Date();
        this.author = author;
        this.parent = parent;
    }
}