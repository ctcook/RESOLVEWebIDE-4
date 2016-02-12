package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import play.db.jpa.Model;

@Entity
@Table(name="byDesignEvents")
public class ByDesignEvent extends Model {
	
    public long author;
    public String module;
    public String lesson;
    public long time;
    @Lob
    public String code;
	public boolean correct;
	public long points;
    
    public ByDesignEvent(long author, String module, String lesson, long time,
                        String code, boolean correct, long points){
        this.author = author;
        this.module = module;
        this.lesson = lesson;
        this.time = time;
        this.code = code;
        this.correct = correct;
		this.points = points;
    }
}
